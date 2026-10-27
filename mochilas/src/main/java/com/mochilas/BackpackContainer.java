package com.mochilas;

import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;

import java.util.ArrayList;
import java.util.List;

/** Las cosas de la mochila: se leen del item al abrirla y se guardan en el item cada vez que algo cambia. */
public class BackpackContainer extends SimpleContainer {

    private final ItemStack mochila;
    private boolean cargando = true;

    public BackpackContainer(ItemStack mochila, int espacios) {
        super(espacios);
        this.mochila = mochila;
        ItemContainerContents contenido = mochila.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
        NonNullList<ItemStack> lista = NonNullList.withSize(espacios, ItemStack.EMPTY);
        contenido.copyInto(lista);
        for (int i = 0; i < espacios; i++) {
            setItem(i, lista.get(i));
        }
        cargando = false;
    }

    @Override
    public void setChanged() {
        super.setChanged();
        if (!cargando) guardar();
    }

    private void guardar() {
        List<ItemStack> lista = new ArrayList<>();
        for (int i = 0; i < getContainerSize(); i++) {
            lista.add(getItem(i).copy());
        }
        mochila.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(lista));
    }
}
