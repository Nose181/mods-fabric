package com.sacos;

import com.sacos.net.AbrirSacoPayload;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;

public class SacosMod implements ModInitializer {
    public static final String MODID = "sacos";

    public static Item SACO;

    @Override
    public void onInitialize() {
        SACO = Registry.register(BuiltInRegistries.ITEM,
                ResourceLocation.fromNamespaceAndPath(MODID, "saco"),
                new SacoItem(new Item.Properties().stacksTo(1)));

        // Pestaña "Herramientas y utilidades" del modo creativo
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES)
                .register(entradas -> entradas.accept(SACO));

        // La tecla B manda este paquete al servidor para abrir el primer saco del inventario
        PayloadTypeRegistry.playC2S().register(AbrirSacoPayload.TYPE, AbrirSacoPayload.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(AbrirSacoPayload.TYPE,
                (payload, contexto) -> Sacos.abrirPrimero(contexto.player()));
    }
}
