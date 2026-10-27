package com.mochilas.client;

import com.mochilas.net.AbrirMochilaPayload;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

/** Parte del cliente: la tecla para abrir la mochila sin tenerla en la mano (se puede cambiar en Controles). */
public class MochilasClient implements ClientModInitializer {

    public static final KeyMapping ABRIR = new KeyMapping(
            "key.mochilas.abrir", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_B, "key.categories.mochilas");

    @Override
    public void onInitializeClient() {
        KeyBindingHelper.registerKeyBinding(ABRIR);

        ClientTickEvents.END_CLIENT_TICK.register(cliente -> {
            while (ABRIR.consumeClick()) {
                if (cliente.player != null) {
                    ClientPlayNetworking.send(new AbrirMochilaPayload());
                }
            }
        });
    }
}
