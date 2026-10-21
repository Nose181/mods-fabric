package com.totemsplus;

import com.totemsplus.net.ActivacionPayload;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;

public class TotemsMod implements ModInitializer {
    public static final String MODID = "totemsplus";

    @Override
    public void onInitialize() {
        // Items de los tótems
        for (Tipo t : Tipo.values()) {
            Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(MODID, t.id),
                    new TotemEspecial(new Item.Properties().stacksTo(1).rarity(Rarity.RARE), t));
        }

        // Pestaña de combate del modo creativo
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.COMBAT).register(entradas -> {
            for (Tipo t : Tipo.values()) {
                entradas.accept(BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(MODID, t.id)));
            }
        });

        // Paquete para mostrar la animacion del tótem en la pantalla del jugador
        PayloadTypeRegistry.playS2C().register(ActivacionPayload.TYPE, ActivacionPayload.CODEC);

        // Si vas a morir con uno de estos tótems en la mano, te salva
        ServerLivingEntityEvents.ALLOW_DEATH.register((entidad, fuente, cantidad) -> permitirMuerte(entidad, fuente));
    }

    /** Devuelve false para evitar la muerte (el tótem te salvó) y true si todo sigue normal. */
    private static boolean permitirMuerte(LivingEntity entidad, DamageSource fuente) {
        if (!(entidad instanceof ServerPlayer jugador)) return true;
        // Igual que el tótem normal: no funciona contra el vacío ni /kill
        if (fuente.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) return true;

        ItemStack totem = ItemStack.EMPTY;
        if (jugador.getMainHandItem().getItem() instanceof TotemEspecial) {
            totem = jugador.getMainHandItem();
        } else if (jugador.getOffhandItem().getItem() instanceof TotemEspecial) {
            totem = jugador.getOffhandItem();
        }
        if (totem.isEmpty()) return true;

        Tipo tipo = ((TotemEspecial) totem.getItem()).tipo();
        ItemStack usado = totem.copy();
        totem.shrink(1);

        Efectos.activar(jugador, tipo);
        if (ServerPlayNetworking.canSend(jugador, ActivacionPayload.TYPE)) {
            ServerPlayNetworking.send(jugador, new ActivacionPayload(BuiltInRegistries.ITEM.getKey(usado.getItem())));
        }
        return false;
    }
}
