package mac.hack.module.mods;

import com.google.common.eventbus.Subscribe;
import mac.hack.MacHack;
import mac.hack.event.events.EventTick;
import mac.hack.module.Category;
import mac.hack.module.Module;
import mac.hack.utils.MacLogger;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import static net.minecraft.util.math.Direction.DOWN;

public class PacketAutoCity extends Module {
    private BlockPos mineTarget = null;

    public PacketAutoCity() {
        super("PacketAutoCity", KEY_UNBOUND, Category.COMBAT, "Packet mine a target");
    }

    // Join MacHack discord discord.gg/h8EQyuYTK7
    @Subscribe
    public void onTick(EventTick event){
        assert mc.player != null;
        assert mc.interactionManager != null;
        PlayerEntity target = null;
        for(PlayerEntity player : mc.world.getPlayers()){
            if (player != mc.player && !MacHack.friendMang.has(player.getDisplayName().getString()))
                if (target == null){
                    target = player;
                }else if (mc.player.distanceTo(target) > mc.player.distanceTo(player)){
                    target = player;
                }
        }
        if (target == null) {
            MacLogger.infoMessage("AutoCity Enabled, no one to city!");
            this.toggle();
            return;
        }
        if (mc.player.distanceTo(target) < 6) {
            if (mineTarget != null) {
                MacLogger.infoMessage(" Attempting to mine: " + target.getName());
            }

            findCityBlock(target);
            if (mineTarget != null) {
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
                MacLogger.infoMessage("Mined");
                this.toggle();
            } else {
                MacLogger.infoMessage("No city blocks to mine!");
                this.toggle();
            }
        }
    }

    public BlockPos findCityBlock(PlayerEntity playerEntity) {
        Vec3d vec = playerEntity.getPos();
        if(mc.player.getPos().distanceTo(vec) <= 6) {
            BlockPos targetX = new BlockPos(vec.add(1, 0, 0));
            BlockPos targetXMinus = new BlockPos(vec.add(-1, 0, 0));
            BlockPos targetZ = new BlockPos(vec.add(0, 0, 1));
            BlockPos targetZMinus = new BlockPos(vec.add(0, 0, -1));
            if(canBreak(targetX) && below(targetX) ) {
                mineTarget = targetX;
            }
            if(!canBreak(targetX) && canBreak(targetXMinus) && below(targetXMinus)) {
                mineTarget = targetXMinus;
            }
            if(!canBreak(targetX) && !canBreak(targetXMinus) && canBreak(targetZ) && below(targetZ)) {
                mineTarget = targetZ;
            }
            if(!canBreak(targetX) && !canBreak(targetXMinus) && !canBreak(targetZ) && canBreak(targetZMinus) && below(targetZMinus)) {
                mineTarget = targetZMinus;
            }
            if((!canBreak(targetX) && !canBreak(targetXMinus) && !canBreak(targetZ) && !canBreak(targetZMinus)) || mc.player.getPos().distanceTo(vec) > 6) {
                mineTarget = null;
            }
        }
        return mineTarget;
    }

    private boolean canBreak(BlockPos pos) {
        BlockState state = mc.world.getBlockState(pos);
        return  !(state.getBlock() == Blocks.AIR || state.getBlock() == Blocks.WATER || state.getBlock() == Blocks.LAVA || state.getBlock() == Blocks.BEDROCK || state.getBlock() == Blocks.VOID_AIR || state.getBlock() == Blocks.CAVE_AIR);
    }
    private boolean below(BlockPos pos) {
        BlockState state = mc.world.getBlockState(pos.down(1));
        return  (state.getBlock() == Blocks.BEDROCK || state.getBlock() == Blocks.OBSIDIAN);
    }

}
