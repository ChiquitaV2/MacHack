package mac.hack.module.mods;

import com.google.common.eventbus.Subscribe;
import mac.hack.MacHack;
import mac.hack.event.events.EventTick;
import mac.hack.module.Category;
import mac.hack.module.Module;
import mac.hack.setting.base.SettingToggle;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.Text;

import java.util.Objects;

public class TablistTweaks extends Module {

    public TablistTweaks() {
        super("TablistTweaks", KEY_UNBOUND, Category.RENDER, "Tablist Modifications",
                new SettingToggle("TabFriends", true).withDesc("Highlights friends in the tablist"),
                new SettingToggle("TabPing", false).withDesc("Adds player ping to the tablist"));
    }

    @Subscribe
    public void tick(EventTick event) {
        assert mc.player != null;
        if (mc.player.age % 10 == 0) {
            for (PlayerListEntry f : mc.player.networkHandler.getPlayerList()) {
                if (MacHack.friendMang.has(f.getProfile().getName()) && getSetting(0).asToggle().state) {
                    Objects.requireNonNull(mc.player.networkHandler.getPlayerListEntry(f.getProfile().getName())).setDisplayName(Text.of("\u00A7b" + f.getProfile().getName() + (getSetting(1).asToggle().state ? " \u00A77[\u00A7f" + f.getLatency() + "ms\u00A77]" : "")));
                } else {
                    Objects.requireNonNull(mc.player.networkHandler.getPlayerListEntry(f.getProfile().getName())).setDisplayName(Text.of("\u00A7f" + f.getProfile().getName() + (getSetting(1).asToggle().state ? " \u00A77[\u00A7f" + f.getLatency() + "ms\u00A77]" : "")));
                }
            }
        }
    }

}