package com.sacos.net;

import com.sacos.SacosMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/** Cliente -> servidor: "abre mi saco" (no lleva datos). */
public class AbrirSacoPayload implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<AbrirSacoPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(SacosMod.MODID, "abrir"));
    public static final StreamCodec<RegistryFriendlyByteBuf, AbrirSacoPayload> CODEC =
            StreamCodec.unit(new AbrirSacoPayload());

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
