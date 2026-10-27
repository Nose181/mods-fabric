package com.mochilas;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;

import java.util.List;

/** Un saco o mochila: al hacer clic derecho se abre y guarda las cosas dentro del propio item. */
public class BackpackItem extends Item {

    private final int filas;

    public BackpackItem(Properties propiedades, int filas) {
        super(propiedades);
        this.filas = filas;
    }

    public int filas() {
        return filas;
    }

    public int espacios() {
        return filas * 9;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level nivel, Player jugador, InteractionHand mano) {
        ItemStack mochila = jugador.getItemInHand(mano);
        if (!nivel.isClientSide && jugador instanceof ServerPlayer sp) {
            Mochilas.abrir(sp, mochila, filas);
        }
        return InteractionResultHolder.sidedSuccess(mochila, nivel.isClientSide());
    }

    /** Una mochila no cabe dentro de un shulker, otra mochila ni un morral (evita trampas). */
    @Override
    public boolean canFitInsideContainerItems() {
        return false;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext contexto,
                                List<Component> lineas, TooltipFlag bandera) {
        ItemContainerContents contenido = stack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
        long usados = contenido.nonEmptyStream().count();
        lineas.add(Component.literal("Espacios: " + usados + "/" + espacios()).withStyle(ChatFormatting.GRAY));
        lineas.add(Component.literal("Clic derecho o tecla B para abrir").withStyle(ChatFormatting.DARK_GRAY));
    }
}
