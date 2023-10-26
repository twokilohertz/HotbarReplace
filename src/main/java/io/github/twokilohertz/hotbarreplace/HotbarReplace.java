package io.github.twokilohertz.hotbarreplace;

import net.fabricmc.api.ModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
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
        LOGGER.info("HotbarReplace v0.1.2 initialised");
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

        // If current screen handler is null, return
        if (player.currentScreenHandler == null) return;

        // Attempt to find a stack of matching items in the player's inventory
        for (int i = 0; i < player.currentScreenHandler.slots.size(); i++) {
            if (player.currentScreenHandler.slots.get(i).getStack().isOf(item)) {
                // Simulate moving the stack from one slot to another
                if (client != null) {
                    client.interactionManager.clickSlot(player.currentScreenHandler.syncId, i, GLFW.GLFW_MOUSE_BUTTON_1, SlotActionType.PICKUP, player);

                    /*
                     Wait 50 milliseconds (on another thread) before attempting to move the new stack
                     PlayerInventory.MAIN_SIZE added to the selected slot (hotbar slot) is the correct slot ID
                    */
                    scheduler.schedule(() -> {
                        client.interactionManager.clickSlot(player.currentScreenHandler.syncId, inventory.selectedSlot + PlayerInventory.MAIN_SIZE, GLFW.GLFW_MOUSE_BUTTON_1, SlotActionType.PICKUP, player);
                    }, 50, TimeUnit.MILLISECONDS);
                }

                return;
            }
        }
    }
}
