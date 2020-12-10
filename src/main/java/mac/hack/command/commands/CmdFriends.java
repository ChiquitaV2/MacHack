package mac.hack.command.commands;

import mac.hack.MacHack;
import mac.hack.command.Command;
import mac.hack.utils.MacLogger;

public class CmdFriends extends Command {

	@Override
	public String getAlias() {
		return "friends";
	}

	@Override
	public String getDescription() {
		return "Manage friends";
	}

	@Override
	public String getSyntax() {
		return "friends add [user] | friends remove [user] | friends list | friends clear";
	}

	@Override
	public void onCommand(String command, String[] args) throws Exception {
		if (args[0].equalsIgnoreCase("add")) {
			if (args.length < 2) {
				MacLogger.errorMessage("No username selected");
				MacLogger.errorMessage(getSyntax());
				return;
			}

			MacHack.friendMang.add(args[1]);
			MacLogger.infoMessage("Added \"" + args[1] + "\" to the friend list");
		} else if (args[0].equalsIgnoreCase("remove")) {
			if (args.length < 2) {
				MacLogger.errorMessage("No username selected");
				MacLogger.errorMessage(getSyntax());
				return;
			}

			MacHack.friendMang.remove(args[1].toLowerCase());
			MacLogger.infoMessage("Removed \"" + args[1] + "\" from the friend list");
		} else if (args[0].equalsIgnoreCase("list")) {
			String text = "";

			for (String f: MacHack.friendMang.getFriends()) {
				text += "\n\u00a72" + f;
			}

			MacLogger.infoMessage(text);
		} else if (args[0].equalsIgnoreCase("clear")) {
			MacHack.friendMang.getFriends().clear();

			MacLogger.infoMessage("Cleared Friend list");
		}
	}

}
