package com.sacos;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.item.ItemStack;

/** Abrir sacos. */
public class Sacos {

    public static void abrir(ServerPlayer jugador, ItemStack saco) {
        jugador.openMenu(new SimpleMenuProvider(
                (id, inventario, j) -> new SacoMenu(id, inventario, new SacoContainer(saco), saco),
                saco.getHoverName()));
        jugador.serverLevel().playSound(null, jugador.blockPosition(),
                SoundEvents.CHEST_OPEN, SoundSource.PLAYERS, 0.6f, 1.5f);
    }

    /** Con la tecla B: abre el primer saco que encuentre (mano secundaria primero, luego el inventario). */
    public static void abrirPrimero(ServerPlayer jugador) {
        ItemStack secundaria = jugador.getOffhandItem();
        if (secundaria.getItem() instanceof SacoItem) {
            abrir(jugador, secundaria);
            return;
        }
        for (ItemStack pila : jugador.getInventory().items) {
            if (pila.getItem() instanceof SacoItem) {
                abrir(jugador, pila);
                return;
            }
        }
        jugador.displayClientMessage(Component.literal("No tienes ningún saco en el inventario."), true);
    }
}
