package mac.hack.module.mods;

import com.google.common.eventbus.Subscribe;
import mac.hack.MacHack;
import mac.hack.event.events.EventTick;
import mac.hack.module.Category;
import mac.hack.module.Module;
import mac.hack.setting.base.SettingMode;
import mac.hack.utils.MacLogger;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.List;

import static net.minecraft.util.math.Direction.DOWN;
import static net.minecraft.util.math.Direction.UP;

public class PacketAutoCity extends Module {
    private boolean firstRun;
    private BlockPos mineTarget = null;
    private PlayerEntity closestTarget;

    public PacketAutoCity() {
        super("PacketAutoCity", KEY_UNBOUND, Category.COMBAT, "Packet mine a target");
    }

    @Subscribe
    public void onTick(EventTick event){
        if(mc.player == null) {
            return;
        }
        findClosestTarget();

        if (closestTarget == null) {
            if (firstRun) {
                firstRun = false;
                MacLogger.infoMessage("AutoCity Enabled, no one to city!");
            }
            this.toggle();
            return;
        }

        if (firstRun && mineTarget != null) {
            firstRun = false;
            MacLogger.infoMessage(" Attempting to mine: " + closestTarget.getName());
        }

        findCityBlock();
        if(mineTarget != null) {
            int newSlot = -1;
            for (int i = 0; i < 9; i++) {
                ItemStack stack = mc.player.inventory.getStack(i);
                if (stack == ItemStack.EMPTY) {
                    continue;
                }
                if ((stack.getItem() instanceof PickaxeItem)) {
                    newSlot = i;
                    break;
                }
            }
            if (newSlot != -1) {
                mc.player.inventory.selectedSlot = newSlot;
            }
            mc.player.swingHand(Hand.MAIN_HAND);
            mc.player.networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, mineTarget, DOWN));
            mc.player.networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, mineTarget, DOWN));
            this.toggle();
        } else {
            MacLogger.infoMessage("No city blocks to mine!");
            this.toggle();
        }
    }

    public BlockPos findCityBlock() {
        Vec3d vec = closestTarget.getPos();
        if(mc.player.getPos().distanceTo(vec) <= 8) {
            BlockPos targetX = new BlockPos(vec.add(1, 0, 0));
            BlockPos targetXMinus = new BlockPos(vec.add(-1, 0, 0));
            BlockPos targetZ = new BlockPos(vec.add(0, 0, 1));
            BlockPos targetZMinus = new BlockPos(vec.add(0, 0, -1));
            if(canBreak(targetX)) {
                mineTarget = targetX;
            }
            if(!canBreak(targetX) && canBreak(targetXMinus)) {
                mineTarget = targetXMinus;
            }
            if(!canBreak(targetX) && !canBreak(targetXMinus) && canBreak(targetZ)) {
                mineTarget = targetZ;
            }
            if(!canBreak(targetX) && !canBreak(targetXMinus) && !canBreak(targetZ) && canBreak(targetZMinus)) {
                mineTarget = targetZMinus;
            }
            if((!canBreak(targetX) && !canBreak(targetXMinus) && !canBreak(targetZ) && !canBreak(targetZMinus)) || mc.player.getPos().distanceTo(vec) > 8) {
                mineTarget = null;
            }
        }
        return mineTarget;
    }

    private boolean canBreak(BlockPos pos) {
        BlockState state = mc.world.getBlockState(pos);
        return  !(state.getBlock() == Blocks.AIR || state.getBlock() == Blocks.WATER || state.getBlock() == Blocks.LAVA || state.getBlock() == Blocks.BEDROCK || state.getBlock() == Blocks.VOID_AIR || state.getBlock() == Blocks.CAVE_AIR);
    }
    
    private void findClosestTarget() {
        List<AbstractClientPlayerEntity> playerList = mc.world.getPlayers();
        closestTarget = null;
        for (PlayerEntity target : playerList) {
            if (target == mc.player) {
                continue;
            }
            if (MacHack.friendMang.has(target.getName().asString())){
                continue;
            }
            if (!isLiving(target)) {
                continue;
            }
            if ((target).getHealth() <= 0) {
                continue;
            }
            if (closestTarget == null) {
                closestTarget = target;
                continue;
            }
            if (mc.player.distanceTo(target) < mc.player.distanceTo(closestTarget)) {
                closestTarget = target;
            }
        }
    }

    public static boolean isLiving(Entity e) {
        return e instanceof LivingEntity;
    }
}
