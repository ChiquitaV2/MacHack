/*
package mac.hack.module.mods;

import com.google.common.eventbus.Subscribe;
import mac.hack.event.events.EventTick;
import mac.hack.module.Category;
import mac.hack.module.Module;
import mac.hack.module.ModuleManager;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class PacketEat extends Module {

    public PacketEat() {
        super("PacketEat", KEY_UNBOUND, Category.PLAYER, "automatically air places anvil to surround");
    }

    @Subscribe
    public void onTick(EventTick event) {
        assert mc.player != null;
        assert mc.interactionManager != null;
        int blockSlot = -1;

        int slot = mc.player.inventory.selectedSlot;
        if(mc.player.input.pressingRight){
            mc.getNetworkHandler().sendPacket(mc.player.t);
            mc.player.swingHand(Hand.MAIN_HAND);
        }
    }
}

 */
