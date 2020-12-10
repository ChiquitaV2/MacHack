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

import java.util.List;

import mac.hack.command.Command;
import mac.hack.module.ModuleManager;
import mac.hack.module.mods.Nuker;
import mac.hack.utils.MacLogger;
import mac.hack.utils.file.MacFileMang;
import net.minecraft.block.Blocks;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class CmdNuker extends Command {

	@Override
	public String getAlias() {
		return "nuker";
	}

	@Override
	public String getDescription() {
		return "Edit Nuker Blocks";
	}

	@Override
	public String getSyntax() {
		return "nuker add [block] | nuker remove [block] | nuker clear | nuker list";
	}

	@Override
	public void onCommand(String command, String[] args) throws Exception {
		if (args[0].equalsIgnoreCase("add")) {
			if (Registry.BLOCK.get(new Identifier(args[1].toLowerCase())) == Blocks.AIR) {
				MacLogger.errorMessage("Invalid Block: " + args[1]);
				return;
			}

			MacFileMang.appendFile(args[1].toLowerCase(), "nukerblocks.txt");
			ModuleManager.getModule(Nuker.class).toggle();
			ModuleManager.getModule(Nuker.class).toggle();
			MacLogger.infoMessage("Added Block: " + args[1]);

		} else if (args[0].equalsIgnoreCase("remove")) {
			List<String> lines = MacFileMang.readFileLines("nukerblocks.txt");

			if (lines.contains(args[1].toLowerCase())) {
				lines.remove(args[1].toLowerCase());

				String s = "";
				for (String s1: lines) s += s1 + "\n";

				MacFileMang.createEmptyFile("nukerblocks.txt");
				MacFileMang.appendFile(s, "nukerblocks.txt");

				MacLogger.infoMessage("Removed Block: " + args[1]);
			}

			MacLogger.errorMessage("Block Not In List: " + args[1]);
		} else if (args[0].equalsIgnoreCase("clear")) {
			MacFileMang.createEmptyFile("nukerblocks.txt");
			MacLogger.infoMessage("Cleared Nuker Blocks");
		} else if (args[0].equalsIgnoreCase("list")) {
			List<String> lines = MacFileMang.readFileLines("nukerblocks.txt");

			String s = "";
			for (String l: lines) {
				s += "\u00a76" + l + "\n";
			}

			MacLogger.infoMessage(s);
		}
	}

}
