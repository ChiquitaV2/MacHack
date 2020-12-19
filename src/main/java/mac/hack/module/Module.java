package mac.hack.module;

import mac.hack.MacHack;
import mac.hack.module.mods.ToggleMSGs;
import mac.hack.setting.base.SettingBase;
import mac.hack.utils.MacLogger;
import mac.hack.utils.MacNotify;
import mac.hack.utils.file.MacFileHelper;
import com.google.common.eventbus.Subscribe;
import net.minecraft.client.MinecraftClient;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Module {

	public final static int KEY_UNBOUND = -2;

	protected MinecraftClient mc = MinecraftClient.getInstance();
	private String name;
	private int key;
	private final int defaultKey;
	private boolean toggled;
	private boolean hidden;
	private final Category category;
	private final String desc;
	private List<SettingBase> settings = new ArrayList<>();
	private boolean drawn;

	public Module(String nm, int k, Category c, String d, SettingBase... s) {
		name = nm;
		setKey(k);
		defaultKey = getKey();
		category = c;
		desc = d;
		settings = Arrays.asList(s);
		toggled = false;
		hidden = false;
		drawn = true;
	}


	public void toggle() {
		toggled = !toggled;
		if (toggled) onEnable();
		else onDisable();
	}

	public void onEnable() {
		MacFileHelper.SCHEDULE_SAVE_MODULES = true;

		for (Method method : getClass().getMethods()) {
			if (method.isAnnotationPresent(Subscribe.class)) {
				MacHack.eventBus.register(this);
				break;
			}
		}
		if (ModuleManager.getModule(ToggleMSGs.class).isToggled() && !this.getName().equals("ClickGUI") && !this.getName().equals("ColourChooser")){
			MacLogger.infoMessage(this.getName() + "\u00A7a Enabled");
		}
	}

	public void toggleNoSave()
	{
		this.setToggled(!this.isToggled());
		if (this.isToggled())
		{
			this.onEnable();
		}
		else
		{
			this.onDisable();
		}
		this.onEnable();
	}

	public void onDisable() {
		MacFileHelper.SCHEDULE_SAVE_MODULES = true;

		try {
			for (Method method : getClass().getMethods()) {
				if (method.isAnnotationPresent(Subscribe.class)) {
					MacHack.eventBus.unregister(this);
					break;
				}
			}
		} catch (Exception this_didnt_get_registered_hmm_weird) {
			this_didnt_get_registered_hmm_weird.printStackTrace();
		}
		if (ModuleManager.getModule(ToggleMSGs.class).isToggled() && !this.getName().equals("ClickGUI") && !this.getName().equals("ColourChooser")){
			MacLogger.infoMessage(this.getName() + "\u00A7c Disabled");
		}
	}

	public void init() {
	}

	public String getName() {
		return name;
	}

	public Category getCategory() {
		return category;
	}

	public String getDesc() {
		return desc;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getKey() {
		return key;
	}

	public int getDefaultKey() {
		return defaultKey;
	}

	public List<SettingBase> getSettings() {
		return settings;
	}

	public SettingBase getSetting(int s) {
		return settings.get(s);
	}

	public void setKey(int key) {
		MacFileHelper.SCHEDULE_SAVE_MODULES = true;
		this.key = key;
	}

	public boolean isToggled() {
		return toggled;
	}
	public boolean isHidden() {
		return hidden;
	}
	public void setHidden(boolean h) {
		MacFileHelper.SCHEDULE_SAVE_MODULES = true;
		hidden = h;
	}

	public void setToggled(boolean toggled) {
		this.toggled = toggled;
		if (toggled) onEnable();
		else onDisable();
	}

	public boolean isDrawn() {
		return drawn;
	}

	public void setDrawn(boolean d) {
		drawn = d;
	}

}