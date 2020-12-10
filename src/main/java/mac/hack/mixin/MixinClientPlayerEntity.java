package mac.hack.mixin;

import mac.hack.MacHack;
import mac.hack.event.events.EventClientMove;
import mac.hack.event.events.EventMovementTick;
import mac.hack.event.events.EventTick;
import mac.hack.module.ModuleManager;
import mac.hack.module.mods.*;
import mac.hack.module.mods.NoSlow;
import mac.hack.module.mods.SafeWalk;
import mac.hack.module.mods.Scaffold;
import mac.hack.utils.MacQueue;
import mac.hack.utils.file.MacFileHelper;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.MovementType;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class MixinClientPlayerEntity extends AbstractClientPlayerEntity {

	public MixinClientPlayerEntity(ClientWorld world, GameProfile profile) {
		super(world, profile);
	}

	@Shadow
	protected MinecraftClient client;

	@Shadow
	protected void autoJump(float float_1, float float_2) {
	}

	@Inject(at = @At("RETURN"), method = "tick()V", cancellable = true)
	public void tick(CallbackInfo info) {
		try {
			if (MinecraftClient.getInstance().player.age % 100 == 0) {
				if (MacFileHelper.SCHEDULE_SAVE_MODULES)
					MacFileHelper.saveModules();
				if (MacFileHelper.SCHEDULE_SAVE_CLICKGUI)
					MacFileHelper.saveClickGui();
				if (MacFileHelper.SCHEDULE_SAVE_FRIENDS)
					MacFileHelper.saveFriends();
			}

			MacQueue.nextQueue();
		} catch (Exception e) {
		}

		EventTick event = new EventTick();
		MacHack.eventBus.post(event);
		if (event.isCancelled())
			info.cancel();
	}

	@Inject(at = @At("HEAD"), method = "sendMovementPackets()V", cancellable = true)
	public void sendMovementPackets(CallbackInfo info) {
		EventMovementTick event = new EventMovementTick();
		MacHack.eventBus.post(event);
		if (event.isCancelled())
			info.cancel();
	}

	@Redirect(method = "tickMovement()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isUsingItem()Z"))
	private boolean tickMovement_isUsingItem(ClientPlayerEntity player) {
		if (ModuleManager.getModule(NoSlow.class).isToggled() && ModuleManager.getModule(NoSlow.class).getSetting(5).asToggle().state) {
			return false;
		}

		return player.isUsingItem();
	}

	@Inject(at = @At("HEAD"), method = "move", cancellable = true)
	public void move(MovementType movementType_1, Vec3d vec3d_1, CallbackInfo info) {
		EventClientMove event = new EventClientMove(movementType_1, vec3d_1);
		MacHack.eventBus.post(event);
		if (event.isCancelled()) {
			info.cancel();
		} else if (!movementType_1.equals(event.type) || !vec3d_1.equals(event.vec3d)) {
			double double_1 = this.getX();
			double double_2 = this.getZ();
			super.move(event.type, event.vec3d);
			this.autoJump((float) (this.getX() - double_1), (float) (this.getZ() - double_2));
			info.cancel();
		}
	}

	@Override
	protected boolean clipAtLedge() {
		return super.clipAtLedge() || ModuleManager.getModule(SafeWalk.class).isToggled()
				|| (ModuleManager.getModule(Scaffold.class).isToggled() && ModuleManager.getModule(Scaffold.class).getSetting(3).asToggle().state);
	}
}