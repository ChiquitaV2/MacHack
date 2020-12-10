package mac.hack.mixin;

import mac.hack.MacHack;
import mac.hack.event.events.EventEntityRender;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public abstract class MixinEntityRenderer<T extends Entity> {

	@Inject(at = @At("HEAD"), method = "render", cancellable = true)
	public void render(T entity_1, float float_1, float float_2, MatrixStack matrixStack_1, VertexConsumerProvider vertexConsumerProvider_1, int int_1, CallbackInfo info) {
		EventEntityRender.Render event = new EventEntityRender.Render(entity_1);
		MacHack.eventBus.post(event);
		if (event.isCancelled()) info.cancel();
	}

	@Inject(at = @At("HEAD"), method = "renderLabelIfPresent", cancellable = true)
	public void renderLabelIfPresent(T entity_1, Text text_1, MatrixStack matrixStack_1, VertexConsumerProvider vertexConsumerProvider_1, int int_1, CallbackInfo info) {
		EventEntityRender.Label event = new EventEntityRender.Label(entity_1);
		MacHack.eventBus.post(event);
		if (event.isCancelled()) info.cancel();
	}
}
