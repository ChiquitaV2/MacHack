package mac.hack.mixin;

import mac.hack.MacHack;
import mac.hack.event.events.EventWorldRender;
import mac.hack.module.ModuleManager;
import mac.hack.module.mods.NoRender;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class MixinGameRenderer {

	@Inject(at = @At("HEAD"), method = "renderHand", cancellable = true)
	private void renderHand(MatrixStack matrixStack_1, Camera camera, float tickDelta, CallbackInfo info) {
		EventWorldRender event = new EventWorldRender(tickDelta);
		MacHack.eventBus.post(event);
		if (event.isCancelled()) info.cancel();
	}


	@Inject(
			at = @At("HEAD"),
			method = "bobViewWhenHurt(Lnet/minecraft/client/util/math/MatrixStack;F)V",
			cancellable = true)
	private void onBobViewWhenHurt(MatrixStack matrixStack, float f, CallbackInfo ci) {
		if (ModuleManager.getModule(NoRender.class).isToggled() && ModuleManager.getModule(NoRender.class).getSetting(2).asToggle().state)
			ci.cancel();
	}

	@Inject(at = @At("HEAD"), method = "showFloatingItem", cancellable = true)
	private void showFloatingItem(ItemStack itemStack_1, CallbackInfo ci) {
		if (ModuleManager.getModule(NoRender.class).isToggled() && ModuleManager.getModule(NoRender.class).getSetting(8).asToggle().state
				&& itemStack_1.getItem() == Items.TOTEM_OF_UNDYING)
			ci.cancel();
	}

	@Redirect(
			at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;lerp(FFF)F", ordinal = 0),
			method = "renderWorld(FJLnet/minecraft/client/util/math/MatrixStack;)V")
	private float nauseaWobble(float delta, float first, float second) {
		if (!(ModuleManager.getModule(NoRender.class).isToggled() && ModuleManager.getModule(NoRender.class).getSetting(6).asToggle().state))
			return MathHelper.lerp(delta, first, second);

		return 0;
	}
}