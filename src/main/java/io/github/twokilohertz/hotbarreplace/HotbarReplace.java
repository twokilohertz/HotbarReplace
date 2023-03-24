package io.github.twokilohertz.hotbarreplace;

import net.fabricmc.api.ModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.SlotActionType;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class HotbarReplace implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("hotbarreplace");
    private static final MinecraftClient client = MinecraftClient.getInstance();
    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    @Override
    public void onInitialize() {
        LOGGER.info("HotbarReplace initialised");
    }

    public static void tryReplaceSlot(ItemPlacementContext context, Item item) {
        // Return immediately if player is a spectator
        PlayerEntity player = context.getPlayer();
        if (player.isSpectator()) return;

        // Creative inventories don't run out of anyway
        if (player.getAbilities().creativeMode) return;

        // Get reference to player's current inventory
        PlayerInventory inventory = player.getInventory();
        if (inventory == null) return;

        // Return if the inventory is empty
        if (inventory.isEmpty()) return;

        // Attempt to find a stack of matching items
        for (int i = 0; i < inventory.main.size(); i++) {
            if (i == inventory.selectedSlot) continue;

            ItemStack stack = inventory.main.get(i);

            if (stack.isOf(item)) {
                // Simulate moving the stack from one slot to another
                if (client != null) {
                    client.interactionManager.clickSlot(player.currentScreenHandler.syncId, i, GLFW.GLFW_MOUSE_BUTTON_1, SlotActionType.PICKUP, player);

                    // Wait 50 seconds (on another thread) before attempting to move the new stack
                    // The magic number 36 is the offset to get the hotbar slotId
                    scheduler.schedule(() -> {
                        client.interactionManager.clickSlot(player.currentScreenHandler.syncId, inventory.selectedSlot + 36, GLFW.GLFW_MOUSE_BUTTON_1, SlotActionType.PICKUP, player);
                    }, 50, TimeUnit.MILLISECONDS);
                }

                return;
            }
        }
    }
}
