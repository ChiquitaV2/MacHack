package mac.hack.module.mods;

import mac.hack.module.Category;
import mac.hack.module.Module;
import mac.hack.setting.base.SettingSlider;
import mac.hack.setting.base.SettingToggle;
import org.lwjgl.glfw.GLFW;

public class Colours extends Module {
    public Colours() {
        super("ColourChooser", GLFW.GLFW_KEY_RIGHT_SHIFT, Category.CLIENT, "HUD color settings",
                new SettingToggle("Rainbow", false),
                new SettingSlider("Red", 0, 255, 177, 1),
                new SettingSlider("Green", 0, 255, 140, 1),
                new SettingSlider("Blue", 0, 255, 254, 1),
                new SettingSlider("TextRed", 0, 255, 255, 1),
                new SettingSlider("TextGreen", 0, 255, 255, 1),
                new SettingSlider("TextBlue", 0, 255, 255, 1));
    }

    @Override
    public void onEnable() {
        setToggled(false);
    }
}