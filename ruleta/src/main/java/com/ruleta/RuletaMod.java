package com.ruleta;

import com.ruleta.net.AvisoPayload;
import com.ruleta.net.GirarPayload;
import com.ruleta.net.MuertePayload;
import com.ruleta.net.PanelAccionPayload;
import com.ruleta.net.PanelPayload;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class RuletaMod implements ModInitializer {
    public static final String MODID = "ruleta";

    @Override
    public void onInitialize() {
        // Paquetes servidor -> cliente
        PayloadTypeRegistry.playS2C().register(GirarPayload.TYPE, GirarPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(MuertePayload.TYPE, MuertePayload.CODEC);
        PayloadTypeRegistry.playS2C().register(AvisoPayload.TYPE, AvisoPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(PanelPayload.TYPE, PanelPayload.CODEC);

        // Paquete cliente -> servidor (acciones del panel de control)
        PayloadTypeRegistry.playC2S().register(PanelAccionPayload.TYPE, PanelAccionPayload.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(PanelAccionPayload.TYPE,
                (payload, contexto) -> RuletaServer.panelAccion(contexto.player(), payload));

        RuletaServer.registrar();
    }
}
