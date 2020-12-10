package mac.hack.module.mods;

import com.google.common.eventbus.Subscribe;

import mac.hack.event.events.EventOpenScreen;
import mac.hack.setting.base.SettingSlider;
import mac.hack.setting.base.SettingToggle;
import mac.hack.module.Category;
import mac.hack.module.Module;
import mac.hack.utils.MacQueue;
import net.minecraft.client.gui.screen.DeathScreen;

public class AutoRespawn extends Module {

	public AutoRespawn() {
		super("AutoRespawn", KEY_UNBOUND, Category.PLAYER, "Automatically respawn when you die",
				new SettingToggle("Delay", false),
				new SettingSlider("Delay: ", 1, 15, 5, 0));
	}

	@Subscribe
	public void onOpenScreen(EventOpenScreen event) {
		if (event.getScreen() instanceof DeathScreen) {
			if (getSettings().get(0).asToggle().state) {
				for (int i = 0; i <= (int) getSettings().get(1).asSlider().getValue(); i++) MacQueue.add("autorespawn", () -> {});
				MacQueue.add("autorespawn", () -> mc.player.requestRespawn());
			} else {
				mc.player.requestRespawn();
			}
		}
	}
}
