package com.ruleta.net;

import com.ruleta.RuletaMod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

/** Servidor -> clientes: "muestra la animacion de la calavera para estos jugadores". */
public class MuertePayload implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<MuertePayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(RuletaMod.MODID, "muerte"));
    public static final StreamCodec<RegistryFriendlyByteBuf, MuertePayload> CODEC =
            StreamCodec.of((buf, p) -> p.escribir(buf), MuertePayload::leer);

    public final List<String> nombres;
    public final boolean castigo;

    public MuertePayload(List<String> nombres, boolean castigo) {
        this.nombres = nombres;
        this.castigo = castigo;
    }

    private void escribir(FriendlyByteBuf buf) {
        buf.writeVarInt(nombres.size());
        for (String s : nombres) buf.writeUtf(s, 64);
        buf.writeBoolean(castigo);
    }

    private static MuertePayload leer(FriendlyByteBuf buf) {
        int n = buf.readVarInt();
        List<String> nombres = new ArrayList<>();
        for (int i = 0; i < n; i++) nombres.add(buf.readUtf(64));
        return new MuertePayload(nombres, buf.readBoolean());
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
