package mac.hack.module.mods;


import com.google.common.collect.Streams;
import com.google.common.eventbus.Subscribe;
import mac.hack.MacHack;
import mac.hack.event.events.EventTick;
import mac.hack.event.events.EventWorldRender;
import mac.hack.module.Category;
import mac.hack.module.Module;
import mac.hack.setting.base.SettingSlider;
import mac.hack.setting.base.SettingToggle;
import mac.hack.utils.CrystalUtils;
import mac.hack.utils.EntityUtils;
import mac.hack.utils.RenderUtils;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

public class EpearlAC extends Module {
    private final int delay = 0;
    public Entity target;
    ArrayList<BlockPos> blocks = new ArrayList<BlockPos>();
    ArrayList<BlockPos> blocksRange;
    int oldSlot = -1;
    int counter = 0;
    private BlockPos targetBlock;


//TODO: antisuicide
//TODO: silent crystal swap

    public EpearlAC() {
        super("EpearlAC", KEY_UNBOUND, Category.COMBAT, "Auto crystal chungus",
                new SettingToggle("Players", true),
                new SettingToggle("Mobs", true),
                new SettingToggle("Animals", true),
                new SettingToggle("AutoSwitch", true),
                new SettingSlider("MaxSelfDMG", 0, 20, 4, 0),
                new SettingSlider("MinEnemyDMG", 0, 20, 6, 0),
                new SettingSlider("FacePlace", 0, 20, 6, 0),
                new SettingSlider("WaitTicks", 0, 20, 5, 0),
                new SettingToggle("1.13 Place", true),
                new SettingSlider("Range", 0, 6, 4, 2),
                new SettingToggle("render", true).withChildren(
                        new SettingSlider("R: ", 0.0D, 255.0D, 255.0D, 0),
                        new SettingSlider("G: ", 0.0D, 255.0D, 255.0D, 0),
                        new SettingSlider("B: ", 0.0D, 255.0D, 255.0D, 0))
        );
        ArrayList<BlockPos> blocksRangeAdder = new ArrayList<BlockPos>();
        int range = (int) getSetting(9).asSlider().getValue();
        for(int x = range; x >= -range; x--)
        {
            for(int y = range; y >= -range; y--)
            {
                for(int z = range; z >= -range; z--)
                {
                    blocksRangeAdder.add(new BlockPos(x, y, z));
                }
            }
        }
        blocksRange = new ArrayList<>(blocksRangeAdder);
    }

    @Subscribe
    public void onRenderWorld(EventWorldRender event) {
        if (targetBlock != null) {
            float r =  (float) (this.getSetting(10).asToggle().getChild(0).asSlider().getValue() / 255.0D);
            float g =  (float) (this.getSetting(10).asToggle().getChild(1).asSlider().getValue() / 255.0D);
            float b =  (float) (this.getSetting(10).asToggle().getChild(2).asSlider().getValue() / 255.0D);
            RenderUtils.drawOutlineBox(targetBlock, r, g, b, 1.0f);
        }
    }

    public void
    onDisable()
    {
        super.onDisable();
        if(oldSlot != -1)
            mc.player.inventory.selectedSlot = oldSlot;
        oldSlot = -1;
    }

    @Subscribe
    public void
    onTick(EventTick event)
    {
        counter++;
        if(counter < getSetting(7).asSlider().getValue())
            return;
        counter = 0;

        EndCrystalEntity crystal = Streams.stream(mc.world.getEntities()).filter(entityx -> (entityx instanceof EndCrystalEntity)).map(entityx -> {
            BlockPos p = entityx.getBlockPos().down();

            return (EndCrystalEntity) entityx;
        }).min(Comparator.comparing(c -> mc.player.distanceTo(c))).orElse(null);

        target = Streams.stream(mc.world.getEntities())
                .filter(e ->
                        (e instanceof PlayerEntity && getSetting(0).asToggle().state)
                                || (e instanceof Monster && getSetting(1).asToggle().state)
                                || (EntityUtils.isAnimal(e) && getSetting(2).asToggle().state))
                .filter(e -> !(MacHack.friendMang.has(e.getName().asString())) && e != mc.player)
                .filter(e -> mc.player.distanceTo(e) < 13)
                .filter(e -> !((LivingEntity) e).isDead())
                .sorted((a, b) -> Float.compare(a.distanceTo(mc.player), b.distanceTo(mc.player)))
                .findFirst()
                .orElse(null);

        if(target == null)
            return;

        blocks.clear();
        blocksRange.parallelStream()
                .filter(pos -> canPlace(pos.add(mc.player.getX(), mc.player.getY(), mc.player.getZ())))
                .forEach(pos -> blocks.add(pos.add(mc.player.getX(), mc.player.getY(), mc.player.getZ())));

        //calculate the block that does the most damage to the target
        targetBlock = blocks.parallelStream()
                .filter(block -> CrystalUtils.getCrystalDamage(mc.player, block) < getSetting(4).asSlider().getValue())
                //.filter(block -> CrystalUtils.getCrystalDamage(mc.player, block) < mc.player.getHealth())
                .filter(block -> {
                    LivingEntity livingEntity = (LivingEntity) target;
                    if(livingEntity.getHealth() + livingEntity.getAbsorptionAmount() < getSetting(6).asSlider().getValue())
                        return true;
                    return CrystalUtils.getCrystalDamage(livingEntity, block) > getSetting(5).asSlider().getValue();
                })
                .collect(Collectors.toList())
                .stream()
                .sorted((block1, block2) -> Double.compare(CrystalUtils.getCrystalDamage((LivingEntity) target, block2), CrystalUtils.getCrystalDamage((LivingEntity) target, block1)))
                .findFirst()
                .orElse(null);

        if(targetBlock == null)
            return;

        if(getSetting(3).asToggle().state && mc.player.getMainHandStack().getItem() != Items.END_CRYSTAL)
            oldSlot = CrystalUtils.changeHotbarSlotToItem(Items.END_CRYSTAL);

        if (mc.player.getMainHandStack().getItem() != Items.END_CRYSTAL) {return;}

        CrystalUtils.placeBlock(new Vec3d(targetBlock.getX(), targetBlock.getY(), targetBlock.getZ()), Hand.MAIN_HAND, Direction.UP);
        mc.interactionManager.attackEntity(mc.player, crystal);
        mc.player.swingHand(Hand.MAIN_HAND);
        target = null;
        targetBlock = null;
    }

    private boolean
    canPlace(BlockPos blockPos)
    {
        BlockPos up1 = blockPos.add(0, 1, 0);
        BlockPos up2 = blockPos.add(0, 2, 0);
        if (getSetting(8).asToggle().state) {
            return (mc.world.getBlockState(blockPos).getBlock() == Blocks.OBSIDIAN
                    || mc.world.getBlockState(blockPos).getBlock() == Blocks.BEDROCK)
                    && mc.world.getBlockState(up1).getBlock() == Blocks.AIR
                    && mc.world.getOtherEntities(null, new Box(up1).stretch(0, 1, 0)).isEmpty();

        } else {
            return (mc.world.getBlockState(blockPos).getBlock() == Blocks.OBSIDIAN
                    || mc.world.getBlockState(blockPos).getBlock() == Blocks.BEDROCK)
                    && mc.world.getBlockState(up1).getBlock() == Blocks.AIR
                    && mc.world.getBlockState(up2).getBlock() == Blocks.AIR
                    && mc.world.getOtherEntities(null, new Box(up1).stretch(0, 1, 0)).isEmpty()
                    && mc.world.getOtherEntities(null, new Box(up2).stretch(0, 1, 0)).isEmpty();
        }
    }

}