package mac.hack.module.mods;

import mac.hack.event.events.EventTick;
import mac.hack.event.events.EventWorldRender;
import mac.hack.module.Category;
import mac.hack.module.Module;
import mac.hack.setting.base.SettingMode;
import mac.hack.setting.base.SettingSlider;
import mac.hack.setting.base.SettingToggle;
import mac.hack.setting.other.SettingRotate;
import mac.hack.utils.RenderUtils;
import mac.hack.utils.WorldUtils;
import com.google.common.eventbus.Subscribe;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.BlockItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

public class Scaffold extends Module {

	private final Set<BlockPos> renderBlocks = new LinkedHashSet<>();

	public Scaffold() {
		super("Scaffold", KEY_UNBOUND, Category.WORLD, "Places blocks under you",
				new SettingSlider("Range", 0, 1, 0.3, 1),
				new SettingMode("Mode", "Normal", "3x3", "5x5", "7x7"),
				new SettingRotate(false).withDesc("Rotates when placing blocks"),
				new SettingToggle("Tower", true).withDesc("Makes scaffolding straight up much easier"),
				new SettingToggle("SafeWalk", true).withDesc("Prevents you from walking of edges when scaffold is on"),
				new SettingToggle("Highlight", false).withDesc("Highlights the blocks you are placing").withChildren(
						new SettingSlider("R: ", 0.0D, 255.0D, 255.0D, 0),
						new SettingSlider("G: ", 0.0D, 255.0D, 255.0D, 0),
						new SettingSlider("B: ", 0.0D, 255.0D, 255.0D, 0),
						new SettingToggle("Placed", false).withDesc("Highlights blocks that are already placed")),
				new SettingSlider("BPT", 1, 10, 2, 0).withDesc("Blocks Per Tick, how many blocks to place per tick"),
				new SettingSlider("Down", 0, 3, 0, 0)
		);
	}

	@Subscribe
	public void onTick(EventTick event) {
		renderBlocks.clear();

		int slot = -1;
		int prevSlot = mc.player.inventory.selectedSlot;

		if (mc.player.inventory.getMainHandStack().getItem() instanceof BlockItem) {
			slot = mc.player.inventory.selectedSlot;
		} else for (int i = 0; i < 9; i++) {
			if (mc.player.inventory.getStack(i).getItem() instanceof BlockItem) {
				slot = i;
				break;
			}
		}

		if (slot == -1) return;

		double range = getSetting(0).asSlider().getValue();
		int mode = getSetting(1).asMode().mode;

		Vec3d placeVec = mc.player.getPos().add(0, -0.85-getSetting(7).asSlider().getValue(), 0);
		Set<BlockPos> blocks = (mode == 0
				? new LinkedHashSet<>(Arrays.asList(new BlockPos(placeVec), new BlockPos(placeVec.add(range, 0, 0)), new BlockPos(placeVec.add(-range, 0, 0)),
				new BlockPos(placeVec.add(0, 0, range)), new BlockPos(placeVec.add(0, 0, -range))))
				: getSpiral(mode, new BlockPos(placeVec)));

		// Don't bother doing anything if there aren't any blocks to place on
		boolean empty = true;
		for (BlockPos bp : blocks) {
			if (WorldUtils.canPlaceBlock(bp)) {
				empty = false;
				break;
			}
		}

		if (empty) return;

		if (getSetting(3).asToggle().state
				&& WorldUtils.NONSOLID_BLOCKS.contains(mc.world.getBlockState(mc.player.getBlockPos().down()).getBlock())
				&& !WorldUtils.NONSOLID_BLOCKS.contains(mc.world.getBlockState(mc.player.getBlockPos().down(2)).getBlock())) {
			double toBlock = (int) mc.player.getY() - mc.player.getY();

			if (toBlock < 0.05 && InputUtil.isKeyPressed(
					mc.getWindow().getHandle(), InputUtil.fromTranslationKey(mc.options.keyJump.getBoundKeyTranslationKey()).getCode())) {
				mc.player.setVelocity(mc.player.getVelocity().x, -toBlock, mc.player.getVelocity().z);
				mc.player.jump();
			}
		}

		if (getSetting(5).asToggle().state) {
			for (BlockPos bp : blocks) {
				if (getSetting(5).asToggle().getChild(3).asToggle().state || WorldUtils.isBlockEmpty(bp)) {
					renderBlocks.add(bp);
				}
			}
		}

		int cap = 0;
		for (BlockPos bp : blocks) {
			mc.player.inventory.selectedSlot = slot;
			if (getSetting(2).asRotate().state) {
				WorldUtils.facePosAuto(bp.getX() + 0.5, bp.getY() + 0.5, bp.getZ() + 0.5, getSetting(2).asRotate());
			}

			if (
					WorldUtils.placeBlock(bp, -1, false, false)
			) {
				cap++;
				if (cap >= (int) getSetting(6).asSlider().getValue()) return;
			}
		}
		mc.player.inventory.selectedSlot = prevSlot;

	}

	@Subscribe
	public void onWorldRender(EventWorldRender event) {
		if (getSetting(5).asToggle().state) {
			float r =  (float) (this.getSetting(5).asToggle().getChild(1).asSlider().getValue() / 255.0D);
			float g =  (float) (this.getSetting(5).asToggle().getChild(2).asSlider().getValue() / 255.0D);
			float b =  (float) (this.getSetting(5).asToggle().getChild(2).asSlider().getValue() / 255.0D);
			for (BlockPos bp : renderBlocks) {
				RenderUtils.drawFilledBox(bp, r, g, b, 0.7f);
			}
		}
	}


	private Set<BlockPos> getSpiral(int size, BlockPos center) {
		Set<BlockPos> set = new LinkedHashSet<>(Arrays.asList(center));

		if (size == 0) return set;

		int step = 1;
		int neededSteps = size * 4;
		BlockPos currentPos = center;
		for (int i = 0; i <= neededSteps; i++) {
			// Do 1 less step on the last side to not overshoot the spiral
			if (i == neededSteps) step--;

			for (int j = 0; j < step; j++) {
				if (i % 4 == 0) currentPos = currentPos.add(-1, 0, 0);
				else if (i % 4 == 1) currentPos = currentPos.add(0, 0, -1);
				else if (i % 4 == 2) currentPos = currentPos.add(1, 0, 0);
				else currentPos = currentPos.add(0, 0, 1);

				set.add(currentPos);
			}

			if (i % 2 != 0) step++;
		}

		return set;
	}
}