package xyz.twokilohertz.mixin;

import net.minecraft.item.*;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.twokilohertz.HotbarReplace;

@Mixin(BoneMealItem.class)
public class BoneMealItemMixin {
    private static Item lastPlacedItem;

    @Inject(at = @At("HEAD"), method = "useOnBlock")
    private void onMinecartPlaced_head(ItemUsageContext context, CallbackInfoReturnable<ActionResult> info) {
        lastPlacedItem = context.getStack().getItem();
    }

    @Inject(at = @At(value="RETURN"), method = "useOnBlock")
    private void onBoneMealUsedReturn(ItemUsageContext context, CallbackInfoReturnable<ActionResult> info) {
        // Try to replace the hotbar slot with bone meal
        if (info.getReturnValue() == ActionResult.SUCCESS) {
            //Have to call on 1 cause of server/client-sided differences
            if (context.getStack().getCount() == 1) {
                HotbarReplace.tryReplaceSlot(context, lastPlacedItem, context.getHand());
            }
        }
    }
}