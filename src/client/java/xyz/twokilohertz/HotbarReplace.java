package xyz.twokilohertz;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
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

    public static void tryReplaceSlot(ItemUsageContext context, Item item, Hand hand) {
        // Return immediately if player is spectator or in creative
        PlayerEntity player = context.getPlayer();
        if(player.isSpectator() || player.getAbilities().creativeMode)
            return;

        // Return immediately if inventory is empty, null or when current screen handler is null
        PlayerInventory inventory = player.getInventory();
        if(inventory == null || inventory.isEmpty() || player.currentScreenHandler == null)
            return;

        //Attempt to find a stack of matching items in the player's inventory
        ReplaceSlot(item, hand, player, inventory);
    }

    public static void tryReplaceSlot(ItemPlacementContext context, Item item, Hand hand) {
        // Return immediately if player is spectator or in creative
        PlayerEntity player = context.getPlayer();
        if(player.isSpectator() || player.getAbilities().creativeMode)
            return;

        // Return immediately if inventory is empty, null or when current screen handler is null
        PlayerInventory inventory = player.getInventory();
        if(inventory == null || inventory.isEmpty() || player.currentScreenHandler == null)
            return;

        // Attempt to find a stack of matching items in the player's inventory
        ReplaceSlot(item, hand, player, inventory);
    }

    private static void ReplaceSlot(Item lastPlacedItem, Hand hand, PlayerEntity player, PlayerInventory inventory) {
        for (int i = 0; i < player.currentScreenHandler.slots.size(); i++) {
            if (player.currentScreenHandler.slots.get(i).getStack().isOf(lastPlacedItem)) {
                // Simulate moving the stack from one slot to another
                if (client != null) {
                    // TODO: This still feels like a bit of a hack
                    // I honestly do not know Minecraft internals enough to be sure that there won't
                    // be de-sync issues.

                    int current_fps = client.getCurrentFps();
                    int click_delay = Math.round(1.0f / (float) current_fps) * 1000;

                    client.interactionManager.clickSlot(player.currentScreenHandler.syncId, i, GLFW.GLFW_MOUSE_BUTTON_1,
                            SlotActionType.PICKUP, player);

                    int slot = (hand == Hand.OFF_HAND ? 9 : inventory.selectedSlot);

                    scheduler.schedule(() -> {
                        client.interactionManager.clickSlot(player.currentScreenHandler.syncId,
                                slot + PlayerInventory.MAIN_SIZE, GLFW.GLFW_MOUSE_BUTTON_1,
                                SlotActionType.PICKUP, player);
                    }, click_delay, TimeUnit.MILLISECONDS);
                }

                return;
            }
        }
    }
}
