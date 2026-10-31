package com.sacos;

import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;

import java.util.ArrayList;
import java.util.List;

/** Las cosas del saco: se leen del item al abrirlo y se guardan en el item cada vez que algo cambia. */
public class SacoContainer extends SimpleContainer {

    private final ItemStack saco;
    private boolean cargando = true;

    public SacoContainer(ItemStack saco) {
        super(SacoItem.ESPACIOS);
        this.saco = saco;
        ItemContainerContents contenido = saco.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
        NonNullList<ItemStack> lista = NonNullList.withSize(SacoItem.ESPACIOS, ItemStack.EMPTY);
        contenido.copyInto(lista);
        for (int i = 0; i < SacoItem.ESPACIOS; i++) {
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
        saco.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(lista));
    }
}
