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
import net.minecraft.item.BlockItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

public class AutoBuilder extends Module {

	private final Set<BlockPos> renderBlocks = new LinkedHashSet<>();

	public AutoBuilder() {
		super("AutoBuilder", KEY_UNBOUND, Category.WORLD, "Places blocks under you",
				new SettingSlider("Range", 0, 7, 5, 1),
				new SettingMode("Mode", "Flat", "NomadHut", "Portal"),
				new SettingRotate(false).withDesc("Rotates when placing blocks"),
				new SettingSlider("BPT", 1, 10, 2, 0).withDesc("Blocks Per Tick, how many blocks to place per tick"),
				new SettingSlider("Down", 0, 3, 0, 0)
						//new SettingToggle("Placed", false).withDesc("Highlights blocks that are already placed"))
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

		Vec3d placeVec = mc.player.getPos().add(0, -0.85-getSetting(4).asSlider().getValue(), 0);
		Set<BlockPos> blocks = (mode == 0
				? new LinkedHashSet<>(Arrays.asList(new BlockPos(placeVec), new BlockPos(placeVec.add(range, 0, 0)), new BlockPos(placeVec.add(-range, 0, 0)),
				new BlockPos(placeVec.add(0, 0, range)), new BlockPos(placeVec.add(0, 0, -range))))
				: getSpiral(mode, new BlockPos(placeVec)));


		int cap = 0;
		for (BlockPos bp : blocks) {
			mc.player.inventory.selectedSlot = slot;
			if (getSetting(2).asRotate().state) {
				WorldUtils.facePosAuto(bp.getX() + 0.5, bp.getY() + 0.5, bp.getZ() + 0.5, getSetting(2).asRotate());
			}

			if (WorldUtils.placeBlock(bp, -1, false, false)) {
				cap++;
				if (cap >= (int) getSetting(3).asSlider().getValue()) return;
			}
		}
		mc.player.inventory.selectedSlot = prevSlot;

	}

	private Set<BlockPos> getSpiral(int size, BlockPos center) {
		Set<BlockPos> set = new LinkedHashSet<>(Arrays.asList(center));

		if (size == 0) return set;

		int step = 1;
		int neededSteps = size * 4;
		BlockPos currentPos = center.up();


		switch (getSetting(1).asMode().mode) {
			case 0:
				for (int l_X = -3; l_X <= 3; ++l_X)
					for (int l_Y = -3; l_Y <= 3; ++l_Y)
					{
						set.add(center.down().add(l_X, 0, l_Y));
					}

				break;
			case 1:
				set.add(currentPos.south(2).west().up());
				set.add(currentPos.south(2).west().up(1));
				set.add(currentPos.south(2).west().up(2));
				set.add(currentPos.south(2).up(2));
				set.add(currentPos.south(2).east().up());
				set.add(currentPos.south(2).east().up(1));
				set.add(currentPos.south(2).east().up(2));
				set.add(currentPos.south(2).east().up(3));
				set.add(currentPos.north(2));
				set.add(currentPos.west().north(2));
				set.add(currentPos.up(3));
				set.add(currentPos.up(3).north());
				set.add(currentPos.up(3).east());
				set.add(currentPos.up(3).south());
				set.add(currentPos.up(3).west());
				set.add(currentPos.up(3).north().east());
				set.add(currentPos.up(3).north().west());
				set.add(currentPos.up(3).south().east());
				set.add(currentPos.up(3).south().west());
				set.add(currentPos.up(2).north(2));
				set.add(currentPos.up(2).west().north(2));
				set.add(currentPos.up().west().north(2));
				set.add(currentPos.east().north(2));
				set.add(currentPos.east().north(2).up());
				set.add(currentPos.east().north(2).up(2));
				set.add(currentPos.north().east(2));
				set.add(currentPos.north().east(2).up());
				set.add(currentPos.north().east(2).up(2));
				set.add(currentPos.east(2));
				set.add(currentPos.east(2).up(2));
				set.add(currentPos.east(2).south());
				set.add(currentPos.east(2).south().up());
				set.add(currentPos.east(2).south().up(2));
				set.add(currentPos.north().west(2));
				set.add(currentPos.north().west(2).up());
				set.add(currentPos.north().west(2).up(2));
				set.add(currentPos.west(2));
				set.add(currentPos.west(2).up(2));
				set.add(currentPos.west(2).south());
				set.add(currentPos.west(2).south().up());
				set.add(currentPos.west(2).south().up(2));
				set.add(currentPos.west(2).south(2));
				break;
			case 2:
				set.add(currentPos.south().east());
				set.add(currentPos.south().east().east());
				set.add(currentPos.south());
				set.add(currentPos.south().east().east().up());
				set.add(currentPos.south().east().east().up().up());
				set.add(currentPos.south().east().east().up().up().up());
				set.add(currentPos.south().east().east().up().up().up().up());
				set.add(currentPos.south().east().east().up().up().up().up().west());
				set.add(currentPos.south().east().east().up().up().up().up().west().west());
				set.add(currentPos.south().east().up().up().up().up().west().west().west());
				set.add(currentPos.south().east().east().up().up().up().up().west().west().west().down());
				set.add(currentPos.south().east().east().up().up().up().up().west().west().west().down().down());
				set.add(currentPos.south().east().east().up().up().up().up().west().west().west().down().down().down());
				set.add(currentPos.south().east().east().up().up().up().up().west().west().west().down().down().down().down());
				break;

		}

		return set;
	}

}