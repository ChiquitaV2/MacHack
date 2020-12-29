package mac.hack.module.mods;

import com.google.common.eventbus.Subscribe;
import mac.hack.event.events.EventTick;
import mac.hack.module.Category;
import mac.hack.module.Module;
import mac.hack.setting.base.SettingMode;

import mac.hack.utils.WorldUtils;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;

public class Burrow extends Module {

    public Burrow() {
        super("Burrow", KEY_UNBOUND, Category.COMBAT, "Glitches you into a block. maybe sometimes?",
                new SettingMode("Mode: ", "Motion","SetPos"));
    }

    private BlockPos originalPos;
    private int oldSlot = -1;


    public void onEnable(){
        super.onEnable();
        if(mc.world !=null) {
            this.originalPos = new BlockPos(mc.player.getPos().x, mc.player.getPos().y, mc.player.getPos().z);

            if (mc.world.getBlockState(new BlockPos(mc.player.getPos().x, mc.player.getPos().y, mc.player.getPos().z)).getBlock().equals(Blocks.OBSIDIAN) || WorldUtils.isInterceptedByOther(this.originalPos) || getHotbarSlot(Items.OBSIDIAN) == -1)
            {
                this.setToggled(false);
                return;
            }
            if(mc.player.isOnGround())
                mc.player.jump();
            WorldUtils.placeBlock(mc.player.getBlockPos(), getHotbarSlot(Items.OBSIDIAN), true, true);
        }
    }

    @Subscribe
    public void onTick(EventTick event) {
        if(mc.player.getPos().y > this.originalPos.getY() + 1.2) {
            this.oldSlot = mc.player.inventory.selectedSlot;

            mc.player.swingHand(Hand.MAIN_HAND);
            WorldUtils.placeBlock(this.originalPos, getHotbarSlot(Items.OBSIDIAN), true, true);

            mc.player.inventory.selectedSlot = oldSlot;

            if(getSettings().get(0).asMode().mode == 0)
                mc.player.setVelocity(mc.player.getVelocity().x, 0.1, mc.player.getVelocity().z);
            else if(getSettings().get(0).asMode().mode == 1)
                mc.player.setPos(mc.player.getX(), mc.player.getY() - 1.2, mc.player.getZ());

            this.setToggled(false);
        }
    }

    public static int getHotbarSlot(final Item item)
    {
        for (int i = 0; i < 9; i++)
        {
            final Item item1 = MinecraftClient.getInstance().player.inventory.getStack(i).getItem();

            if (item.equals(item1)) return i;
        }
        return -1;
    }

}
