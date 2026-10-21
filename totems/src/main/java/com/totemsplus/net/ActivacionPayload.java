package com.totemsplus.net;

import com.totemsplus.TotemsMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/** Servidor -> jugador: muestra en su pantalla el tótem que se acaba de usar (la animacion de siempre). */
public class ActivacionPayload implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ActivacionPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(TotemsMod.MODID, "activacion"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ActivacionPayload> CODEC =
            StreamCodec.of((buf, p) -> buf.writeResourceLocation(p.item),
                    buf -> new ActivacionPayload(buf.readResourceLocation()));

    public final ResourceLocation item;

    public ActivacionPayload(ResourceLocation item) {
        this.item = item;
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
