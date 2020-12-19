package mac.hack.module.mods;

import com.google.common.collect.Sets;
import com.google.common.eventbus.Subscribe;

import mac.hack.event.events.EventTick;
import mac.hack.setting.base.SettingMode;
import mac.hack.setting.base.SettingSlider;
import mac.hack.setting.base.SettingToggle;
import mac.hack.module.Category;
import mac.hack.module.Module;
import mac.hack.utils.MacLogger;
import mac.hack.utils.WorldUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class Surround extends Module {

	public Surround() {
		super("Surround", KEY_UNBOUND, Category.COMBAT, "Surrounds yourself with obsidian",
				new SettingMode("Mode: ", "1x1", "Fit").withDesc("Mode, 1x1 places 4 blocks around you, fit fits the blocks around you so it doesn't place inside of you"),
				new SettingToggle("Autocenter", false).withDesc("Autocenters you to the nearest block"),
				new SettingToggle("Keep on", true).withDesc("Keeps the module on after placing the obsidian"),
				new SettingToggle("Sneak", true).withDesc("Disables the module if you jump"),
				new SettingSlider("BPT: ", 1, 8, 2, 0).withDesc("Blocks per tick, how many blocks to place per tick"),
				new SettingToggle("Rotate", false).withDesc("Rotates serverside when placing"),
				new SettingToggle("AirPlace", false));
	}
	
	@Subscribe
	public void onTick(EventTick event) {
		int obby = -1;
		for (int i = 0; i < 9; i++) {
			if (mc.player.inventory.getStack(i).getItem() == Items.OBSIDIAN) {
				obby = i;
				break;
			}
		}
		
		if (obby == -1) {
			MacLogger.errorMessage("Ran out of obsidian!");
			setToggled(false);
			return;
		}
		
		placeTick(obby);
	}
	
	private void placeTick(int obsidian) {
		int cap = 0;
		if (getSetting(3).asToggle().state) {
			if (mc.player.isSneaking()) {
				if (getSettings().get(1).asToggle().state) {
					Vec3d centerPos = Vec3d.of(mc.player.getBlockPos()).add(0.5, 0.5, 0.5);
					mc.player.updatePosition(centerPos.x, centerPos.y, centerPos.z);
					mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionOnly(centerPos.x, centerPos.y, centerPos.z, mc.player.isOnGround()));
				}
				if (getSettings().get(0).asMode().mode == 0) {
					for (BlockPos b : new BlockPos[]{
							mc.player.getBlockPos().north(), mc.player.getBlockPos().east(),
							mc.player.getBlockPos().south(), mc.player.getBlockPos().west()}) {

						if (cap >= (int) getSettings().get(4).asSlider().getValue()) {
							return;
						}

						if (WorldUtils.placeBlock(b, obsidian, getSettings().get(5).asToggle().state, false)) {
							cap++;
						}
					}
				} else {
					Box box = mc.player.getBoundingBox();
					for (BlockPos b : Sets.newHashSet(
							new BlockPos(box.minX - 1, box.minY, box.minZ), new BlockPos(box.minX, box.minY, box.minZ - 1),
							new BlockPos(box.maxX + 1, box.minY, box.minZ), new BlockPos(box.maxX, box.minY, box.minZ - 1),
							new BlockPos(box.minX - 1, box.minY, box.maxZ), new BlockPos(box.minX, box.minY, box.maxZ + 1),
							new BlockPos(box.maxX + 1, box.minY, box.maxZ), new BlockPos(box.maxX, box.minY, box.maxZ + 1))) {

						if (cap >= (int) getSettings().get(4).asSlider().getValue()) {
							return;
						}
						if (getSetting(6).asToggle().state) {
							if (WorldUtils.placeBlock(b, obsidian, getSettings().get(5).asToggle().state, false)) {
								cap++;
							}
						} else if (!getSetting(6).asToggle().state) {
							mc.interactionManager.interactBlock(mc.player, mc.world, Hand.MAIN_HAND, new BlockHitResult(Vec3d.of(b), Direction.DOWN, b, true));
							cap++;
						}
					}
				}
			}
		}
		else {
			if (getSettings().get(1).asToggle().state) {
				Vec3d centerPos = Vec3d.of(mc.player.getBlockPos()).add(0.5, 0.5, 0.5);
				mc.player.updatePosition(centerPos.x, centerPos.y, centerPos.z);
				mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionOnly(centerPos.x, centerPos.y, centerPos.z, mc.player.isOnGround()));
			}
			if (getSettings().get(0).asMode().mode == 0) {
				for (BlockPos b : new BlockPos[]{
						mc.player.getBlockPos().north(), mc.player.getBlockPos().east(),
						mc.player.getBlockPos().south(), mc.player.getBlockPos().west()}) {

					if (cap >= (int) getSettings().get(4).asSlider().getValue()) {
						return;
					}

					if (WorldUtils.placeBlock(b, obsidian, getSettings().get(5).asToggle().state, false)) {
						cap++;
					}
				}
			} else {
				Box box = mc.player.getBoundingBox();
				for (BlockPos b : Sets.newHashSet(
						new BlockPos(box.minX - 1, box.minY, box.minZ), new BlockPos(box.minX, box.minY, box.minZ - 1),
						new BlockPos(box.maxX + 1, box.minY, box.minZ), new BlockPos(box.maxX, box.minY, box.minZ - 1),
						new BlockPos(box.minX - 1, box.minY, box.maxZ), new BlockPos(box.minX, box.minY, box.maxZ + 1),
						new BlockPos(box.maxX + 1, box.minY, box.maxZ), new BlockPos(box.maxX, box.minY, box.maxZ + 1))) {

					if (cap >= (int) getSettings().get(4).asSlider().getValue()) {
						return;
					}
					if (getSetting(6).asToggle().state) {
						if (WorldUtils.placeBlock(b, obsidian, getSettings().get(5).asToggle().state, false)) {
							cap++;
						}
					} else if (!getSetting(6).asToggle().state) {
						mc.interactionManager.interactBlock(mc.player, mc.world, Hand.MAIN_HAND, new BlockHitResult(Vec3d.of(b), Direction.DOWN, b, true));
						cap++;
					}
				}
			}
		}
	}

}
