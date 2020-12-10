package mac.hack.module.mods;

import mac.hack.event.events.EventTick;
import mac.hack.module.Category;
import mac.hack.module.Module;
import mac.hack.setting.base.SettingSlider;
import mac.hack.setting.base.SettingToggle;
import mac.hack.utils.WorldUtils;
import com.google.common.eventbus.Subscribe;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.passive.LlamaEntity;
import net.minecraft.entity.vehicle.MinecartEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class EntityControl extends Module {

	public EntityControl() {
		super("EntityControl", KEY_UNBOUND, Category.MOVEMENT, "Manipulate Entities.",
				new SettingToggle("EntitySpeed", true),
				new SettingSlider("Speed", 0, 50, 1.2, 2),
				new SettingToggle("EntityFly", false),
				new SettingSlider("Ascend", 0, 2, 0.3, 2),
				new SettingSlider("Descend", 0, 2, 0.5, 2),
				new SettingToggle("Ground Snap", false),
				new SettingToggle("AntiStuck", false));
	}

	@Subscribe
	public void onTick(EventTick event) {
		if (mc.player.getVehicle() == null) return;

		Entity e = mc.player.getVehicle();
		e.yaw = mc.player.yaw;
		double speed = getSetting(1).asSlider().getValue();

		if (getSetting(6).asToggle().state && e instanceof HorseBaseEntity) {
			HorseBaseEntity h = (HorseBaseEntity) e;
			h.saddle(null);
			h.setTame(true);
			h.setAiDisabled(true);
		}

		if (e instanceof LlamaEntity) {
			((LlamaEntity) e).headYaw = mc.player.headYaw;
		}

		double forward = mc.player.forwardSpeed;
		double strafe = mc.player.sidewaysSpeed;
		float yaw = mc.player.yaw;

		if (getSetting(0).asToggle().state) {
			if ((forward == 0.0D) && (strafe == 0.0D)) {
				e.setVelocity(0, e.getVelocity().y, 0);
			} else {
				if (forward != 0.0D) {
					if (strafe > 0.0D) {
						yaw += (forward > 0.0D ? -45 : 45);
					} else if (strafe < 0.0D) yaw += (forward > 0.0D ? 45 : -45);
					strafe = 0.0D;
					if (forward > 0.0D) {
						forward = 1.0D;
					} else if (forward < 0.0D) forward = -1.0D;
				}
				e.setVelocity((forward * speed * Math.cos(Math.toRadians(yaw + 90.0F)) + strafe * speed * Math.sin(Math.toRadians(yaw + 90.0F))), e.getVelocity().y,
						forward * speed * Math.sin(Math.toRadians(yaw + 90.0F)) - strafe * speed * Math.cos(Math.toRadians(yaw + 90.0F)));
				if (e instanceof MinecartEntity) {
					MinecartEntity em = (MinecartEntity) e;
					em.setVelocity((forward * speed * Math.cos(Math.toRadians(yaw + 90.0F)) + strafe * speed * Math.sin(Math.toRadians(yaw + 90.0F))), em.getVelocity().y, (forward * speed * Math.sin(Math.toRadians(yaw + 90.0F)) - strafe * speed * Math.cos(Math.toRadians(yaw + 90.0F))));
				}
			}
		}

		if (getSetting(2).asToggle().state) {
			if (mc.options.keyJump.isPressed()) {
				e.setVelocity(e.getVelocity().x, getSetting(3).asSlider().getValue(), e.getVelocity().z);
			} else {
				e.setVelocity(e.getVelocity().x, -getSetting(4).asSlider().getValue(), e.getVelocity().z);
			}
		}

		if (getSetting(5).asToggle().state) {
			BlockPos p = new BlockPos(e.getPos());
			if (!WorldUtils.NONSOLID_BLOCKS.contains(mc.world.getBlockState(p.down()).getBlock()) && e.fallDistance > 0.01) {
				e.setVelocity(e.getVelocity().x, -1, e.getVelocity().z);
			}
		}

		if (getSetting(6).asToggle().state) {
			Vec3d vel = e.getVelocity().multiply(2);
			if (!WorldUtils.isBoxEmpty(WorldUtils.moveBox(e.getBoundingBox(), vel.x, 0, vel.z))) {
				for (int i = 2; i < 10; i++) {
					if (WorldUtils.isBoxEmpty(WorldUtils.moveBox(e.getBoundingBox(), vel.x / i, 0, vel.z / i))) {
						e.setVelocity(vel.x / i / 2, vel.y, vel.z / i / 2);
						break;
					}
				}
			}
		}
	}
}