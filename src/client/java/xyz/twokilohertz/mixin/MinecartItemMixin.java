package xyz.twokilohertz.mixin;

import net.minecraft.block.Blocks;
import net.minecraft.item.*;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.twokilohertz.HotbarReplace;

@Mixin(MinecartItem.class)
public class MinecartItemMixin {
    private Item lastPlacedItem;
    @Inject(at = @At("HEAD"), method = "useOnBlock")
    private void onMinecartPlaced_head(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        lastPlacedItem = context.getStack().getItem();
    }

    @Inject(at = @At("TAIL"), method = "useOnBlock")
    private void onMinecartPlaced_tail(ItemUsageContext context, CallbackInfoReturnable<ActionResult> info) {
        // Early return if the block place action would fail
        if (info.getReturnValue() != ActionResult.SUCCESS)
            return;

        // Check if the stack is not empty, return if so
        if (context.getStack().getCount() != 0)
            return;

        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();

        // Check if the block the player clicked is rails
        if (world.getBlockState(pos).getBlock() == Blocks.RAIL ||
                world.getBlockState(pos).getBlock() == Blocks.POWERED_RAIL ||
                world.getBlockState(pos).getBlock() == Blocks.DETECTOR_RAIL ||
                world.getBlockState(pos).getBlock() == Blocks.ACTIVATOR_RAIL) {

            // Check if the item is a minecart
            if (lastPlacedItem == Items.MINECART ||
                    lastPlacedItem == Items.CHEST_MINECART ||
                    lastPlacedItem == Items.TNT_MINECART ||
                    lastPlacedItem == Items.HOPPER_MINECART ||
                    lastPlacedItem == Items.FURNACE_MINECART) {

                // Try to replace the hotbar slot
                HotbarReplace.tryReplaceSlot(context, lastPlacedItem, context.getHand());
            }
        }
    }
}
