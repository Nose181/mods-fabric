package com.ruleta.net;

import com.ruleta.RuletaMod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/** Cliente -> servidor: lo que se pulso en el panel de control (el servidor comprueba que sea operador). */
public class PanelAccionPayload implements CustomPacketPayload {

    public static final int GUARDAR = 0;
    public static final int LANZAR = 1;
    public static final int SOLO_AVISO = 2;
    public static final int DETENER = 3;

    public static final CustomPacketPayload.Type<PanelAccionPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(RuletaMod.MODID, "panel_accion"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PanelAccionPayload> CODEC =
            StreamCodec.of((buf, p) -> p.escribir(buf), PanelAccionPayload::leer);

    public final int accion;
    public final int indice;
    public final String titulo;
    public final String mensaje;
    public final int segundos;
    public final boolean cumplido;
    public final boolean aviso;

    public PanelAccionPayload(int accion, int indice, String titulo, String mensaje,
                              int segundos, boolean cumplido, boolean aviso) {
        this.accion = accion;
        this.indice = indice;
        this.titulo = titulo;
        this.mensaje = mensaje;
        this.segundos = segundos;
        this.cumplido = cumplido;
        this.aviso = aviso;
    }

    private void escribir(FriendlyByteBuf buf) {
        buf.writeVarInt(accion);
        buf.writeVarInt(indice);
        buf.writeUtf(titulo, 120);
        buf.writeUtf(mensaje, 1100);
        buf.writeVarInt(segundos);
        buf.writeBoolean(cumplido);
        buf.writeBoolean(aviso);
    }

    private static PanelAccionPayload leer(FriendlyByteBuf buf) {
        int accion = buf.readVarInt();
        int indice = buf.readVarInt();
        String titulo = buf.readUtf(120);
        String mensaje = buf.readUtf(1100);
        int segundos = buf.readVarInt();
        boolean cumplido = buf.readBoolean();
        boolean aviso = buf.readBoolean();
        return new PanelAccionPayload(accion, indice, titulo, mensaje, segundos, cumplido, aviso);
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
