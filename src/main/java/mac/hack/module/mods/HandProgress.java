package mac.hack.module.mods;

import mac.hack.event.events.EventTick;
import mac.hack.mixin.FirstPersonRendererAccessor;
import mac.hack.module.Category;
import mac.hack.module.Module;
import mac.hack.setting.base.SettingSlider;
import com.google.common.eventbus.Subscribe;

public class HandProgress extends Module {

	public HandProgress() {
		super("HandProgress", KEY_UNBOUND, Category.RENDER, "Smaller view of mainhand/offhand",
				new SettingSlider("Mainhand", 0.1, 1.0, 1.0, 1), // 0
				new SettingSlider("Offhand", 0.1, 1.0, 1.0, 1) // 1
		);
	}

	@Subscribe
	public void tick(EventTick event) {
		FirstPersonRendererAccessor accessor = (FirstPersonRendererAccessor) mc.gameRenderer.firstPersonRenderer;

		// Refresh the item held in hand every tick
		accessor.setItemStackMainHand(mc.player.getMainHandStack());
		accessor.setItemStackOffHand(mc.player.getOffHandStack());

		// Set the item render height
		accessor.setEquippedProgressMainHand((float) this.getSetting(0).asSlider().getValue());
		accessor.setEquippedProgressOffHand((float) this.getSetting(1).asSlider().getValue());
	}
}