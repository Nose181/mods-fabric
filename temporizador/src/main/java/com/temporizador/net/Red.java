package com.temporizador.net;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

/** Envio de paquetes del servidor a los jugadores (solo a los que tienen el mod). */
public class Red {

    public static void todos(MinecraftServer servidor, CustomPacketPayload carga) {
        if (servidor == null) return;
        for (ServerPlayer jugador : servidor.getPlayerList().getPlayers()) {
            a(jugador, carga);
        }
    }

    public static void a(ServerPlayer jugador, CustomPacketPayload carga) {
        if (ServerPlayNetworking.canSend(jugador, carga.type())) {
            ServerPlayNetworking.send(jugador, carga);
        }
    }
}
