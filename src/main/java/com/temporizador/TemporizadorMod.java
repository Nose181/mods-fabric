package com.temporizador;

import com.temporizador.net.TemporizadorPayload;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public class TemporizadorMod implements ModInitializer {
    public static final String MODID = "temporizador";

    @Override
    public void onInitialize() {
        PayloadTypeRegistry.playS2C().register(TemporizadorPayload.TYPE, TemporizadorPayload.CODEC);
        Servidor.registrar();
    }
}
