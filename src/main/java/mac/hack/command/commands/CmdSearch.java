package mac.hack.command.commands;

import mac.hack.command.Command;
import mac.hack.utils.MacLogger;
import mac.hack.utils.file.MacFileMang;
import net.minecraft.block.Blocks;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.List;

public class CmdSearch extends Command {
    @Override
    public String getAlias() {
        return "search";
    }

    @Override
    public String getDescription() {
        return "Edit search blocks";
    }

    @Override
    public String getSyntax() {
        return "search add [block] | search remove [block] | search clear | search list";
    }

    @Override
    public void onCommand(String command, String[] args) throws Exception {
        MacFileMang.createFile("searchblocks.txt");

        List<String> lines = MacFileMang.readFileLines("searchblocks.txt");
        lines.removeIf(s -> s.isEmpty());
        System.out.println(lines);

        if (args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("remove")) {
            String block = (args[1].contains(":") ? "" : "minecraft:") + args[1].toLowerCase();

            if (args[0].equalsIgnoreCase("add")) {
                if (Registry.BLOCK.get(new Identifier(block)) == Blocks.AIR) {
                    MacLogger.errorMessage("Invalid Block: " + args[1]);
                    return;
                } else if (lines.contains(block)) {
                    MacLogger.errorMessage("Block is already added!");
                    return;
                }

                MacFileMang.appendFile(block, "searchblocks.txt");


                MacLogger.infoMessage("Added Block: " + args[1]);

            } else if (args[0].equalsIgnoreCase("remove")) {
                if (lines.contains(block)) {
                    lines.remove(block);

                    String s = "";
                    for (String s1 : lines) s += s1 + "\n";

                    MacFileMang.createEmptyFile("searchblocks.txt");
                    MacFileMang.appendFile(s, "searchblocks.txt");


                    MacLogger.infoMessage("Removed Block: " + args[1]);
                } else {
                    MacLogger.errorMessage("Block Not In List: " + args[1]);
                }
            }
        } else if (args[0].equalsIgnoreCase("clear")) {
            MacFileMang.createEmptyFile("searchblocks.txt");
            MacLogger.infoMessage("Cleared Xray Blocks");
        } else if (args[0].equalsIgnoreCase("list")) {
            String s = "";
            for (String l : lines) {
                s += "\n\u00a76" + l;
            }

            MacLogger.infoMessage(s);
        }
    }
}
