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
import mac.hack.module.Module;
import mac.hack.module.ModuleManager;
import mac.hack.utils.MacLogger;
import mac.hack.utils.MacQueue;

public class CmdToggle extends Command {

    @Override
    public String getAlias() {
        return "toggle";
    }

    @Override
    public String getDescription() {
        return "Toggles a mod with a command.";
    }

    @Override
    public String getSyntax() {
        return "toggle [Module]";
    }

    @Override
    public void onCommand(String command, String[] args) throws Exception {
        for (Module m : ModuleManager.getModules()) {
            if (args[0].equalsIgnoreCase(m.getName())) {
                MacQueue.add(() -> m.toggle());
                MacLogger.infoMessage(m.getName() + " Toggled");
                return;
            }
        }
        MacLogger.errorMessage("Module \"" + args[0] + "\" Not Found!");
    }

}
