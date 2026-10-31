package com.sacos;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/**
 * La ventana del saco: igual que la de un cofre (3 filas), pero sin dejar mover el saco abierto
 * y sin aceptar otros sacos ni shulkers adentro.
 */
public class SacoMenu extends AbstractContainerMenu {

    private static final int FILAS = SacoItem.FILAS;

    private final Container contenedor;
    private final ItemStack saco;

    public SacoMenu(int id, Inventory inventario, Container contenedor, ItemStack saco) {
        super(MenuType.GENERIC_9x3, id);
        checkContainerSize(contenedor, FILAS * 9);
        this.contenedor = contenedor;
        this.saco = saco;
        contenedor.startOpen(inventario.player);

        int desplazamiento = (FILAS - 4) * 18;
        for (int f = 0; f < FILAS; f++) {
            for (int c = 0; c < 9; c++) {
                addSlot(new SlotSaco(contenedor, c + f * 9, 8 + c * 18, 18 + f * 18));
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

    /** Casilla del saco: no acepta cosas que no caben dentro de contenedores (otros sacos, shulkers). */
    private static class SlotSaco extends Slot {
        SlotSaco(Container contenedor, int indice, int x, int y) {
            super(contenedor, indice, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack pila) {
            return pila.getItem().canFitInsideContainerItems();
        }
    }

    @Override
    public void clicked(int ranura, int boton, ClickType tipo, Player jugador) {
        // No se puede mover el saco que esta abierto
        if (ranura >= 0 && ranura < slots.size() && slots.get(ranura).getItem() == saco) return;
        if (tipo == ClickType.SWAP) {
            if (boton == 40 && jugador.getOffhandItem() == saco) return;
            if (boton >= 0 && boton < 9) {
                int indice = FILAS * 9 + 27 + boton;
                if (indice < slots.size() && slots.get(indice).getItem() == saco) return;
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
            if (indice < FILAS * 9) {
                if (!moveItemStackTo(pila, FILAS * 9, slots.size(), true)) return ItemStack.EMPTY;
            } else if (!moveItemStackTo(pila, 0, FILAS * 9, false)) {
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

    /** La ventana se cierra sola si el saco deja de estar en el inventario del jugador. */
    @Override
    public boolean stillValid(Player jugador) {
        if (saco.isEmpty()) return false;
        if (jugador.getOffhandItem() == saco) return true;
        for (ItemStack pila : jugador.getInventory().items) {
            if (pila == saco) return true;
        }
        return false;
    }

    @Override
    public void removed(Player jugador) {
        super.removed(jugador);
        contenedor.stopOpen(jugador);
    }
}
