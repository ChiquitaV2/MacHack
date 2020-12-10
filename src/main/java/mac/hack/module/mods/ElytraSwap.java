package mac.hack.module.mods;

import mac.hack.module.Category;
import mac.hack.module.Module;
import mac.hack.module.ModuleManager;

public class ElytraSwap extends Module {

    public ElytraSwap() {
        super("ElytraSwap", KEY_UNBOUND, Category.PLAYER, "Swaps elytra out for chestplate and vice versa");
    }
    public void onEnable() {
        if (!ModuleManager.getModule(AutoArmor.class).isToggled()) {
            ModuleManager.getModule(AutoArmor.class).toggle();
        }
        if (ModuleManager.getModule(AutoArmor.class).getSettings().get(1).asToggle().state == Boolean.TRUE) {
            ModuleManager.getModule(AutoArmor.class).getSettings().get(1).asToggle().state = Boolean.FALSE;
        } else if (ModuleManager.getModule(AutoArmor.class).getSettings().get(1).asToggle().state == Boolean.FALSE) {
            ModuleManager.getModule(AutoArmor.class).getSettings().get(1).asToggle().state = Boolean.TRUE;
        }
        this.setToggled(false);
    }
}