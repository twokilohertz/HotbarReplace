package today.theladpack.hotbarreplace.mixin;

import net.minecraft.client.gui.screen.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import today.theladpack.hotbarreplace.HotbarReplace;

@Mixin(TitleScreen.class)
public class HotbarReplaceMixin {
    @Inject(at = @At("HEAD"), method = "init()V")
    private void init(CallbackInfo info) {
        HotbarReplace.LOGGER.info("Hello from the HotbarReplace mixin");
    }
}
