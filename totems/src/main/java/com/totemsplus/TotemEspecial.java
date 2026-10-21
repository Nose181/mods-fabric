package com.totemsplus;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

/** Un tótem nuevo. Como el de la Inmortalidad, solo funciona si lo llevas en la mano. */
public class TotemEspecial extends Item {

    private final Tipo tipo;

    public TotemEspecial(Properties propiedades, Tipo tipo) {
        super(propiedades);
        this.tipo = tipo;
    }

    public Tipo tipo() {
        return tipo;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext contexto,
                                List<Component> lineas, TooltipFlag bandera) {
        lineas.add(Component.literal("Si vas a morir y lo llevas en la mano,").withStyle(ChatFormatting.GRAY));
        lineas.add(Component.literal("te salva (como el Tótem de la Inmortalidad).").withStyle(ChatFormatting.GRAY));
        for (String d : tipo.descripcion) {
            lineas.add(Component.literal("• " + d).withStyle(ChatFormatting.AQUA));
        }
    }
}
