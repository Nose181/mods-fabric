package com.ruleta.net;

import com.ruleta.RuletaMod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/** Servidor -> clientes: muestra (o quita) el cuadro de aviso de arriba a la izquierda. */
public class AvisoPayload implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<AvisoPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(RuletaMod.MODID, "aviso"));
    public static final StreamCodec<RegistryFriendlyByteBuf, AvisoPayload> CODEC =
            StreamCodec.of((buf, p) -> p.escribir(buf), AvisoPayload::leer);

    public final String titulo;
    public final String mensaje;
    public final int rgb;
    public final int segundos;
    public final boolean pideCumplido;
    public final boolean limpiar;

    public AvisoPayload(String titulo, String mensaje, int rgb, int segundos, boolean pideCumplido, boolean limpiar) {
        this.titulo = titulo;
        this.mensaje = mensaje;
        this.rgb = rgb;
        this.segundos = segundos;
        this.pideCumplido = pideCumplido;
        this.limpiar = limpiar;
    }

    public static AvisoPayload quitar() {
        return new AvisoPayload("", "", 0, 0, false, true);
    }

    private void escribir(FriendlyByteBuf buf) {
        buf.writeUtf(titulo, 120);
        buf.writeUtf(mensaje, 1100);
        buf.writeInt(rgb);
        buf.writeVarInt(segundos);
        buf.writeBoolean(pideCumplido);
        buf.writeBoolean(limpiar);
    }

    private static AvisoPayload leer(FriendlyByteBuf buf) {
        String titulo = buf.readUtf(120);
        String mensaje = buf.readUtf(1100);
        int rgb = buf.readInt();
        int segundos = buf.readVarInt();
        boolean pide = buf.readBoolean();
        boolean limpiar = buf.readBoolean();
        return new AvisoPayload(titulo, mensaje, rgb, segundos, pide, limpiar);
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
