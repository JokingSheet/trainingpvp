package de.jokingsheet.trainingpvp;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public final class TrainingPvpClient implements ClientModInitializer {
    public static final String MOD_ID = "trainingpvp";

    private static KeyMapping openMenuKey;
    private static long tickWindowStart = System.nanoTime();
    private static int tickWindowCount;
    private static int measuredClientTicks = 20;

    @Override
    public void onInitializeClient() {
        KeyMapping.Category category = KeyMapping.Category.register(
                Identifier.fromNamespaceAndPath(MOD_ID, "main")
        );

        openMenuKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.trainingpvp.open",
                InputConstants.Type.KEYSYM,
                InputConstants.KEY_H,
                category
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            updateTickMonitor();

            while (openMenuKey.consumeClick()) {
                if (client.player != null) {
                    client.setScreen(new AcademyScreen());
                }
            }
        });
    }

    private static void updateTickMonitor() {
        tickWindowCount++;
        long now = System.nanoTime();
        long elapsed = now - tickWindowStart;
        if (elapsed >= 1_000_000_000L) {
            measuredClientTicks = (int) Math.round(tickWindowCount * 1_000_000_000.0 / elapsed);
            tickWindowCount = 0;
            tickWindowStart = now;
        }
    }

    public static int getMeasuredClientTicks() {
        return measuredClientTicks;
    }

    /**
     * Gives harmless building and movement supplies for solo practice.
     * Existing inventory contents are never cleared or replaced.
     */
    public static void giveMovementKit(Minecraft client) {
        if (client.player == null) {
            return;
        }

        var inventory = client.player.getInventory();
        inventory.add(new ItemStack(Items.OAK_PLANKS, 64));
        inventory.add(new ItemStack(Items.SCAFFOLDING, 32));
        inventory.add(new ItemStack(Items.SLIME_BLOCK, 16));
        inventory.add(new ItemStack(Items.HONEY_BLOCK, 16));
        inventory.add(new ItemStack(Items.WATER_BUCKET, 1));
        inventory.add(new ItemStack(Items.ENDER_PEARL, 8));
        inventory.add(new ItemStack(Items.FIREWORK_ROCKET, 16));

        TrainingStats.recordKitClaim();
        client.player.displayClientMessage(
                Component.literal("§aMovement-Kit hinzugefügt! §7Dein Inventar wurde nicht geleert."),
                false
        );
    }
}
