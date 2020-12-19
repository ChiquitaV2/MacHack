package mac.hack.module.mods;

import com.google.common.collect.Streams;
import com.google.common.eventbus.Subscribe;
import mac.hack.MacHack;
import mac.hack.event.events.EventTick;
import mac.hack.module.Category;
import mac.hack.module.Module;
import mac.hack.setting.base.SettingSlider;
import mac.hack.setting.base.SettingToggle;
import mac.hack.setting.other.SettingRotate;
import mac.hack.utils.EntityUtils;
import mac.hack.utils.WorldUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket.Mode;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.Hand;
import java.util.List;
import java.util.stream.Collectors;

public class Trident extends Module {

	private int delay = 0;
	private boolean flag;
	private double pos;

	public Trident() {
		super("Trident", KEY_UNBOUND, Category.COMBAT, "Automatically attacks entities",
				new SettingToggle("Players", true),
				new SettingToggle("Mobs", false),
				new SettingToggle("Animals", false),
				new SettingToggle("Armor Stands", false),
				new SettingRotate(true),
				new SettingSlider("Range", 0, 6, 4.25, 2),
				new SettingToggle("Walk", true),
				new SettingToggle("step", true));
	}
	private int slot;
	@Subscribe
	public void onTick(EventTick event) {
		delay++;
		int reqDelay = (int) 4.25;
		slot = mc.player.inventory.selectedSlot;
		if (mc.player.inventory.getMainHandStack().getItem().equals(Items.TRIDENT)) {
			List<Entity> targets = Streams.stream(mc.world.getEntities())
					.filter(e -> (e instanceof PlayerEntity && getSetting(0).asToggle().state
							&& !MacHack.friendMang.has(e.getName().asString()))
							|| (e instanceof Monster && getSetting(1).asToggle().state)
							|| (EntityUtils.isAnimal(e) && getSetting(2).asToggle().state)
							|| (e instanceof ArmorStandEntity && getSetting(3).asToggle().state))
					.sorted((a, b) -> Float.compare(a.distanceTo(mc.player), b.distanceTo(mc.player))).collect(Collectors.toList());

			for (Entity e : targets) {
				if (mc.player.distanceTo(e) > getSetting(5).asSlider().getValue()
						|| ((LivingEntity) e).getHealth() <= 0 || e.getEntityName().equals(mc.getSession().getUsername()) || e == mc.player.getVehicle())
					continue;

				if (getSetting(4).asRotate().state) {
					WorldUtils.facePosAuto(e.getX(), e.getY() + e.getHeight() / 2, e.getZ(), getSetting(4).asRotate());
				}

				if (mc.player.getAttackCooldownProgress(mc.getTickDelta()) == 1.0f) {
					boolean wasSprinting = mc.player.isSprinting();

					if (wasSprinting)
						mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, Mode.STOP_SPRINTING));

					mc.interactionManager.attackEntity(mc.player, e);
					mc.player.swingHand(Hand.MAIN_HAND);

					if (wasSprinting)
						mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, Mode.START_SPRINTING));

					delay = 0;
				}

			}
			if (getSetting(6).asToggle().state){
				mc.options.keyForward.setPressed(true);
			}
			if (getSetting(7).asToggle().state){
				if (!mc.player.horizontalCollision && flag) {
					mc.player.setVelocity(mc.player.getVelocity().x, -0.1, mc.player.getVelocity().z);
				} else if (mc.player.horizontalCollision) {
					mc.player.setVelocity(mc.player.getVelocity().x, 1, mc.player.getVelocity().z);
					mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket(true));
					mc.player.jump();
					flag = true;
				}
				if (!mc.player.horizontalCollision) flag = false;
			}
		}
	}
	public void onDisable() {
		mc.player.stepHeight = 0.5F;
		mc.options.keyForward.setPressed(false);
		super.onDisable();
	}
}
