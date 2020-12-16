package mac.hack.module.mods;

import mac.hack.event.events.EventTick;
import mac.hack.module.Category;
import mac.hack.module.Module;
import mac.hack.module.ModuleManager;
import com.google.common.eventbus.Subscribe;

public class AutoWalk extends Module {

	public AutoWalk() {
		super("AutoWalk", KEY_UNBOUND, Category.MOVEMENT, "Automatically walks/flies forward");
	}

	public void onDisable() {
		mc.options.keyForward.setPressed(false);
		super.onDisable();
	}

	@Subscribe
	public void onTick(EventTick event) {
		AutoEat autoEat = (AutoEat) ModuleManager.getModule(AutoEat.class);
		AutoTunnel at = ((AutoTunnel) ModuleManager.getModule(AutoTunnel.class));
		boolean pause = at.PauseAutoWalk() || autoEat.isEating();
		if (!pause) {
			mc.options.keyForward.setPressed(true);
		}
		else if (pause) {
			mc.options.keyForward.setPressed(false);
		}
	}
}

