package com.example;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;

public class AntiTotemExploit implements ClientModInitializer {
    public static boolean enabled = false;
    public static int sneakTicks = 0;
    private static KeyMapping toggleKey;

    @Override
    public void onInitializeClient() {
        toggleKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
            "key.antitotemexploit.toggle",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_Y,
            KeyMapping.Category.register(Identifier.fromNamespaceAndPath("antitotemexploit", "main"))
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleKey.consumeClick()) {
                enabled = !enabled;
                if (client.player != null) {
                    client.player.displayClientMessage(
                        Component.literal("AntiTotemExploit: " + (enabled ? "ON" : "OFF")), true);
                }
            }
            if (sneakTicks > 0) {
                client.options.keyShift.setDown(true);
                sneakTicks--;
                if (sneakTicks == 0) client.options.keyShift.setDown(false);
            }
        });
    }

    public static void onTotemPop(Minecraft client) {
        if (!enabled || client.player == null) return;
        for (int i = 0; i < 9; i++) {
            ItemStack s = client.player.getInventory().getItem(i);
            if (!s.isEmpty() && s.getHoverName().getString().toLowerCase().contains("шар крови")) {
                client.player.getInventory().setSelectedSlot(i);
                sneakTicks = 3;
                return;
            }
        }
    }
}
