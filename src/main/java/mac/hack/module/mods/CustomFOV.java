package mac.hack.module.mods;

import com.google.common.eventbus.Subscribe;
import mac.hack.event.events.EventReadPacket;
import mac.hack.module.Category;
import mac.hack.module.Module;
import mac.hack.setting.base.SettingSlider;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.GameOptions;
import net.minecraft.network.packet.s2c.play.CloseScreenS2CPacket;
import net.minecraft.network.packet.s2c.play.DisconnectS2CPacket;

public class CustomFOV extends Module {

    public CustomFOV() {
        super ("CustomFOV", KEY_UNBOUND, Category.RENDER, "a s d f",
                new SettingSlider("Scale", 0, 1, 0.3, 1),
                new SettingSlider("PrevFOV", 30, 110, 100, 0));
    }

    public void toggleNoSave() {

    }

    public void onEnable() {
        if (mc.options == null) {
            return;
        }
        MinecraftClient mc = MinecraftClient.getInstance();
        GameOptions options = mc.options;
        if (mc.world != null) {
            options.fov = getSetting(1).asSlider().getValue() * (1 + getSetting(0).asSlider().getValue());
        }
    }

    public void onDisable() {
        MinecraftClient mc = MinecraftClient.getInstance();
        mc.options.fov = getSetting(1).asSlider().getValue();
    }

    @Subscribe
    private void EventDisconnect(EventReadPacket event) {
        if (event.getPacket() instanceof CloseScreenS2CPacket || event.getPacket() instanceof DisconnectS2CPacket)
            setToggled(false);
    }
}