package mac.hack.module.mods;

import mac.hack.gui.clickgui.ClickGuiScreen;
import mac.hack.module.Category;
import mac.hack.module.Module;
import mac.hack.setting.base.SettingMode;
import mac.hack.setting.base.SettingSlider;
import mac.hack.setting.base.SettingToggle;
import org.lwjgl.glfw.GLFW;

public class ClickGui extends Module {

	public static ClickGuiScreen clickGui = new ClickGuiScreen();

	public ClickGui() {
		super("ClickGUI", GLFW.GLFW_KEY_RIGHT_SHIFT, Category.CLIENT, "Draws the clickgui",
				new SettingSlider("Length", 70, 85, 85, 0),
				new SettingToggle("Search bar", true),
				new SettingToggle("Help", true),
				new SettingToggle("Static descriptions", true),
				new SettingMode("Theme", "Default"),
				new SettingToggle("Snow", false),
				new SettingToggle("BRIcon", false).withDesc("NEEDS TEXTURE PACK :("));
	}

	public void onEnable() {
		mc.openScreen(clickGui);
		setToggled(false);
	}
}