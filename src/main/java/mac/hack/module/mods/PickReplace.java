package mac.hack.module.mods;

import mac.hack.event.events.EventTick;
import mac.hack.module.Category;
import mac.hack.module.Module;
import mac.hack.setting.base.SettingMode;
import mac.hack.setting.base.SettingSlider;
import mac.hack.utils.Timer;
import com.google.common.eventbus.Subscribe;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;

import java.util.ArrayList;

public class PickReplace extends Module {

    public PickReplace() {
        super("HotbarCache", KEY_UNBOUND, Category.MISC, "Autototem for picks");
    }

    @Subscribe
    public void onTick(EventTick event) {
//        Hotbar2 = Hotbar;
//        if (mc.currentScreen != null)
//            return;
//
//        if (!timer.passed(getSettings().get(1).asSlider().getValue() * 1000))
//            return;
//
//        if (getSetting(0).asMode().mode == 0) {
//            for (int l_I = 0; l_I < 9; ++l_I) {
//                if (SwitchSlotIfNeed(l_I)) {
//                    timer.reset();
//                    return;
//                }
//            }
//        }
//        if (getSetting(0).asMode().mode == 1) {
//            for (int l_I = 0; l_I < 9; ++l_I) {
//                if (RefillSlotIfNeed(l_I)) {
//                    System.out.println("THIS IS PRINTED");
//                    timer.reset();
//                    return;
//                }
//            }
//        }
//    }
//    private boolean SwitchSlotIfNeed(int targetSlot)
//    {
//        MinecraftClient mc = MinecraftClient.getInstance();
//        PlayerEntity player = mc.player;
//        Item itemFromCache = Hotbar.get(targetSlot);
//
//        if (itemFromCache == Items.AIR)
//            return false;
//
//        if (!player.inventory.getStack(targetSlot).isEmpty() && player.inventory.getStack(targetSlot).getItem() == itemFromCache)
//            return false;
//
//        int slotFromCache = GetItemSlot(itemFromCache);
//
//        if (slotFromCache != -1 && slotFromCache != 45)
//        {
//            mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, slotFromCache, targetSlot, SlotActionType.SWAP, mc.player);
//            System.out.println("This should be working.");
//            System.out.println(slotFromCache);
//            System.out.println(targetSlot);
//            return true;
//        }
//
//        return false;
//    }
//
//    public int GetItemSlot(Item input)
//    {
//        MinecraftClient mc = MinecraftClient.getInstance();
//        PlayerEntity player = mc.player;
//        if (player == null)
//            return 0;
//
//        for (int i = 0; i < player.inventory.size(); ++i)
//        {
//            if (i == 0 || i == 5 || i == 6 || i == 7 || i == 8)
//                continue;
//
//            ItemStack s = player.inventory.getStack(i);
//
//            if (s.isEmpty())
//                continue;
//
//            if (s.getItem() == input)
//            {
//                return i;
//            }
//            System.out.println(i);
//        }
//        return -1;
//    }
//
//    private boolean RefillSlotIfNeed(int targetSlot)
//    {
//        ItemStack itemInTargetSlot = mc.player.inventory.getStack(targetSlot);
//
//        if (itemInTargetSlot.isEmpty() || itemInTargetSlot.getItem() == Items.AIR)
//            return false;
//
//        if (!itemInTargetSlot.isStackable())
//            return false;
//
//        if (itemInTargetSlot.getCount() >= itemInTargetSlot.getMaxCount())
//            return false;
//
//        /// We're going to search the entire inventory for the same stack, WITH THE SAME NAME, and use quick move.
//        for (int l_I = 9; l_I < 36; ++l_I)
//        {
//            final ItemStack l_Item = mc.player.inventory.getStack(l_I);
//
//            if (l_Item.isEmpty())
//                continue;
//
//            if (CanItemBeMergedWith(itemInTargetSlot, l_Item))
//            {
//                mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, l_I, 0,
//                        SlotActionType.QUICK_MOVE, mc.player);
//                /// Check again for more next available tick
//                return true;
//            }
//        }
//
//        return false;
//    }
//
//    private boolean CanItemBeMergedWith(ItemStack p_Source, ItemStack p_Target)
//    {
//        return p_Source.getItem() == p_Target.getItem() && p_Source.getName().equals(p_Target.getName());
//    }
        this.findPicks();
    }

    private void findPicks() {
        if (mc.currentScreen == null || !(mc.currentScreen instanceof InventoryScreen)) {
            for (int c = 0; c < 6; c++) {
                if (mc.player.inventory.getStack(c).getItem() != Items.NETHERITE_PICKAXE && mc.player.inventory.getStack(c).getItem() != Items.DIAMOND_PICKAXE) {
                    for (int i = 9; i < 36; ++i) {
                        if (mc.player.inventory.getStack(i).getItem() == Items.NETHERITE_PICKAXE && mc.player.inventory.getStack(i).getItem() == Items.DIAMOND_PICKAXE) {
                            mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, i, 0, SlotActionType.SWAP, mc.player);
                            break;
                        }
                    }
                }
            }
        }
    }

}