package com.temporizador.net;

import com.temporizador.TemporizadorMod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/** Servidor -> clientes: estado del temporizador (corriendo, en pausa o detenido). */
public class TemporizadorPayload implements CustomPacketPayload {

    public static final int PARAR = 0;
    public static final int CORRER = 1;
    public static final int PAUSA = 2;

    public static final CustomPacketPayload.Type<TemporizadorPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(TemporizadorMod.MODID, "estado"));
    public static final StreamCodec<RegistryFriendlyByteBuf, TemporizadorPayload> CODEC =
            StreamCodec.of((buf, p) -> p.escribir(buf), TemporizadorPayload::leer);

    public final int accion;
    public final long restanteMs;
    public final long totalMs;
    public final String texto;

    public TemporizadorPayload(int accion, long restanteMs, long totalMs, String texto) {
        this.accion = accion;
        this.restanteMs = restanteMs;
        this.totalMs = totalMs;
        this.texto = texto;
    }

    private void escribir(FriendlyByteBuf buf) {
        buf.writeVarInt(accion);
        buf.writeVarLong(restanteMs);
        buf.writeVarLong(totalMs);
        buf.writeUtf(texto, 256);
    }

    private static TemporizadorPayload leer(FriendlyByteBuf buf) {
        int accion = buf.readVarInt();
        long restante = buf.readVarLong();
        long total = buf.readVarLong();
        String texto = buf.readUtf(256);
        return new TemporizadorPayload(accion, restante, total, texto);
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
