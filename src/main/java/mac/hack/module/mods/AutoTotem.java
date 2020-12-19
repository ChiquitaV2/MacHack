package mac.hack.module.mods;

import mac.hack.event.events.EventTick;
import mac.hack.module.Category;
import mac.hack.module.Module;
import mac.hack.module.ModuleManager;
import mac.hack.setting.base.SettingToggle;
import com.google.common.eventbus.Subscribe;
import mac.hack.utils.MacLogger;
import mac.hack.utils.MacNotify;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;

import java.io.IOException;

public class AutoTotem extends Module {

	public AutoTotem() {
		super("AutoTotem", KEY_UNBOUND, Category.COMBAT, "Automatically equips totems.",
				new SettingToggle("Override", true).withDesc("Equips a totem even if theres another item in the offhand"),
				new SettingToggle("Chat", true).withDesc("Sends you a chat when a totem is placed"));
	}

	@Subscribe
	public void onTick(EventTick event) throws IOException {
		if (mc.player.getOffHandStack().getItem() == Items.TOTEM_OF_UNDYING
				|| (!mc.player.getOffHandStack().isEmpty() && !getSetting(0).asToggle().state))
			return;

		// Cancel at all non-survival-inventory containers
		if (mc.currentScreen instanceof InventoryScreen || mc.currentScreen == null) {
			for (int i = 9; i < 45; i++) {
				if (mc.player.inventory.getStack(i >= 36 ? i - 36 : i).getItem() == Items.TOTEM_OF_UNDYING) {
					boolean itemInOffhand = !mc.player.getOffHandStack().isEmpty();
					mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, i, 0, SlotActionType.PICKUP, mc.player);
					mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, 45, 0, SlotActionType.PICKUP, mc.player);

					if (itemInOffhand)
						mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, i, 0, SlotActionType.PICKUP, mc.player);
					if (getSetting(1).asToggle().state) {
						MacLogger.infoMessage("Your offhand now has totem");
					}
					if (ModuleManager.getModule(Notifications.class).isToggled() && ModuleManager.getModule(Notifications.class).getSetting(1).asToggle().state)
						MacNotify.Notifications("AutoTotem", "Your offhand now has totem");
					return;
				}
			}
		}
	}

}