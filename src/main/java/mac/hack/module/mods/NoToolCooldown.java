package mac.hack.module.mods;

import mac.hack.module.Category;
import mac.hack.module.Module;
import mac.hack.setting.base.SettingSlider;

public class NoToolCooldown extends Module {

    public NoToolCooldown() {
        super("NoToolCooldown", KEY_UNBOUND, Category.COMBAT, "No Tool Cooldown",
                new SettingSlider("Timer", 7, 20, 7, 1));
    }
}