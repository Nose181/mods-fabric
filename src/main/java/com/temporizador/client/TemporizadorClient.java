package com.temporizador.client;

import com.temporizador.net.TemporizadorPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

/** Parte del cliente: recibe el estado del temporizador y dibuja el reloj arriba al centro. */
public class TemporizadorClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(TemporizadorPayload.TYPE,
                (p, contexto) -> TemporizadorOverlay.recibir(p.accion, p.restanteMs, p.totalMs, p.texto));

        HudRenderCallback.EVENT.register((g, delta) ->
                TemporizadorOverlay.render(g, g.guiWidth(), g.guiHeight()));
    }
}
