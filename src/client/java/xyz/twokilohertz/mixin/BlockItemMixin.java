package xyz.twokilohertz.mixin;

import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.twokilohertz.HotbarReplace;

@Mixin(BlockItem.class)
public class BlockItemMixin {
    private Item lastPlacedItem;

    @Inject(at = @At("HEAD"), method = "Lnet/minecraft/item/BlockItem;place(Lnet/minecraft/item/ItemPlacementContext;)Lnet/minecraft/util/ActionResult;")
    private void BlockItem_place_head(ItemPlacementContext context, CallbackInfoReturnable<ActionResult> info) {
        lastPlacedItem = context.getStack().getItem();
    }

    @Inject(at = @At("TAIL"), method = "Lnet/minecraft/item/BlockItem;place(Lnet/minecraft/item/ItemPlacementContext;)Lnet/minecraft/util/ActionResult;")
    private void BlockItem_place_tail(ItemPlacementContext context, CallbackInfoReturnable<ActionResult> info) {
        // Early return if the block place action would fail
        if (info.getReturnValue() != ActionResult.SUCCESS)
            return;

        // Check if the stack is not empty, return if so
        if (context.getStack().getCount() != 0)
            return;

        // Try to replace the hotbar slot
        HotbarReplace.tryReplaceSlot(context, lastPlacedItem);
    }
}
