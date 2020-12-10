package mac.hack.command.commands;

import java.util.Arrays;

import mac.hack.command.Command;
import mac.hack.module.ModuleManager;
import mac.hack.module.mods.CustomChat;
import mac.hack.utils.MacLogger;
import mac.hack.utils.file.MacFileHelper;

public class CmdCustomChat extends Command {

	@Override
	public String getAlias() {
		return "customchat";
	}

	@Override
	public String getDescription() {
		return "Changes customchat prefix and suffix";
	}

	@Override
	public String getSyntax() {
		return "customchat current | customchat reset | customchat prefix [prefix] | customchat suffix [suffix]";
	}

	@Override
	public void onCommand(String command, String[] args) throws Exception {
		if (args.length == 0) {
			MacLogger.errorMessage(getSyntax());
			return;
		}
		
		CustomChat chat = (CustomChat) ModuleManager.getModule(CustomChat.class);
		if (args[0].equalsIgnoreCase("current")) {
			MacLogger.infoMessage("Current prefix: \"" + chat.prefix + "\", suffix: \"" + chat.suffix + "\"");
		} else if (args[0].equalsIgnoreCase("reset")) {
			chat.prefix = "";
			chat.suffix = " \u25ba \u0432\u2113\u0454\u03b1c\u043d\u043d\u03b1c\u043a";
			
			MacLogger.infoMessage("Reset the chat prefix and suffix");
			MacFileHelper.saveMiscSetting("customChatPrefix", chat.prefix);
			MacFileHelper.saveMiscSetting("customChatSuffix", chat.suffix);
		} else if (args[0].equalsIgnoreCase("prefix")) {
			chat.prefix = String.join(" ", Arrays.asList(args).subList(1, args.length)).trim() + " ";
			
			MacLogger.infoMessage("Set prefix to: \"" + chat.prefix + "\"");
			MacFileHelper.saveMiscSetting("customChatPrefix", chat.prefix);
		} else if (args[0].equalsIgnoreCase("suffix")) {
			chat.suffix = " " + String.join(" ", Arrays.asList(args).subList(1, args.length)).trim();
			
			MacLogger.infoMessage("Set suffix to: \"" + chat.suffix + "\"");
			MacFileHelper.saveMiscSetting("customChatSuffix", chat.suffix );
		} else {
			MacLogger.errorMessage(getSyntax());
		}
	}

}