package mac.hack.module.mods;

import mac.hack.event.events.EventReadPacket;
import mac.hack.event.events.EventTick;
import mac.hack.module.Category;
import mac.hack.module.Module;
import mac.hack.module.ModuleManager;
import mac.hack.setting.base.SettingMode;
import mac.hack.setting.base.SettingSlider;
import mac.hack.setting.base.SettingToggle;
import mac.hack.utils.EntityUtils;
import com.google.common.collect.Maps;
import com.google.common.eventbus.Subscribe;
import mac.hack.utils.FabricReflect;
import mac.hack.utils.WorldUtils;
import mac.hack.utils.file.MacFileMang;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.RaycastContext;

import java.util.ArrayList;
import java.util.List;

public class HighwayNuker extends Module {

    private List<Block> blockList = new ArrayList<>();

    public HighwayNuker() {
        super("HighwayNuker", KEY_UNBOUND, Category.WORLD, "Breaks blocks around you",
                new SettingMode("Mode: ", "Normal", "Multi", "Instant"),
                new SettingMode("Blocks: ", "1x3", "2x3", "3x3", "Highway"),
                new SettingSlider("Cooldown: ", 0, 4, 0, 0),
                new SettingToggle("All Blocks", true),
                new SettingToggle("Flatten", false),
                new SettingToggle("Rotate", false),
                new SettingToggle("NoParticles", false),
                new SettingMode("Sort: ", "Normal", "Hardness"),
                new SettingSlider("Multi: ", 1, 10, 2, 0));
    }

    public void onEnable() {
        blockList.clear();
        for (String s : MacFileMang.readFileLines("nukerblocks.txt"))
            blockList.add(Registry.BLOCK.get(new Identifier(s)));

        super.onEnable();
    }

    private BlockPos lastPlayerPos = null;

    private List<BlockPos> getBlocks() {
        int mode = getSettings().get(1).asMode().mode;
        List<BlockPos> blocks = new ArrayList<>();
        if (this.isToggled()) {
            switch (mode) {
                case 0:
                    blocks = get1x3();
                    break;
                case 1:
                    blocks = get2x3();
                    break;
                case 2:
                    blocks = getCube();
                    break;
                case 3:
                    blocks = getHighway4();
                    break;
            }
        }
        return blocks;
    }
    public boolean canSeeBlock(BlockPos pos) {
        double diffX = pos.getX() + 0.5 - mc.player.getCameraPosVec(mc.getTickDelta()).x;
        double diffY = pos.getY() + 0.5 - mc.player.getCameraPosVec(mc.getTickDelta()).y;
        double diffZ = pos.getZ() + 0.5 - mc.player.getCameraPosVec(mc.getTickDelta()).z;

        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);

        float yaw = mc.player.yaw + MathHelper.wrapDegrees((float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90 - mc.player.yaw);
        float pitch = mc.player.pitch + MathHelper.wrapDegrees((float) -Math.toDegrees(Math.atan2(diffY, diffXZ)) - mc.player.pitch);

        Vec3d rotation = new Vec3d(
                (double) (MathHelper.sin(-yaw * 0.017453292F) * MathHelper.cos(pitch * 0.017453292F)),
                (double) (-MathHelper.sin(pitch * 0.017453292F)),
                (double) (MathHelper.cos(-yaw * 0.017453292F) * MathHelper.cos(pitch * 0.017453292F)));

        Vec3d rayVec = mc.player.getCameraPosVec(mc.getTickDelta()).add(rotation.x * 6, rotation.y * 6, rotation.z * 6);
        return mc.world.raycast(new RaycastContext(mc.player.getCameraPosVec(mc.getTickDelta()),
                rayVec, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, mc.player))
                .getBlockPos().equals(pos);
    }

    @Subscribe
    public void onTick(EventTick event) {
        List<BlockPos> blocks = getBlocks();
        double range = 6;

//		/* Add blocks around player */
//		for (int x = (int) range; x >= (int) -range; x--) {
//			for (int y = (int) range; y >= (getSettings().get(4).toToggle().state ? 0 : (int) -range); y--) {
//				for (int z = (int) range; z >= (int) -range; z--) {
//					BlockPos pos = new BlockPos(mc.player.getPos().add(x, y + 0.1, z));
//					if (!canSeeBlock(pos) || mc.world.getBlockState(pos).getBlock() == Blocks.AIR || WorldUtils.isFluid(pos)) continue;
//					blocks.add(pos);
//				}
//			}
//		}

        AutoEat autoEat = (AutoEat) ModuleManager.getModule(AutoEat.class);

        if (autoEat.isEating()) return;

        if (blocks.isEmpty()) return;

        if (getSettings().get(6).asToggle().state) FabricReflect.writeField(
                mc.particleManager, Maps.newIdentityHashMap(), "field_3830", "particles");

        if (getSettings().get(7).asMode().mode == 1) blocks.sort((a, b) -> Float.compare(
                mc.world.getBlockState(a).getHardness(null, a), mc.world.getBlockState(b).getHardness(null, b)));

        /* Move the block under the player to last so it doesn't mine itself down without clearing everything above first */
        if (blocks.contains(mc.player.getBlockPos().down())) {
            blocks.remove(mc.player.getBlockPos().down());
            blocks.add(mc.player.getBlockPos().down());
        }

        int broken = 0;
        for (BlockPos pos : blocks) {
            if (!canSeeBlock(pos) || mc.world.getBlockState(pos).getBlock() != Blocks.NETHERRACK)
                continue;

            if (!getSettings().get(3).asToggle().state && !blockList.contains(mc.world.getBlockState(pos).getBlock()))
                continue;

            Vec3d vec = Vec3d.of(pos).add(0.5, 0.5, 0.5);

            if (mc.player.getPos().distanceTo(vec) > range + 0.5) continue;

            Direction dir = null;
            double dist = Double.MAX_VALUE;
            for (Direction d : Direction.values()) {
                double dist2 = mc.player.getPos().distanceTo(Vec3d.of(pos.offset(d)).add(0.5, 0.5, 0.5));
                if (dist2 > range || mc.world.getBlockState(pos.offset(d)).getBlock() != Blocks.AIR || dist2 > dist)
                    continue;
                dist = dist2;
                dir = d;
            }

            if (dir == null) continue;

            if (getSettings().get(5).asToggle().state) {
                float[] prevRot = new float[]{mc.player.yaw, mc.player.pitch};
                WorldUtils.facePosPacket(vec.x, vec.y, vec.z);
                mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.LookOnly(
                        mc.player.yaw, mc.player.pitch, mc.player.isOnGround()));
                mc.player.yaw = prevRot[0];
                mc.player.pitch = prevRot[1];
            }

            if (getSettings().get(0).asMode().mode == 1) mc.interactionManager.attackBlock(pos, dir);
            else mc.interactionManager.attackBlock(pos, dir);

//            mc.player.swingHand(Hand.MAIN_HAND);

            broken++;
            if (getSettings().get(0).asMode().mode == 0
                    || (getSettings().get(0).asMode().mode == 1 && broken >= (int) getSettings().get(8).asSlider().getValue()))
                return;
        }
    }

    public List<BlockPos> getCube() {
        List<BlockPos> cubeBlocks = new ArrayList<>();
        BlockPos playerPos = new BlockPos(Math.floor(mc.player.getX()), Math.floor(mc.player.getY()), Math.floor(mc.player.getZ()));
        if (lastPlayerPos == null || !lastPlayerPos.equals(playerPos)) {
            switch (EntityUtils.GetFacing()) {
                case East:
                    for (int i = 0; i < 7; ++i) {
                        cubeBlocks.add(playerPos.east());
                        cubeBlocks.add(playerPos.east().up());
                        cubeBlocks.add(playerPos.east().up().up());
                        cubeBlocks.add(playerPos.east().north());
                        cubeBlocks.add(playerPos.east().north().up());
                        cubeBlocks.add(playerPos.east().north().up().up());
                        cubeBlocks.add(playerPos.east().south());
                        cubeBlocks.add(playerPos.east().south().up());
                        cubeBlocks.add(playerPos.east().south().up().up());

                        playerPos = new BlockPos(playerPos).east();
                    }
                    break;
                case North:
                    for (int i = 0; i < 7; ++i) {
                        cubeBlocks.add(playerPos.north());
                        cubeBlocks.add(playerPos.north().up());
                        cubeBlocks.add(playerPos.north().up().up());
                        cubeBlocks.add(playerPos.north().east());
                        cubeBlocks.add(playerPos.north().east().up());
                        cubeBlocks.add(playerPos.north().east().up().up());
                        cubeBlocks.add(playerPos.north().west());
                        cubeBlocks.add(playerPos.north().west().up());
                        cubeBlocks.add(playerPos.north().west().up().up());

                        playerPos = new BlockPos(playerPos).north();
                    }
                    break;
                case South:
                    for (int i = 0; i < 7; ++i) {
                        cubeBlocks.add(playerPos.south());
                        cubeBlocks.add(playerPos.south().up());
                        cubeBlocks.add(playerPos.south().up().up());
                        cubeBlocks.add(playerPos.south().west());
                        cubeBlocks.add(playerPos.south().west().up());
                        cubeBlocks.add(playerPos.south().west().up().up());
                        cubeBlocks.add(playerPos.south().east());
                        cubeBlocks.add(playerPos.south().east().up());
                        cubeBlocks.add(playerPos.south().east().up().up());

                        playerPos = new BlockPos(playerPos).south();
                    }
                    break;
                case West:
                    for (int i = 0; i < 7; ++i) {
                        cubeBlocks.add(playerPos.west());
                        cubeBlocks.add(playerPos.west().up());
                        cubeBlocks.add(playerPos.west().up().up());
                        cubeBlocks.add(playerPos.west().south());
                        cubeBlocks.add(playerPos.west().south().up());
                        cubeBlocks.add(playerPos.west().south().up().up());
                        cubeBlocks.add(playerPos.west().north());
                        cubeBlocks.add(playerPos.west().north().up());
                        cubeBlocks.add(playerPos.west().north().up().up());


                        playerPos = new BlockPos(playerPos).west();
                    }
                    break;
            }
        }
        return cubeBlocks;
    }

    public List<BlockPos> get1x3() {
        List<BlockPos> cubeBlocks = new ArrayList<>();
        BlockPos playerPos = new BlockPos(Math.floor(mc.player.getX()), Math.floor(mc.player.getY()), Math.floor(mc.player.getZ()));
        if (lastPlayerPos == null || !lastPlayerPos.equals(playerPos)) {
            switch (EntityUtils.GetFacing()) {
                case East:
                    for (int i = 0; i < 7; ++i) {
                        cubeBlocks.add(playerPos.east());
                        cubeBlocks.add(playerPos.east().up());
                        cubeBlocks.add(playerPos.east().up().up());
                        playerPos = new BlockPos(playerPos).east();
                    }
                    break;
                case North:
                    for (int i = 0; i < 7; ++i) {
                        cubeBlocks.add(playerPos.north());
                        cubeBlocks.add(playerPos.north().up());
                        cubeBlocks.add(playerPos.north().up().up());

                        playerPos = new BlockPos(playerPos).north();
                    }
                    break;
                case South:
                    for (int i = 0; i < 7; ++i) {
                        cubeBlocks.add(playerPos.south());
                        cubeBlocks.add(playerPos.south().up());
                        cubeBlocks.add(playerPos.south().up().up());

                        playerPos = new BlockPos(playerPos).south();
                    }
                    break;
                case West:
                    for (int i = 0; i < 7; ++i) {
                        cubeBlocks.add(playerPos.west());
                        cubeBlocks.add(playerPos.west().up());
                        cubeBlocks.add(playerPos.west().up().up());


                        playerPos = new BlockPos(playerPos).west();
                    }
                    break;
            }
        }
        return cubeBlocks;
    }

    public List<BlockPos> get2x3() {
        List<BlockPos> cubeBlocks = new ArrayList<>();
        BlockPos playerPos = new BlockPos(Math.floor(mc.player.getX()), Math.floor(mc.player.getY()), Math.floor(mc.player.getZ()));
        if (lastPlayerPos == null || !lastPlayerPos.equals(playerPos)) {
            switch (EntityUtils.GetFacing()) {
                case East:
                    for (int i = 0; i < 7; ++i) {
                        cubeBlocks.add(playerPos.east());
                        cubeBlocks.add(playerPos.east().up());
                        cubeBlocks.add(playerPos.east().up().up());
                        cubeBlocks.add(playerPos.east().north());
                        cubeBlocks.add(playerPos.east().north().up());
                        cubeBlocks.add(playerPos.east().north().up().up());
                        playerPos = new BlockPos(playerPos).east();
                    }
                    break;
                case North:
                    for (int i = 0; i < 7; ++i) {
                        cubeBlocks.add(playerPos.north());
                        cubeBlocks.add(playerPos.north().up());
                        cubeBlocks.add(playerPos.north().up().up());
                        cubeBlocks.add(playerPos.north().east());
                        cubeBlocks.add(playerPos.north().east().up());
                        cubeBlocks.add(playerPos.north().east().up().up());
                        playerPos = new BlockPos(playerPos).north();
                    }
                    break;
                case South:
                    for (int i = 0; i < 7; ++i) {
                        cubeBlocks.add(playerPos.south());
                        cubeBlocks.add(playerPos.south().up());
                        cubeBlocks.add(playerPos.south().up().up());
                        cubeBlocks.add(playerPos.south().west());
                        cubeBlocks.add(playerPos.south().west().up());
                        cubeBlocks.add(playerPos.south().west().up().up());
                        playerPos = new BlockPos(playerPos).south();
                    }
                    break;
                case West:
                    for (int i = 0; i < 7; ++i) {
                        cubeBlocks.add(playerPos.west());
                        cubeBlocks.add(playerPos.west().up());
                        cubeBlocks.add(playerPos.west().up().up());
                        cubeBlocks.add(playerPos.west().south());
                        cubeBlocks.add(playerPos.west().south().up());
                        cubeBlocks.add(playerPos.west().south().up().up());
                        playerPos = new BlockPos(playerPos).west();
                    }
                    break;
            }
        }
        return cubeBlocks;
    }
    public List<BlockPos> getHighway4() {
        List<BlockPos> cubeBlocks = new ArrayList<>();
        BlockPos playerPos = new BlockPos(Math.floor(mc.player.getX()), Math.floor(mc.player.getY()), Math.floor(mc.player.getZ()));
        switch (EntityUtils.GetFacing()) {
            case East:
                for (int i = 0; i < 4; ++i) {
                    cubeBlocks.add(playerPos.east());
                    cubeBlocks.add(playerPos.east().up());
                    cubeBlocks.add(playerPos.east().up(2));
                    cubeBlocks.add(playerPos.east().up(3));
                    cubeBlocks.add(playerPos.east().south());
                    cubeBlocks.add(playerPos.east().south().up());
                    cubeBlocks.add(playerPos.east().south().up(2));
                    cubeBlocks.add(playerPos.east().south().up(3));
                    cubeBlocks.add(playerPos.east().south(2).up());
                    cubeBlocks.add(playerPos.east().south(2).up(2));
                    cubeBlocks.add(playerPos.east().south(2).up(3));
                    cubeBlocks.add(playerPos.east().north());
                    cubeBlocks.add(playerPos.east().north().up());
                    cubeBlocks.add(playerPos.east().north().up(2));
                    cubeBlocks.add(playerPos.east().north().up(3));
                    cubeBlocks.add(playerPos.east().north(2));
                    cubeBlocks.add(playerPos.east().north(2).up());
                    cubeBlocks.add(playerPos.east().north(2).up(2));
                    cubeBlocks.add(playerPos.east().north(2).up(3));
                    cubeBlocks.add(playerPos.east().north(3).up());
                    cubeBlocks.add(playerPos.east().north(3).up(2));
                    cubeBlocks.add(playerPos.east().north(3).up(3));
                    playerPos = new BlockPos(playerPos).east();
                }
                break;
            case North:
                for (int i = 0; i < 4; ++i) {
                    cubeBlocks.add(playerPos.north());
                    cubeBlocks.add(playerPos.north().up());
                    cubeBlocks.add(playerPos.north().up(2));
                    cubeBlocks.add(playerPos.north().up(3));
                    cubeBlocks.add(playerPos.north().east());
                    cubeBlocks.add(playerPos.north().east().up());
                    cubeBlocks.add(playerPos.north().east().up(2));
                    cubeBlocks.add(playerPos.north().east().up(3));
                    cubeBlocks.add(playerPos.north().east(2).up());
                    cubeBlocks.add(playerPos.north().east(2).up(2));
                    cubeBlocks.add(playerPos.north().east(2).up(3));
                    cubeBlocks.add(playerPos.north().west());
                    cubeBlocks.add(playerPos.north().west().up());
                    cubeBlocks.add(playerPos.north().west().up(2));
                    cubeBlocks.add(playerPos.north().west().up(3));
                    cubeBlocks.add(playerPos.north().west(2));
                    cubeBlocks.add(playerPos.north().west(2).up());
                    cubeBlocks.add(playerPos.north().west(2).up(2));
                    cubeBlocks.add(playerPos.north().west(2).up(3));
                    cubeBlocks.add(playerPos.north().west(3).up());
                    cubeBlocks.add(playerPos.north().west(3).up(2));
                    cubeBlocks.add(playerPos.north().west(3).up(3));
                    playerPos = new BlockPos(playerPos).north();
                }
                break;
            case South:
                for (int i = 0; i < 4; ++i) {
                    cubeBlocks.add(playerPos.south());
                    cubeBlocks.add(playerPos.south().up());
                    cubeBlocks.add(playerPos.south().up(2));
                    cubeBlocks.add(playerPos.south().up(3));
                    cubeBlocks.add(playerPos.south().west());
                    cubeBlocks.add(playerPos.south().west().up());
                    cubeBlocks.add(playerPos.south().west().up(2));
                    cubeBlocks.add(playerPos.south().west().up(3));
                    cubeBlocks.add(playerPos.south().west(2).up());
                    cubeBlocks.add(playerPos.south().west(2).up(2));
                    cubeBlocks.add(playerPos.south().west(2).up(3));
                    cubeBlocks.add(playerPos.south().east());
                    cubeBlocks.add(playerPos.south().east().up());
                    cubeBlocks.add(playerPos.south().east().up(2));
                    cubeBlocks.add(playerPos.south().east().up(3));
                    cubeBlocks.add(playerPos.south().east(2));
                    cubeBlocks.add(playerPos.south().east(2).up());
                    cubeBlocks.add(playerPos.south().east(2).up(2));
                    cubeBlocks.add(playerPos.south().east(2).up(3));
                    cubeBlocks.add(playerPos.south().east(3).up());
                    cubeBlocks.add(playerPos.south().east(3).up(2));
                    cubeBlocks.add(playerPos.south().east(3).up(3));
                    playerPos = new BlockPos(playerPos).south();
                }
                break;
            case West:
                for (int i = 0; i < 4; ++i) {
                    cubeBlocks.add(playerPos.west());
                    cubeBlocks.add(playerPos.west().up());
                    cubeBlocks.add(playerPos.west().up(2));
                    cubeBlocks.add(playerPos.west().up(3));
                    cubeBlocks.add(playerPos.west().north());
                    cubeBlocks.add(playerPos.west().north().up());
                    cubeBlocks.add(playerPos.west().north().up(2));
                    cubeBlocks.add(playerPos.west().north().up(3));
                    cubeBlocks.add(playerPos.west().north(2).up());
                    cubeBlocks.add(playerPos.west().north(2).up(2));
                    cubeBlocks.add(playerPos.west().north(2).up(3));
                    cubeBlocks.add(playerPos.west().south());
                    cubeBlocks.add(playerPos.west().south().up());
                    cubeBlocks.add(playerPos.west().south().up(2));
                    cubeBlocks.add(playerPos.west().south().up(3));
                    cubeBlocks.add(playerPos.west().south(2));
                    cubeBlocks.add(playerPos.west().south(2).up());
                    cubeBlocks.add(playerPos.west().south(2).up(2));
                    cubeBlocks.add(playerPos.west().south(2).up(3));
                    cubeBlocks.add(playerPos.west().south(3).up());
                    cubeBlocks.add(playerPos.west().south(3).up(2));
                    cubeBlocks.add(playerPos.west().south(3).up(3));
                    playerPos = new BlockPos(playerPos).west();
                }
                break;
        }
        return cubeBlocks;
    }

}