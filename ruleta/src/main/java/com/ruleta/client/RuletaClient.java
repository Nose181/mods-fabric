package com.ruleta.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.ruleta.net.AvisoPayload;
import com.ruleta.net.GirarPayload;
import com.ruleta.net.MuertePayload;
import com.ruleta.net.PanelPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

/** Parte del cliente: recibe los paquetes del servidor, dibuja las capas y maneja la tecla de ocultar. */
public class RuletaClient implements ClientModInitializer {

    /** Tecla para ocultar el cuadro de aviso y saltar la ruleta (se puede cambiar en Controles). */
    public static final KeyMapping OCULTAR = new KeyMapping(
            "key.ruleta.ocultar", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_I, "key.categories.ruleta");

    @Override
    public void onInitializeClient() {
        KeyBindingHelper.registerKeyBinding(OCULTAR);

        ClientPlayNetworking.registerGlobalReceiver(GirarPayload.TYPE,
                (p, contexto) -> ClientHooks.abrirRuleta(p.colores, p.indice, p.texto));
        ClientPlayNetworking.registerGlobalReceiver(MuertePayload.TYPE,
                (p, contexto) -> ClientHooks.mostrarMuerte(p.nombres, p.castigo));
        ClientPlayNetworking.registerGlobalReceiver(AvisoPayload.TYPE,
                (p, contexto) -> ClientHooks.recibirAviso(p.titulo, p.mensaje, p.rgb, p.segundos, p.pideCumplido, p.limpiar));
        ClientPlayNetworking.registerGlobalReceiver(PanelPayload.TYPE,
                (p, contexto) -> ClientHooks.abrirPanel(p.retos, p.seleccionado));

        // Las capas se dibujan encima del juego (no bloquean nada)
        HudRenderCallback.EVENT.register((g, delta) -> {
            int w = g.guiWidth();
            int h = g.guiHeight();
            RuletaOverlay.render(g, w, h);
            AvisoOverlay.render(g, w, h);
            MuerteOverlay.render(g, w, h);
        });

        ClientTickEvents.END_CLIENT_TICK.register(cliente -> {
            while (OCULTAR.consumeClick()) {
                AvisoOverlay.ocultar();
                RuletaOverlay.saltar();
            }
        });
    }
}
