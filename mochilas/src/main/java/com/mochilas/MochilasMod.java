package com.mochilas;

import com.mochilas.net.AbrirMochilaPayload;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

public class MochilasMod implements ModInitializer {
    public static final String MODID = "mochilas";

    public static Item SACO;
    public static Item MOCHILA;
    public static Item MOCHILA_GRANDE;

    @Override
    public void onInitialize() {
        SACO = registrar("saco", new BackpackItem(new Item.Properties().stacksTo(1), 1));
        MOCHILA = registrar("mochila", new BackpackItem(new Item.Properties().stacksTo(1), 3));
        MOCHILA_GRANDE = registrar("mochila_grande",
                new BackpackItem(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON), 6));

        // Pestaña "Herramientas y utilidades" del modo creativo
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(entradas -> {
            entradas.accept(SACO);
            entradas.accept(MOCHILA);
            entradas.accept(MOCHILA_GRANDE);
        });

        // La tecla B manda este paquete al servidor para abrir la primera mochila del inventario
        PayloadTypeRegistry.playC2S().register(AbrirMochilaPayload.TYPE, AbrirMochilaPayload.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(AbrirMochilaPayload.TYPE,
                (payload, contexto) -> Mochilas.abrirPrimera(contexto.player()));
    }

    private static Item registrar(String id, Item item) {
        return Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(MODID, id), item);
    }
}
