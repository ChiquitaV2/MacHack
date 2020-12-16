package mac.hack.module.mods;

import mac.hack.event.events.EventTick;
import mac.hack.module.Category;
import mac.hack.module.Module;
import mac.hack.module.ModuleManager;
import mac.hack.setting.base.SettingMode;
import com.google.common.eventbus.Subscribe;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;

public class AutoSwitcher extends Module {

    public AutoSwitcher() {
        super("AutoSwitcher", KEY_UNBOUND, Category.MISC, "Autototem for items",
                new SettingMode("Item", "Pickaxe", "Crystal", "Gapple"));
    }

    @Subscribe
    public void onTick(EventTick event) {
        AutoEat autoEat = (AutoEat) ModuleManager.getModule(AutoEat.class);
        if (autoEat.isEating()) return;
        MinecraftClient mc = MinecraftClient.getInstance();
        PlayerEntity player = mc.player;
        int mode = getSettings().get(0).asMode().mode;
        switch (mode) {
            case 0:
                /* Inventory */
                if (player.inventory.getMainHandStack().isEmpty() || player.inventory.getMainHandStack().getItem() != Items.DIAMOND_PICKAXE || player.inventory.getMainHandStack().getItem() != Items.NETHERITE_PICKAXE) {
                    for (int i = 0; i < 9; i++) {
                        if (player.inventory.getStack(i).getItem() == Items.DIAMOND_PICKAXE || player.inventory.getStack(i).getItem() == Items.NETHERITE_PICKAXE) {
                            player.inventory.selectedSlot = i;
                            //                            player.inventory.swapSlotWithHotbar(i);
                            return;
                        }
                    }
                }
                break;
            case 1:
                /* Inventory */
                if (player.inventory.getStack(0).isEmpty() || player.inventory.getStack(0).getItem() != Items.END_CRYSTAL) {
                    for (int i = 0; i < 9; i++) {
                        if (player.inventory.getStack(i).getItem() == Items.END_CRYSTAL) {
                            player.inventory.selectedSlot = i;
                            return;
                        }
                    }
                }
                break;
            case 2:
                /* Inventory */
                if (player.inventory.getStack(0).isEmpty() || player.inventory.getStack(0).getItem() != Items.ENCHANTED_GOLDEN_APPLE) {
                    for (int i = 0; i < 9; i++) {
                        if (player.inventory.getStack(i).getItem() == Items.ENCHANTED_GOLDEN_APPLE) {
                            player.inventory.selectedSlot = i;
                            return;
                        }
                    }
                }
                break;
            case 3:
                /* Inventory */
                if (mc.player.inventory.getStack(0).isEmpty() || mc.player.inventory.getStack(0).getItem() != Items.SNOWBALL) {
                    for (int i = 0; i < 9; i++) {
                        if (mc.player.inventory.getStack(i).getItem() == Items.SNOWBALL) {
                            mc.player.inventory.selectedSlot = i;
                            return;
                        }
                    }
                }
                break;
        }
    }
}
