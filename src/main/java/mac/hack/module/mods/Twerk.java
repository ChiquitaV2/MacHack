package mac.hack.module.mods;

import mac.hack.event.events.EventTick;
import mac.hack.setting.base.SettingSlider;
import mac.hack.module.Category;
import mac.hack.module.Module;
import com.google.common.eventbus.Subscribe;


public class Twerk extends Module {

    public Twerk() {
        super("Twerk", KEY_UNBOUND, Category.MISC, "Auto Twerk (I know but still)",
                new SettingSlider("Speed: ", 1, 10, 1, 1));
    }

    private int speed;

    @Subscribe
    public void onTick(EventTick event) {
        speed++;
        if(speed < 10 - getSettings().get(0).asSlider().getValue())
            return;

        mc.options.keySneak.setPressed(!mc.options.keySneak.isPressed());
        speed = -1;
    }
}