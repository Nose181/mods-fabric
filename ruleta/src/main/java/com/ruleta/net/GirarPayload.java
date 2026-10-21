package com.ruleta.net;

import com.ruleta.RuletaMod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * Servidor -> clientes: "gira la ruleta con estos colores y que caiga en este sector".
 * Solo se manda el texto del reto ganador, asi nadie puede ver los demas.
 */
public class GirarPayload implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<GirarPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(RuletaMod.MODID, "girar"));
    public static final StreamCodec<RegistryFriendlyByteBuf, GirarPayload> CODEC =
            StreamCodec.of((buf, p) -> p.escribir(buf), GirarPayload::leer);

    public final int[] colores;
    public final int indice;
    public final String texto;

    public GirarPayload(int[] colores, int indice, String texto) {
        this.colores = colores;
        this.indice = indice;
        this.texto = texto;
    }

    private void escribir(FriendlyByteBuf buf) {
        buf.writeVarInt(colores.length);
        for (int c : colores) buf.writeInt(c);
        buf.writeVarInt(indice);
        buf.writeUtf(texto, 1100);
    }

    private static GirarPayload leer(FriendlyByteBuf buf) {
        int n = buf.readVarInt();
        int[] colores = new int[n];
        for (int i = 0; i < n; i++) colores[i] = buf.readInt();
        int indice = buf.readVarInt();
        String texto = buf.readUtf(1100);
        return new GirarPayload(colores, indice, texto);
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
