package mac.hack.module.mods;

import mac.hack.command.Command;
import mac.hack.event.events.EventReadPacket;
import mac.hack.module.Category;
import mac.hack.module.Module;
import mac.hack.utils.file.MacFileMang;
import com.google.common.eventbus.Subscribe;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CleanChat extends Module {

    public CleanChat() {
        super("CleanChat", KEY_UNBOUND, Category.CHAT, "checks messages you receive and removes ones with bad words in them! To add words \"" + Command.PREFIX + "cleanchat add/del [word]\"");
    }
    ArrayList blacklistedWords = new ArrayList<String>();
    @Override
    public void onEnable() {
        super.onEnable();
        for (String s : MacFileMang.readFileLines("cleanchat.txt")) {
            blacklistedWords.add(s);
        }
    }

    @Subscribe
    public void onPacketRead(EventReadPacket event) {
        if (event.getPacket() instanceof GameMessageS2CPacket) {
            List<String> allMatches = new ArrayList<String>();
            String text = ((GameMessageS2CPacket) event.getPacket()).getMessage().toString();
            Pattern p = Pattern.compile("text='(.*?)'");   // the pattern to search for
            Matcher m = p.matcher(text);
            while (m.find()) {
                allMatches.add(m.group(1));
            }
            StringBuilder parsed = new StringBuilder();
            for (String s : allMatches)
            {
                parsed.append(s);
            }
            for (Object s : blacklistedWords)
            {
                if (parsed.toString().toLowerCase().contains(s.toString().toLowerCase())) {
                    event.setCancelled(true);
                }
            }

        }
    }

}