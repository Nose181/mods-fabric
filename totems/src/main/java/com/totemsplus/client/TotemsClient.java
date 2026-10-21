package com.totemsplus.client;

import com.totemsplus.net.ActivacionPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;

/** Parte del cliente: muestra la animacion del tótem cuando el servidor lo pide. */
public class TotemsClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(ActivacionPayload.TYPE, (p, contexto) -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null) return;
            mc.gameRenderer.displayItemActivation(new ItemStack(BuiltInRegistries.ITEM.get(p.item)));
        });
    }
}
