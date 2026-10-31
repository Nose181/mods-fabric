package com.sacos;

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

/** El saco: al hacer clic derecho se abre y guarda las cosas dentro del propio item (27 espacios). */
public class SacoItem extends Item {

    public static final int FILAS = 3;
    public static final int ESPACIOS = FILAS * 9;

    public SacoItem(Properties propiedades) {
        super(propiedades);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level nivel, Player jugador, InteractionHand mano) {
        ItemStack saco = jugador.getItemInHand(mano);
        if (!nivel.isClientSide && jugador instanceof ServerPlayer sp) {
            Sacos.abrir(sp, saco);
        }
        return InteractionResultHolder.sidedSuccess(saco, nivel.isClientSide());
    }

    /** Un saco no cabe dentro de un shulker, de otro saco ni de un morral (evita trampas). */
    @Override
    public boolean canFitInsideContainerItems() {
        return false;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext contexto,
                                List<Component> lineas, TooltipFlag bandera) {
        ItemContainerContents contenido = stack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
        long usados = contenido.nonEmptyStream().count();
        lineas.add(Component.literal("Espacios: " + usados + "/" + ESPACIOS).withStyle(ChatFormatting.GRAY));
        lineas.add(Component.literal("Clic derecho o tecla B para abrir").withStyle(ChatFormatting.DARK_GRAY));
    }
}
