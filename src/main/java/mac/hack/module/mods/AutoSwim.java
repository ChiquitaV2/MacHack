package mac.hack.module.mods;

import mac.hack.event.events.EventTick;
import mac.hack.module.Category;
import mac.hack.module.Module;
import com.google.common.eventbus.Subscribe;


public class AutoSwim extends Module {

    public AutoSwim() {
        super("AutoSwim", KEY_UNBOUND, Category.MOVEMENT, "automaticall does the swimming animation");
    }




    @Subscribe
    public void onTick(EventTick event) {
        if (!isToggled()) return;
        if(!mc.player.isTouchingWater())
            return;
        if(mc.player.forwardSpeed > 0)
            mc.player.setSprinting(true);
    }

}