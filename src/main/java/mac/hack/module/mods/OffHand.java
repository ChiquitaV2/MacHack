package mac.hack.module.mods;

import mac.hack.event.events.EventTick;
import mac.hack.module.Category;
import mac.hack.module.Module;
import mac.hack.module.ModuleManager;
import mac.hack.setting.base.SettingMode;
import mac.hack.setting.base.SettingSlider;
import mac.hack.setting.base.SettingToggle;
import com.google.common.eventbus.Subscribe;
import mac.hack.utils.MacLogger;
import mac.hack.utils.MacNotify;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;

import java.io.IOException;

public class OffHand extends Module {

    public OffHand() {
        super("OffHand", KEY_UNBOUND, Category.COMBAT, "AutoTotem for other stuff",
                new SettingToggle("Override", true).withDesc("Equips even if theres another item in the offhand"),
                new SettingMode("Mode", "Gap", "Crystal"),
                new SettingSlider("ToggleHealth", 0, 36, 13, 1).withDesc("What Health to toggle autototem on"),
                new SettingToggle("Chat", true).withDesc("send you a chat notification when an item is placed in your off hand")
                );
    }

    @Subscribe
    public void onTick(EventTick event) throws IOException {
        if (ModuleManager.getModule(AutoTotem.class).isToggled()) {
            ModuleManager.getModule(AutoTotem.class).setToggled(false);
            return;
        }
        if ((mc.player.getHealth() + mc.player.getAbsorptionAmount()) < getSetting(2).asSlider().getValue()) {
            setToggled(false);
            MacLogger.infoMessage("Health low autototum on");
            ModuleManager.getModule(AutoTotem.class).setToggled(true);
            return;
        }
        if (getSettings().get(1).asMode().mode == 0) {
            if (mc.player.getOffHandStack().getItem() == Items.ENCHANTED_GOLDEN_APPLE
                    || (!mc.player.getOffHandStack().isEmpty() && !getSetting(0).asToggle().state))
                return;

            // Cancel at all non-survival-inventory containers
            if (mc.currentScreen instanceof InventoryScreen || mc.currentScreen == null) {
                for (int i = 9; i < 45; i++) {
                    if (mc.player.inventory.getStack(i >= 36 ? i - 36 : i).getItem() == Items.ENCHANTED_GOLDEN_APPLE) {
                        boolean itemInOffhand = !mc.player.getOffHandStack().isEmpty();
                        mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, i, 0, SlotActionType.PICKUP, mc.player);
                        mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, 45, 0, SlotActionType.PICKUP, mc.player);

                        if (itemInOffhand)
                            mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, i, 0, SlotActionType.PICKUP, mc.player);
                        if (getSetting(3).asToggle().state) {
                            MacLogger.infoMessage("Your offhand now has golden apples");
                        }
                        if (ModuleManager.getModule(Notifications.class).isToggled() && ModuleManager.getModule(Notifications.class).getSetting(2).asToggle().state)
                            MacNotify.Notifications("OffHand", "Your offhand now has golden apples");

                        return;
                    }
                }
            }
        }
        else if (getSettings().get(1).asMode().mode == 1) {
            if (mc.player.getOffHandStack().getItem() == Items.END_CRYSTAL
                    || (!mc.player.getOffHandStack().isEmpty() && !getSetting(0).asToggle().state))
                return;

            // Cancel at all non-survival-inventory containers
            if (mc.currentScreen instanceof InventoryScreen || mc.currentScreen == null) {
                for (int i = 9; i < 45; i++) {
                    if (mc.player.inventory.getStack(i >= 36 ? i - 36 : i).getItem() == Items.END_CRYSTAL) {
                        boolean itemInOffhand = !mc.player.getOffHandStack().isEmpty();
                        mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, i, 0, SlotActionType.PICKUP, mc.player);
                        mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, 45, 0, SlotActionType.PICKUP, mc.player);

                        if (itemInOffhand)
                            mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, i, 0, SlotActionType.PICKUP, mc.player);
                        if (getSetting(3).asToggle().state) {
                            MacLogger.infoMessage("Your offhand now has endcrystals");
                        }
                        if (ModuleManager.getModule(Notifications.class).isToggled() && ModuleManager.getModule(Notifications.class).getSetting(2).asToggle().state){
                            MacNotify.Notifications("Offhand", "Your offhand now has endcrystals");
                        }

                        return;
                    }
                }
            }
        }
    }

}