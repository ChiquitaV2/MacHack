package mac.hack.module.mods;

import mac.hack.module.Category;
import mac.hack.module.Module;
import mac.hack.setting.base.SettingSlider;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.GameOptions;

public class CustomFOV extends Module {

    public CustomFOV() {
        super ("CustomFOV", KEY_UNBOUND, Category.RENDER, "a s d f",
                new SettingSlider("Scale", 0, 1, 0.3, 1));
    }

    public void toggleNoSave() {

    }

    public void onEnable() {
        MinecraftClient mc = MinecraftClient.getInstance();
        GameOptions options = mc.options;
        if (mc.world != null) {
            options.fov = options.fov * (1 + getSetting(0).asSlider().getValue());
        }
    }

    public void onDisable() {
        MinecraftClient mc = MinecraftClient.getInstance();
        GameOptions options = mc.options;
        options.fov = options.fov / (1 + getSetting(0).asSlider().getValue());
    }
}