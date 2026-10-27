package com.mochilas;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

/** Abrir mochilas. */
public class Mochilas {

    public static void abrir(ServerPlayer jugador, ItemStack mochila, int filas) {
        MenuType<?> tipo = filas == 1 ? MenuType.GENERIC_9x1 : (filas == 3 ? MenuType.GENERIC_9x3 : MenuType.GENERIC_9x6);
        jugador.openMenu(new SimpleMenuProvider(
                (id, inventario, j) -> new BackpackMenu(tipo, id, inventario,
                        new BackpackContainer(mochila, filas * 9), filas, mochila),
                mochila.getHoverName()));
        jugador.serverLevel().playSound(null, jugador.blockPosition(),
                SoundEvents.CHEST_OPEN, SoundSource.PLAYERS, 0.6f, 1.5f);
    }

    /** Con la tecla B: abre la primera mochila que encuentre (mano secundaria primero, luego el inventario). */
    public static void abrirPrimera(ServerPlayer jugador) {
        ItemStack secundaria = jugador.getOffhandItem();
        if (secundaria.getItem() instanceof BackpackItem b) {
            abrir(jugador, secundaria, b.filas());
            return;
        }
        for (ItemStack pila : jugador.getInventory().items) {
            if (pila.getItem() instanceof BackpackItem b) {
                abrir(jugador, pila, b.filas());
                return;
            }
        }
        jugador.displayClientMessage(Component.literal("No tienes ninguna mochila en el inventario."), true);
    }
}
