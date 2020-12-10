/*
 * This file is part of the MacHack distribution (https://github.com/BleachDrinker420/bleachhack-1.14/).
 * Copyright (c) 2019 Bleach.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package mac.hack.command.commands;

import mac.hack.command.Command;
import mac.hack.module.ModuleManager;
import mac.hack.module.mods.Xray;
import mac.hack.utils.MacLogger;
import mac.hack.utils.file.MacFileMang;
import net.minecraft.block.Blocks;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.List;

public class CmdXray extends Command {
	//brb stealing your nuker command
	@Override
	public String getAlias() {
		return "xray";
	}

	@Override
	public String getDescription() {
		return "Edit Xray blocks";
	}

	@Override
	public String getSyntax() {
		return "xray add [block] | xray remove [block] | xray clear | xray list";
	}

	@Override
	public void onCommand(String command, String[] args) throws Exception {
		if (args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("remove")) {

			Xray xray = (Xray) ModuleManager.getModule(Xray.class);
			String block = (args[1].contains(":") ? "" : "minecraft:") + args[1].toLowerCase();

			if (args[0].equalsIgnoreCase("add")) {
				if (Registry.BLOCK.get(new Identifier(block)) == Blocks.AIR) {
					MacLogger.errorMessage("Invalid Block: " + args[1]);
					return;
				} else if (xray.getVisibleBlocks().contains(Registry.BLOCK.get(new Identifier(block)))) {
					MacLogger.errorMessage("Block is already added!");
					return;
				}

				MacFileMang.appendFile(block, "xrayblocks.txt");
				xray.toggle();
				xray.toggle();
				MacLogger.infoMessage("Added Block: " + args[1]);

			} else if (args[0].equalsIgnoreCase("remove")) {
				List<String> lines = MacFileMang.readFileLines("xrayblocks.txt");

				if (lines.contains(block)) {
					lines.remove(block);

					String s = "";
					for (String s1 : lines) s += s1 + "\n";

					MacFileMang.createEmptyFile("xrayblocks.txt");
					MacFileMang.appendFile(s, "xrayblocks.txt");
					xray.toggle();
					xray.toggle();
					MacLogger.infoMessage("Removed Block: " + args[1]);
				} else {
					MacLogger.errorMessage("Block Not In List: " + args[1]);
				}
			}
		} else if (args[0].equalsIgnoreCase("clear")) {
			MacFileMang.createEmptyFile("xrayblocks.txt");
			MacLogger.infoMessage("Cleared Xray Blocks");
		} else if (args[0].equalsIgnoreCase("list")) {
			List<String> lines = MacFileMang.readFileLines("xrayblocks.txt");

			String s = "";
			for (String l: lines) {
				s += "\u00a76" + l + "\n";
			}

			MacLogger.infoMessage(s);
		}
	}
}

