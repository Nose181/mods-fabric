package com.temporizador.client;

import com.temporizador.net.TemporizadorPayload;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.util.Locale;

/**
 * Temporizador lo mas simple posible: solo el numero (y el texto opcional arriba), arriba al centro.
 * Sin caja, sin barra, sin animaciones y sin sonidos. No bloquea el juego.
 */
public class TemporizadorOverlay {

    /** Tamaño del numero. Sube o baja este valor para cambiarlo (1,0 = tamaño normal del texto de Minecraft). */
    private static final float ESC = 1.25f;

    private static final int NADA = 0;
    private static final int CORRIENDO = 1;
    private static final int PAUSADO = 2;
    private static final int TERMINADO = 3;

    private static int estado = NADA;
    private static long finMs = 0L;
    private static long restantePausa = 0L;
    private static long terminadoDesde = 0L;
    private static String texto = "";

    public static void recibir(int accion, long restanteMs, long total, String t) {
        if (accion == TemporizadorPayload.PARAR) {
            estado = NADA;
            return;
        }
        texto = t;
        if (accion == TemporizadorPayload.PAUSA) {
            estado = PAUSADO;
            restantePausa = restanteMs;
        } else {
            estado = CORRIENDO;
            finMs = Util.getMillis() + restanteMs;
        }
    }

    public static void render(GuiGraphics g, int w, int h) {
        if (estado == NADA) return;
        long ahora = Util.getMillis();
        Font font = Minecraft.getInstance().font;

        long restante;
        if (estado == CORRIENDO) {
            restante = Math.max(0L, finMs - ahora);
        } else if (estado == PAUSADO) {
            restante = restantePausa;
        } else {
            restante = 0L;
        }

        // Al llegar a cero se queda 3 segundos en 00:00 y se va (el chat avisa que se acabo el tiempo)
        if (estado == CORRIENDO && restante <= 0L) {
            estado = TERMINADO;
            terminadoDesde = ahora;
        }
        if (estado == TERMINADO && ahora - terminadoDesde > 3000L) {
            estado = NADA;
            return;
        }

        float cx = w / 2f;
        float y = 6f;

        String etiqueta = colorear(texto);
        if (!etiqueta.isEmpty()) {
            g.pose().pushPose();
            g.pose().translate(cx, y, 0);
            g.pose().scale(0.85f, 0.85f, 1f);
            g.drawCenteredString(font, Component.literal(etiqueta), 0, 0, 0xFFFFFFFF);
            g.pose().popPose();
            y += 10f;
        }

        String tiempoTxt = formatear(restante);
        int color = estado == PAUSADO ? 0xFFAAAAAA : 0xFFFFFFFF;
        g.pose().pushPose();
        g.pose().translate(cx, y, 0);
        g.pose().scale(ESC, ESC, 1f);
        g.drawString(font, tiempoTxt, -font.width(tiempoTxt) / 2, 0, color, true);
        g.pose().popPose();
    }

    private static String formatear(long ms) {
        long s = (ms + 999L) / 1000L;
        long hh = s / 3600L;
        long mm = (s % 3600L) / 60L;
        long ss = s % 60L;
        return hh > 0
                ? String.format(Locale.ROOT, "%d:%02d:%02d", hh, mm, ss)
                : String.format(Locale.ROOT, "%02d:%02d", mm, ss);
    }

    private static String colorear(String s) {
        return s == null ? "" : s.replaceAll("&([0-9a-fA-Fk-oK-OrR])", "\u00A7$1");
    }
}
