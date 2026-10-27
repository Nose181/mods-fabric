package com.mochilas.net;

import com.mochilas.MochilasMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/** Cliente -> servidor: "abre mi mochila" (no lleva datos). */
public class AbrirMochilaPayload implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<AbrirMochilaPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(MochilasMod.MODID, "abrir"));
    public static final StreamCodec<RegistryFriendlyByteBuf, AbrirMochilaPayload> CODEC =
            StreamCodec.unit(new AbrirMochilaPayload());

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
