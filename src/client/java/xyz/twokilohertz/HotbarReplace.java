package xyz.twokilohertz;

import net.fabricmc.api.ClientModInitializer;
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

public class HotbarReplace implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("hotbarreplace");
    private static final MinecraftClient client = MinecraftClient.getInstance();
    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    @Override
    public void onInitializeClient() {
        LOGGER.info("HotbarReplace v0.1.3 initialised");
    }

    public static void tryReplaceSlot(ItemPlacementContext context, Item item) {
        // Return immediately if player is a spectator
        PlayerEntity player = context.getPlayer();
        if (player.isSpectator())
            return;

        // Creative inventories don't run out of blocks anyway
        if (player.getAbilities().creativeMode)
            return;

        // Get reference to player's current inventory
        PlayerInventory inventory = player.getInventory();
        if (inventory == null)
            return;

        // Return if the inventory is empty
        if (inventory.isEmpty())
            return;

        // If current screen handler is null, return
        if (player.currentScreenHandler == null)
            return;

        // Attempt to find a stack of matching items in the player's inventory
        for (int i = 0; i < player.currentScreenHandler.slots.size(); i++) {
            if (player.currentScreenHandler.slots.get(i).getStack().isOf(item)) {
                // Simulate moving the stack from one slot to another
                if (client != null) {
                    // TODO: This still feels like a bit of a hack
                    // I honestly do not know Minecraft internals enough to be sure that there won't
                    // be de-sync issues.

                    int current_fps = client.getCurrentFps();
                    int click_delay = Math.round(1.0f / (float) current_fps) * 1000;

                    client.interactionManager.clickSlot(player.currentScreenHandler.syncId, i, GLFW.GLFW_MOUSE_BUTTON_1,
                            SlotActionType.PICKUP, player);

                    scheduler.schedule(() -> {
                        client.interactionManager.clickSlot(player.currentScreenHandler.syncId,
                                inventory.selectedSlot + PlayerInventory.MAIN_SIZE, GLFW.GLFW_MOUSE_BUTTON_1,
                                SlotActionType.PICKUP, player);
                    }, click_delay, TimeUnit.MILLISECONDS);
                }

                return;
            }
        }
    }
}
