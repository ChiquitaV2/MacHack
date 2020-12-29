package mac.hack.module.mods;

import com.google.common.eventbus.Subscribe;
import mac.hack.MacHack;
import mac.hack.event.events.EventTick;
import mac.hack.module.Category;
import mac.hack.module.Module;
import mac.hack.module.ModuleManager;
import mac.hack.setting.base.SettingMode;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class SelfTrap extends Module {

    public SelfTrap() {
        super("SelfTrap", KEY_UNBOUND, Category.COMBAT, "autotraps yourself!"
        );
    }

    @Subscribe
    public void onTick(EventTick event) {
        assert mc.player != null;
        assert mc.interactionManager != null;
        int blockSlot = -1;
        PlayerEntity target = null;
        for(int i = 0; i < 9; i++){
            if (mc.player.inventory.getStack(i).getItem() == Blocks.OBSIDIAN.asItem() || mc.player.inventory.getStack(i).getItem() == Blocks.NETHERITE_BLOCK.asItem()){
                blockSlot = i;
                break;
            }
        }
        if (blockSlot == -1) return;
        target = mc.player;
        if (target == null) return;
        if (mc.player.distanceTo(target) < 5){
            int prevSlot = mc.player.inventory.selectedSlot;
            mc.player.inventory.selectedSlot = blockSlot;
            BlockPos targetPos = target.getBlockPos().up();
                    if(mc.world.getBlockState(targetPos.add(0, 1, 0)).getMaterial().isReplaceable()){
                        mc.interactionManager.interactBlock(mc.player, mc.world, Hand.MAIN_HAND, new BlockHitResult(target.getPos().add(0, 1, 0), Direction.UP, targetPos.add(0, 1, 0), false));
                        mc.player.swingHand(Hand.MAIN_HAND);
                    }
                    if(mc.world.getBlockState(targetPos.add(1, 0, 0)).getMaterial().isReplaceable()){
                        mc.interactionManager.interactBlock(mc.player, mc.world, Hand.MAIN_HAND, new BlockHitResult(target.getPos().add(1, 0, 0), Direction.UP, targetPos.add(1, 0, 0), false));
                        mc.player.swingHand(Hand.MAIN_HAND);
                    }
                    if(mc.world.getBlockState(targetPos.add(-1, 0, 0)).getMaterial().isReplaceable()){
                        mc.interactionManager.interactBlock(mc.player, mc.world, Hand.MAIN_HAND, new BlockHitResult(target.getPos().add(-1, 0, 0), Direction.UP, targetPos.add(-1, 0, 0), false));
                        mc.player.swingHand(Hand.MAIN_HAND);
                    }
                    if(mc.world.getBlockState(targetPos.add(0, 0, 1)).getMaterial().isReplaceable()){
                        mc.interactionManager.interactBlock(mc.player, mc.world, Hand.MAIN_HAND, new BlockHitResult(target.getPos().add(0, 0, 1), Direction.UP, targetPos.add(0, 0, 1), false));
                        mc.player.swingHand(Hand.MAIN_HAND);
                    }
                    if(mc.world.getBlockState(targetPos.add(0, 0, -1)).getMaterial().isReplaceable()){
                        mc.interactionManager.interactBlock(mc.player, mc.world, Hand.MAIN_HAND, new BlockHitResult(target.getPos().add(0, 0, -1), Direction.UP, targetPos.add(0, 0, -1), false));
                        mc.player.swingHand(Hand.MAIN_HAND);
                    }
                    if(mc.world.getBlockState(targetPos.add(1, -1, 0)).getMaterial().isReplaceable()){
                        mc.interactionManager.interactBlock(mc.player, mc.world, Hand.MAIN_HAND, new BlockHitResult(target.getPos().add(1, 0, 0), Direction.UP, targetPos.add(1, -1, 0), false));
                        mc.player.swingHand(Hand.MAIN_HAND);
                    }
                    if(mc.world.getBlockState(targetPos.add(-1, -1, 0)).getMaterial().isReplaceable()){
                        mc.interactionManager.interactBlock(mc.player, mc.world, Hand.MAIN_HAND, new BlockHitResult(target.getPos().add(-1, 0, 0), Direction.UP, targetPos.add(-1, -1, 0), false));
                        mc.player.swingHand(Hand.MAIN_HAND);
                    }
                    if(mc.world.getBlockState(targetPos.add(0, -1, 1)).getMaterial().isReplaceable()){
                        mc.interactionManager.interactBlock(mc.player, mc.world, Hand.MAIN_HAND, new BlockHitResult(target.getPos().add(0, 0, 1), Direction.UP, targetPos.add(0, -1, 1), false));
                        mc.player.swingHand(Hand.MAIN_HAND);
                    }
                    if(mc.world.getBlockState(targetPos.add(0, -1, -1)).getMaterial().isReplaceable()){
                        mc.interactionManager.interactBlock(mc.player, mc.world, Hand.MAIN_HAND, new BlockHitResult(target.getPos().add(0, 0, -1), Direction.UP, targetPos.add(0, -1, -1), false));
                        mc.player.swingHand(Hand.MAIN_HAND);
                    }

            mc.player.inventory.selectedSlot = prevSlot;
            ModuleManager.getModule(SelfTrap.class).toggle();
        }
    }
}