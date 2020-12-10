package mac.hack.command.commands;

import mac.hack.command.Command;
import mac.hack.utils.MacLogger;
import mac.hack.utils.file.MacFileMang;

import java.util.List;

public class CmdCleanChat extends Command {

    @Override
    public String getAlias() {
        return "cleanchat";
    }

    @Override
    public String getDescription() {
        return "remove or add a word to the cleanchat blacklist";
    }

    @Override
    public String getSyntax() {
        return "cleanchat add/del [word]";
    }

    @Override
    public void onCommand(String command, String[] args) throws Exception {
        if (args[0] == null) {
            MacLogger.errorMessage("Expected add or del.");
            return;
        }
        if (args[0].toLowerCase().contains("add")) {
            MacFileMang.appendFile(args[1].toLowerCase(), "cleanchat.txt");
            MacLogger.infoMessage("Word \"" + args[1] + "\" has been added to the list of blacklisted words");
        } else if (args[0].toLowerCase().contains("del")) {
            List<String> lines = MacFileMang.readFileLines("cleanchat.txt");
            lines.removeIf(s -> s.equals(args[1].toLowerCase()));
            MacFileMang.createEmptyFile("cleanchat.txt");
            for (String line : lines) {
                MacFileMang.appendFile(line.toLowerCase(), "cleanchat.txt");
            }
            MacLogger.infoMessage("Word \"" + args[1] + "\" has been removed from the list of blacklisted words");
        }
    }

}