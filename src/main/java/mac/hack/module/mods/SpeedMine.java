package mac.hack.module.mods;

import mac.hack.event.events.EventTick;
import mac.hack.module.Category;
import mac.hack.module.Module;
import mac.hack.setting.base.SettingMode;
import mac.hack.setting.base.SettingSlider;
import mac.hack.setting.base.SettingToggle;
import com.google.common.eventbus.Subscribe;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

public class SpeedMine extends Module {

	public SpeedMine() {
		super("SpeedMine", KEY_UNBOUND, Category.EXPLOITS, "Allows you to mine at sanic speeds",
				new SettingMode("Mode", "Haste", "OG"),
				new SettingSlider("Haste Lvl", 1, 3, 1, 0),
				new SettingSlider("Cooldown", 0, 4, 1, 0),
				new SettingSlider("Multiplier", 1, 9, 1.3, 1),
				new SettingToggle("AntiFatigue", true),
				new SettingToggle("AntiOffGround", true));
	}

	@Override
	public void onDisable() {
		super.onDisable();
		mc.player.removeStatusEffect(StatusEffects.HASTE);
	}

	@Subscribe
	public void onTick(EventTick event) {
		if (this.getSetting(0).asMode().mode == 0) {
			mc.player.addStatusEffect(new StatusEffectInstance(StatusEffects.HASTE, 1, (int) getSetting(1).asSlider().getValue() - 1));
		}
	}
}