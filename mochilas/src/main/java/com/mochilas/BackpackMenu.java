package com.mochilas;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/**
 * La ventana de la mochila: igual que la de un cofre (1, 3 o 6 filas), pero sin dejar mover la mochila abierta
 * y sin aceptar otras mochilas ni shulkers adentro.
 */
public class BackpackMenu extends AbstractContainerMenu {

    private final Container contenedor;
    private final int filas;
    private final ItemStack mochila;

    public BackpackMenu(MenuType<?> tipo, int id, Inventory inventario, Container contenedor, int filas, ItemStack mochila) {
        super(tipo, id);
        checkContainerSize(contenedor, filas * 9);
        this.contenedor = contenedor;
        this.filas = filas;
        this.mochila = mochila;
        contenedor.startOpen(inventario.player);

        int desplazamiento = (filas - 4) * 18;
        for (int f = 0; f < filas; f++) {
            for (int c = 0; c < 9; c++) {
                addSlot(new SlotMochila(contenedor, c + f * 9, 8 + c * 18, 18 + f * 18));
            }
        }
        for (int f = 0; f < 3; f++) {
            for (int c = 0; c < 9; c++) {
                addSlot(new Slot(inventario, c + f * 9 + 9, 8 + c * 18, 103 + f * 18 + desplazamiento));
            }
        }
        for (int c = 0; c < 9; c++) {
            addSlot(new Slot(inventario, c, 8 + c * 18, 161 + desplazamiento));
        }
    }

    /** Casilla de la mochila: no acepta cosas que no caben dentro de contenedores (otras mochilas, shulkers). */
    private static class SlotMochila extends Slot {
        SlotMochila(Container contenedor, int indice, int x, int y) {
            super(contenedor, indice, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack pila) {
            return pila.getItem().canFitInsideContainerItems();
        }
    }

    @Override
    public void clicked(int ranura, int boton, ClickType tipo, Player jugador) {
        // No se puede mover la mochila que esta abierta
        if (ranura >= 0 && ranura < slots.size() && slots.get(ranura).getItem() == mochila) return;
        if (tipo == ClickType.SWAP) {
            if (boton == 40 && jugador.getOffhandItem() == mochila) return;
            if (boton >= 0 && boton < 9) {
                int indice = filas * 9 + 27 + boton;
                if (indice < slots.size() && slots.get(indice).getItem() == mochila) return;
            }
        }
        super.clicked(ranura, boton, tipo, jugador);
    }

    @Override
    public ItemStack quickMoveStack(Player jugador, int indice) {
        ItemStack copia = ItemStack.EMPTY;
        Slot ranura = slots.get(indice);
        if (ranura != null && ranura.hasItem()) {
            ItemStack pila = ranura.getItem();
            copia = pila.copy();
            if (indice < filas * 9) {
                if (!moveItemStackTo(pila, filas * 9, slots.size(), true)) return ItemStack.EMPTY;
            } else if (!moveItemStackTo(pila, 0, filas * 9, false)) {
                return ItemStack.EMPTY;
            }
            if (pila.isEmpty()) {
                ranura.setByPlayer(ItemStack.EMPTY);
            } else {
                ranura.setChanged();
            }
        }
        return copia;
    }

    /** La ventana se cierra sola si la mochila deja de estar en el inventario del jugador. */
    @Override
    public boolean stillValid(Player jugador) {
        if (mochila.isEmpty()) return false;
        if (jugador.getOffhandItem() == mochila) return true;
        for (ItemStack pila : jugador.getInventory().items) {
            if (pila == mochila) return true;
        }
        return false;
    }

    @Override
    public void removed(Player jugador) {
        super.removed(jugador);
        contenedor.stopOpen(jugador);
    }
}
