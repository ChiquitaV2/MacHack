package mac.hack.module.mods;

import com.google.common.eventbus.Subscribe;
import mac.hack.event.events.EventTick;
import mac.hack.module.Category;
import mac.hack.module.Module;
import mac.hack.setting.base.SettingSlider;


public class Twerk extends Module {

    private int speed;

    public Twerk() {
        super("Twerk", KEY_UNBOUND, Category.MISC, "Auto Twerk (I know but still)",
                new SettingSlider("Speed: ", 1, 10, 1, 1));
    }

    @Subscribe
    public void onTick(EventTick event) {
        speed++;
        if (speed < 10 - getSettings().get(0).asSlider().getValue())
            return;

        mc.options.keySneak.setPressed(!mc.options.keySneak.isPressed());
        speed = -1;
    }
}