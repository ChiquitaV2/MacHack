package mac.hack.module.mods;

import mac.hack.event.events.EventTick;
import mac.hack.module.Category;
import mac.hack.module.Module;
import com.google.common.eventbus.Subscribe;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.item.*;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.registry.Registry;
import net.minecraft.item.Items.*;

import java.util.Arrays;
import java.util.List;

public class ShulkerDrop extends Module {

    public ShulkerDrop() {
        super("Shulker Drop", KEY_UNBOUND, Category.MISC, "Drop all shulkers");
    }

    private static final List<Block> Shulkers_Blocks = Arrays.asList(
            Blocks.SHULKER_BOX,
            Blocks.BLACK_SHULKER_BOX, Blocks.BLUE_SHULKER_BOX, Blocks.BROWN_SHULKER_BOX, Blocks.CYAN_SHULKER_BOX,
            Blocks.GRAY_SHULKER_BOX, Blocks.GREEN_SHULKER_BOX, Blocks.LIGHT_BLUE_SHULKER_BOX, Blocks.LIGHT_GRAY_SHULKER_BOX,
            Blocks.LIME_SHULKER_BOX, Blocks.MAGENTA_SHULKER_BOX, Blocks.ORANGE_SHULKER_BOX, Blocks.PINK_SHULKER_BOX,
            Blocks.PURPLE_SHULKER_BOX, Blocks.RED_SHULKER_BOX, Blocks.YELLOW_SHULKER_BOX, Blocks.WHITE_SHULKER_BOX
    );
    public static final List<Item> Shulkers = Arrays.asList(
            Items.SHULKER_BOX, Items.BLACK_SHULKER_BOX,
            Items.BLUE_SHULKER_BOX, Items.BROWN_SHULKER_BOX, Items.CYAN_SHULKER_BOX,
            Items.GRAY_SHULKER_BOX, Items.GREEN_SHULKER_BOX, Items.LIGHT_BLUE_SHULKER_BOX, Items.LIGHT_GRAY_SHULKER_BOX,
            Items.LIME_SHULKER_BOX, Items.MAGENTA_SHULKER_BOX, Items.ORANGE_SHULKER_BOX, Items.PINK_SHULKER_BOX,
            Items.PURPLE_SHULKER_BOX, Items.RED_SHULKER_BOX, Items.YELLOW_SHULKER_BOX, Items.WHITE_SHULKER_BOX
    );
    @Subscribe
    public void onTick(EventTick event) {
        this.dropS();
    }
    private void dropS() {
        if (mc.currentScreen == null || !(mc.currentScreen instanceof InventoryScreen)) {
            for(int i = 0; i < 45; ++i) {
                if (mc.player.currentScreenHandler.slots.get(i).getStack().getItem() instanceof BlockItem
                        && ((BlockItem) mc.player.currentScreenHandler.slots.get(i).getStack().getItem()).getBlock() instanceof ShulkerBoxBlock) {
                    mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, i, 0, SlotActionType.THROW, mc.player);
                    break;
                }
            }
        }
    }
}
