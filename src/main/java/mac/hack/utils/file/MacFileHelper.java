package mac.hack.utils.file;

import mac.hack.MacHack;
import mac.hack.command.Command;
import mac.hack.gui.window.Window;
import mac.hack.module.Module;
import mac.hack.module.ModuleManager;
import mac.hack.module.mods.ClickGui;
import mac.hack.setting.base.SettingBase;
import mac.hack.utils.FriendManager;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

public class MacFileHelper {

	public static boolean SCHEDULE_SAVE_MODULES = false;
	public static boolean SCHEDULE_SAVE_FRIENDS = false;
	public static boolean SCHEDULE_SAVE_CLICKGUI = false;

	public static void saveModules() {
		SCHEDULE_SAVE_MODULES = false;

		JsonObject jo = new JsonObject();

		for (Module m : ModuleManager.getModules()) {
			JsonObject mo = new JsonObject();

			if (m.isToggled() && !m.getName().equals("ClickGui") && !m.getName().equals("Freecam") && !m.getName().equals("ACGameSense")) {
				mo.add("toggled", new JsonPrimitive(true));
			}

			if (m.getKey() >= 0 || m.getDefaultKey() >= 0 /* Force saving of modules with a default bind to prevent them reapplying the default bind */) {
				mo.add("bind", new JsonPrimitive(m.getKey()));
			}

			if (m.isHidden()) {
				mo.add("hidden", new JsonPrimitive(true));
			}

			if (!m.getSettings().isEmpty()) {
				JsonObject so = new JsonObject();
				// Seperate JsonObject with all the settings to keep the extra number so when it reads, it doesn't mess up the order
				JsonObject fullSo = new JsonObject();

				for (SettingBase s : m.getSettings()) {
					String name = s.getName();

					int extra = 0;
					while (fullSo.has(name)) {
						extra++;
						name = s.getName() + extra;
					}

					fullSo.add(name, s.saveSettings());
					if (!s.isDefault()) so.add(name, s.saveSettings());
				}

				if (so.size() != 0) {
					mo.add("settings", so);
				}

			}

			if (mo.size() != 0) {
				jo.add(m.getName(), mo);
			}
		}

		MacJsonHelper.setJsonFile(jo, "modules.json");
	}

	public static void readModules() {
		JsonObject jo = MacJsonHelper.readJsonFile("modules.json");

		if (jo == null) return;

		for (Entry<String, JsonElement> e : jo.entrySet()) {
			Module mod = ModuleManager.getModuleByName(e.getKey());

			if (mod == null) continue;

			if (e.getValue().isJsonObject()) {
				JsonObject mo = e.getValue().getAsJsonObject();
				if (mo.has("toggled")) {
					mod.setToggled(true);
				}
				if (mo.has("bind") && mo.get("bind").isJsonPrimitive() && mo.get("bind").getAsJsonPrimitive().isNumber()) {
					mod.setKey(mo.get("bind").getAsInt());
				}
				if (mo.has("hidden")) {
					mod.setHidden(true);
				}
				if (mo.has("settings") && mo.get("settings").isJsonObject()) {
					for (Entry<String, JsonElement> se : mo.get("settings").getAsJsonObject().entrySet()) {
						// Map to keep track if there are multiple settings with the same name
						HashMap<String, Integer> sNames = new HashMap<>();

						for (SettingBase sb : mod.getSettings()) {
							String name = sNames.containsKey(sb.getName()) ? sb.getName() + sNames.get(sb.getName()) : sb.getName();

							if (name.equals(se.getKey())) {
								sb.readSettings(se.getValue());
								break;
							} else {
								sNames.put(sb.getName(), sNames.containsKey(sb.getName()) ? sNames.get(sb.getName()) + 1 : 1);
							}
						}
					}
				}
			}
		}
	}

	public static void saveClickGui() {
		SCHEDULE_SAVE_CLICKGUI = false;

		MacFileMang.createEmptyFile("clickgui.txt");

		String text = "";
		for (Window w : ClickGui.clickGui.windows) text += w.x1 + ":" + w.y1 + "\n";

		MacFileMang.appendFile(text, "clickgui.txt");
	}

	public static void readClickGui() {
		List<String> lines = MacFileMang.readFileLines("clickgui.txt");

		try {
			int c = 0;
			for (Window w : ClickGui.clickGui.windows) {
				w.x1 = Integer.parseInt(lines.get(c).split(":")[0]);
				w.y1 = Integer.parseInt(lines.get(c).split(":")[1]);
				c++;
			}
		} catch (Exception e) {
		}
	}

	public static void readPrefix() {
		List<String> lines = MacFileMang.readFileLines("prefix.txt");
		if (!lines.isEmpty()) Command.PREFIX = lines.get(0);
	}

	public static void readFriends() {
		MacHack.friendMang = new FriendManager(MacFileMang.readFileLines("friends.txt"));
	}

	public static void saveFriends() {
		SCHEDULE_SAVE_FRIENDS = false;

		String toWrite = "";
		for (String s : MacHack.friendMang.getFriends()) toWrite += s + "\n";

		MacFileMang.createEmptyFile("friends.txt");
		MacFileMang.appendFile(toWrite, "friends.txt");
	}

	public static String readMiscSetting(String key) {
		JsonElement element = MacJsonHelper.readJsonElement(key, "misc.json");

		try {
			return element.getAsString();
		} catch (Exception e) {
			return null;
		}
	}

	public static void saveMiscSetting(String key, String value) {
		MacJsonHelper.addJsonElement(key, new JsonPrimitive(value), "misc.json");
	}

}