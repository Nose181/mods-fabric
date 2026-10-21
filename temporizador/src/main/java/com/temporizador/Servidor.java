package com.temporizador;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.temporizador.net.Red;
import com.temporizador.net.TemporizadorPayload;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * /temporizador <tiempo> [texto]   inicia una cuenta regresiva para todos (ej: 90, 5m, 1h30m, 2:30)
 * /temporizador pausar | seguir | parar | sumar <tiempo>
 * Solo operadores (o el dueño del mundo en un mundo propio). El tiempo corre en el servidor.
 */
public class Servidor {

    private enum Estado { NINGUNO, CORRIENDO, PAUSADO }

    /** El servidor en marcha (se guarda al iniciar y se borra al detenerse). */
    private static MinecraftServer servidor = null;

    private static final long MAX_MS = 24L * 3600L * 1000L;
    private static final Pattern COMPUESTO = Pattern.compile("^(?:(\\d+)h)?(?:(\\d+)m)?(?:(\\d+)s)?$");
    private static final Pattern RELOJ = Pattern.compile("^(?:(\\d+):)?(\\d+):(\\d{1,2})$");

    private static Estado estado = Estado.NINGUNO;
    private static long finMs = 0L;       // momento (reloj del servidor) en que termina
    private static long restanteMs = 0L;  // solo se usa en pausa
    private static long totalMs = 0L;
    private static String texto = "";

    // ------------------------------------------------------------ comandos
    /** Se llama una vez al iniciar el mod. */
    public static void registrar() {
        ServerLifecycleEvents.SERVER_STARTED.register(srv -> servidor = srv);
        ServerLifecycleEvents.SERVER_STOPPED.register(srv -> {
            estado = Estado.NINGUNO;
            servidor = null;
        });
        ServerTickEvents.END_SERVER_TICK.register(Servidor::tick);
        ServerPlayConnectionEvents.JOIN.register((manejador, emisor, srv) -> alEntrar(manejador.getPlayer()));
        CommandRegistrationCallback.EVENT.register((dispatcher, acceso, entorno) -> comandos(dispatcher));
    }

    private static void comandos(CommandDispatcher<CommandSourceStack> d) {
        List<String> sugerencias = List.of("30", "1m", "5m", "10m");

        LiteralCommandNode<CommandSourceStack> nodo = d.register(Commands.literal("temporizador")
                .requires(Servidor::puedeAdministrar)
                .executes(ctx -> ayuda(ctx.getSource()))
                .then(Commands.literal("pausar").executes(ctx -> pausar(ctx.getSource())))
                .then(Commands.literal("seguir").executes(ctx -> seguir(ctx.getSource())))
                .then(Commands.literal("parar").executes(ctx -> parar(ctx.getSource())))
                .then(Commands.literal("sumar")
                        .then(Commands.argument("tiempo", StringArgumentType.word())
                                .suggests((ctx, b) -> SharedSuggestionProvider.suggest(sugerencias, b))
                                .executes(ctx -> sumar(ctx.getSource(),
                                        StringArgumentType.getString(ctx, "tiempo")))))
                .then(Commands.argument("tiempo", StringArgumentType.word())
                        .suggests((ctx, b) -> SharedSuggestionProvider.suggest(sugerencias, b))
                        .executes(ctx -> iniciar(ctx.getSource(),
                                StringArgumentType.getString(ctx, "tiempo"), ""))
                        .then(Commands.argument("texto", StringArgumentType.greedyString())
                                .executes(ctx -> iniciar(ctx.getSource(),
                                        StringArgumentType.getString(ctx, "tiempo"),
                                        StringArgumentType.getString(ctx, "texto"))))));

        // Atajo: /timer hace lo mismo
        d.register(Commands.literal("timer")
                .requires(Servidor::puedeAdministrar)
                .redirect(nodo));
    }

    private static boolean puedeAdministrar(CommandSourceStack src) {
        if (src.hasPermission(2)) return true;
        return src.getEntity() instanceof ServerPlayer sp
                && src.getServer().isSingleplayerOwner(sp.getGameProfile());
    }

    private static int ayuda(CommandSourceStack src) {
        src.sendSuccess(() -> Component.literal(
                "§6[Temporizador] §fUso:\n"
                        + "§e/temporizador <tiempo> [texto] §7inicia la cuenta (ej: 90, 5m, 1h30m, 2:30)\n"
                        + "§e/temporizador pausar §7| §eseguir §7| §eparar\n"
                        + "§e/temporizador sumar <tiempo> §7agrega tiempo"), false);
        return 1;
    }

    // ------------------------------------------------------------ acciones
    private static int iniciar(CommandSourceStack src, String tiempo, String txt) {
        long ms = parsear(tiempo);
        if (ms < 0) {
            src.sendFailure(Component.literal("Tiempo no válido. Ejemplos: 90, 5m, 1h30m, 2:30 (máximo 24 horas)."));
            return 0;
        }
        texto = limpiarTexto(txt);
        totalMs = ms;
        finMs = System.currentTimeMillis() + ms;
        estado = Estado.CORRIENDO;
        difundir();
        MinecraftServer server = src.getServer();
        server.getPlayerList().broadcastSystemMessage(Component.literal(
                "§6[Temporizador] §fIniciado: §e" + formatear(ms)
                        + (texto.isEmpty() ? "" : " §f- " + colorear(texto) + "§r")), false);
        return 1;
    }

    private static int pausar(CommandSourceStack src) {
        if (estado != Estado.CORRIENDO) {
            src.sendFailure(Component.literal("No hay un temporizador corriendo."));
            return 0;
        }
        restanteMs = Math.max(0L, finMs - System.currentTimeMillis());
        estado = Estado.PAUSADO;
        difundir();
        src.getServer().getPlayerList().broadcastSystemMessage(Component.literal(
                "§6[Temporizador] §fEn pausa: §e" + formatear(restanteMs)), false);
        return 1;
    }

    private static int seguir(CommandSourceStack src) {
        if (estado != Estado.PAUSADO) {
            src.sendFailure(Component.literal("El temporizador no está en pausa."));
            return 0;
        }
        finMs = System.currentTimeMillis() + restanteMs;
        estado = Estado.CORRIENDO;
        difundir();
        src.getServer().getPlayerList().broadcastSystemMessage(Component.literal(
                "§6[Temporizador] §fContinúa: §e" + formatear(restanteMs)), false);
        return 1;
    }

    private static int parar(CommandSourceStack src) {
        if (estado == Estado.NINGUNO) {
            src.sendFailure(Component.literal("No hay ningún temporizador."));
            return 0;
        }
        estado = Estado.NINGUNO;
        Red.todos(servidor, new TemporizadorPayload(TemporizadorPayload.PARAR, 0L, 0L, ""));
        src.getServer().getPlayerList().broadcastSystemMessage(
                Component.literal("§6[Temporizador] §fDetenido."), false);
        return 1;
    }

    private static int sumar(CommandSourceStack src, String tiempo) {
        if (estado == Estado.NINGUNO) {
            src.sendFailure(Component.literal("No hay ningún temporizador."));
            return 0;
        }
        long ms = parsear(tiempo);
        if (ms < 0) {
            src.sendFailure(Component.literal("Tiempo no válido. Ejemplos: 30, 5m, 1h30m, 2:30."));
            return 0;
        }
        long actual = restanteActual();
        if (actual + ms > MAX_MS) {
            src.sendFailure(Component.literal("El máximo es 24 horas."));
            return 0;
        }
        if (estado == Estado.CORRIENDO) {
            finMs += ms;
        } else {
            restanteMs += ms;
        }
        totalMs += ms;
        difundir();
        src.getServer().getPlayerList().broadcastSystemMessage(Component.literal(
                "§6[Temporizador] §f+" + formatear(ms) + " §7(quedan " + formatear(restanteActual()) + ")"), false);
        return 1;
    }

    // ------------------------------------------------------------ reloj
    private static void tick(MinecraftServer server) {
        if (estado != Estado.CORRIENDO) return;
        if (System.currentTimeMillis() < finMs) return;

        // Se acabo. Los clientes ya muestran "¡TIEMPO!" al llegar a 0 por su cuenta.
        String t = texto;
        estado = Estado.NINGUNO;
        if (server != null) {
            server.getPlayerList().broadcastSystemMessage(Component.literal(
                    "§6[Temporizador] §a¡Se acabó el tiempo!" + (t.isEmpty() ? "" : " §f" + colorear(t) + "§r")), false);
        }
    }

    /** Si alguien entra con el temporizador en marcha, se le manda el estado actual. */
    private static void alEntrar(ServerPlayer jugador) {
        if (estado == Estado.NINGUNO) return;
        Red.a(jugador, paquete());
    }


    // ------------------------------------------------------------ utilidades
    private static long restanteActual() {
        return estado == Estado.CORRIENDO ? Math.max(0L, finMs - System.currentTimeMillis()) : restanteMs;
    }

    private static TemporizadorPayload paquete() {
        int accion = estado == Estado.CORRIENDO ? TemporizadorPayload.CORRER : TemporizadorPayload.PAUSA;
        return new TemporizadorPayload(accion, restanteActual(), totalMs, texto);
    }

    private static void difundir() {
        Net.CHANNEL.send(paquete());
    }

    /** Acepta 90 (segundos), 5m, 1h30m, 45s, 2:30 (min:seg) y 1:02:03 (h:min:seg). Devuelve -1 si no es valido. */
    static long parsear(String entrada) {
        String t = entrada.trim().toLowerCase(Locale.ROOT);
        if (t.isEmpty()) return -1L;
        long seg;
        try {
            if (t.matches("\\d+")) {
                seg = Long.parseLong(t);
            } else {
                Matcher r = RELOJ.matcher(t);
                if (r.matches()) {
                    long h = r.group(1) == null ? 0L : Long.parseLong(r.group(1));
                    seg = h * 3600L + Long.parseLong(r.group(2)) * 60L + Long.parseLong(r.group(3));
                } else {
                    Matcher c = COMPUESTO.matcher(t);
                    if (!c.matches()) return -1L;
                    long h = c.group(1) == null ? 0L : Long.parseLong(c.group(1));
                    long m = c.group(2) == null ? 0L : Long.parseLong(c.group(2));
                    long s = c.group(3) == null ? 0L : Long.parseLong(c.group(3));
                    seg = h * 3600L + m * 60L + s;
                }
            }
        } catch (NumberFormatException e) {
            return -1L;
        }
        long ms = seg * 1000L;
        return (seg > 0 && ms <= MAX_MS) ? ms : -1L;
    }

    static String formatear(long ms) {
        long s = (ms + 999L) / 1000L;
        long h = s / 3600L;
        long m = (s % 3600L) / 60L;
        long seg = s % 60L;
        return h > 0
                ? String.format(Locale.ROOT, "%d:%02d:%02d", h, m, seg)
                : String.format(Locale.ROOT, "%02d:%02d", m, seg);
    }

    private static String limpiarTexto(String s) {
        String t = s == null ? "" : s.replace("\n", " ").strip();
        return t.length() > 80 ? t.substring(0, 80) : t;
    }

    private static String colorear(String s) {
        return s.replaceAll("&([0-9a-fA-Fk-oK-OrR])", "\u00A7$1");
    }
}
