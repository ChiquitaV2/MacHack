package mac.hack.mixin;

import mac.hack.MacHack;
import mac.hack.event.events.EventDrawOverlay;
import mac.hack.module.ModuleManager;
import mac.hack.module.mods.NoRender;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class MixinIngameHud {

	@Inject(at = @At(value = "RETURN"), method = "render", cancellable = true)
	public void render(MatrixStack matrixStack, float float_1, CallbackInfo info) {
		EventDrawOverlay event = new EventDrawOverlay(matrixStack);
		MacHack.eventBus.post(event);
		if (event.isCancelled()) info.cancel();
	}

	@Inject(at = @At("HEAD"), method = "renderPumpkinOverlay()V", cancellable = true)
	private void onRenderPumpkinOverlay(CallbackInfo ci) {
		if (ModuleManager.getModule(NoRender.class).isToggled() && ModuleManager.getModule(NoRender.class).getSetting(4).asToggle().state)
			ci.cancel();
	}
}