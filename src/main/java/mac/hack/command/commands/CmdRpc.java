package mac.hack.command.commands;

import mac.hack.command.Command;
import mac.hack.module.ModuleManager;
import mac.hack.module.mods.DiscordRPCMod;
import mac.hack.utils.MacLogger;
import mac.hack.utils.file.MacFileHelper;

public class CmdRpc extends Command {

	@Override
	public String getAlias() {
		return "rpc";
	}

	@Override
	public String getDescription() {
		return "Sets custom discord rpc text";
	}

	@Override
	public String getSyntax() {
		return "rpc [top text] [bottom text]";
	}

	@Override
	public void onCommand(String command, String[] args) throws Exception {
		if (args.length != 2) {
			MacLogger.errorMessage(getSyntax());
		}

		((DiscordRPCMod) ModuleManager.getModule(DiscordRPCMod.class)).setText(args[0], args[1]);

		MacLogger.infoMessage("Set RPC to " + args[0] + ", " + args[1]);

		MacFileHelper.saveMiscSetting("discordRPCTopText", args[0]);
		MacFileHelper.saveMiscSetting("discordRPCBottomText", args[1]);
	}

}
