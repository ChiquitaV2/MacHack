package mac.hack.utils;

import mac.hack.module.ModuleManager;
import mac.hack.module.mods.Colours;

public class ColorUtils {
    public static int guiColour() {
        if (ModuleManager.getModule(Colours.class).getSettings().get(0).asToggle().state) return Rainbow.getInt();
        int red;
        int green;
        int blue;
        red = (int) ModuleManager.getModule(Colours.class).getSettings().get(1).asSlider().getValue();
        green = (int) ModuleManager.getModule(Colours.class).getSettings().get(2).asSlider().getValue();
        blue = (int) ModuleManager.getModule(Colours.class).getSettings().get(3).asSlider().getValue();
        return (0xff << 24) | ((red & 0xff) << 16) | ((green & 0xff) << 8) | (blue & 0xff);
    }
    public static int textColor() {
        int red;
        int green;
        int blue;
        red = (int) ModuleManager.getModule(Colours.class).getSettings().get(4).asSlider().getValue();
        green = (int) ModuleManager.getModule(Colours.class).getSettings().get(5).asSlider().getValue();
        blue = (int) ModuleManager.getModule(Colours.class).getSettings().get(6).asSlider().getValue();
        return (0xff << 24) | ((red & 0xff) << 16) | ((green & 0xff) << 8) | (blue & 0xff);
    }
}