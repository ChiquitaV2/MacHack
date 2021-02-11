package mac.hack.module.mods;

import mac.hack.module.Category;
import mac.hack.module.Module;
import mac.hack.setting.base.SettingToggle;

public class Notifications extends Module {

    public Notifications() {
        super("Notifications", KEY_UNBOUND, Category.CHAT, "Notifications out of game",
                new SettingToggle("ToggleNotifications", true).withDesc("Sends a notification when module is toggeled"),
                new SettingToggle("Totem", true).withDesc("Notifies you when Totem is put in your hand"),
                new SettingToggle("Offhand", true).withDesc("Notifies you when something is put in your offhand")

        );
    }
}