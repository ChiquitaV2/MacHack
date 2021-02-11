package mac.hack.module.mods;

import com.google.common.eventbus.Subscribe;
import mac.hack.MacHack;
import mac.hack.event.events.EventTick;
import mac.hack.module.Category;
import mac.hack.module.Module;
import mac.hack.setting.base.SettingMode;
import mac.hack.setting.base.SettingToggle;
import mac.hack.utils.DiscordRPCManager;
import mac.hack.utils.file.MacFileHelper;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;
import net.minecraft.SharedConstants;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.apache.commons.lang3.RandomUtils;

public class DiscordRPCMod extends Module {

    private String customText1 = "top text";
    private String customText2 = "bottom text";

    private int tick = 0;

    private boolean silent;

    public DiscordRPCMod() {
        super("DiscordRPC", KEY_UNBOUND, Category.MISC, "Discord RPC, use the \"rpc\" command to set a custom status",
                new SettingMode("Text 1: ", "Playing %server%", "%server%", "%type%", "%username% ontop", "Minecraft %mcver%", "%username%", "<- bad client", "%custom%"),
                new SettingMode("Text 2: ", "%hp% hp - Holding %item%", "%username% - %hp% hp", "Holding %item%", "%hp% hp - At %coords%", "At %coords%", "%custom%"),
                new SettingMode("Elapsed: ", "Normal", "Random", "Backwards", "None"),
                new SettingToggle("Silent", false));
    }

    public void init() {
        String t1 = MacFileHelper.readMiscSetting("discordRPCTopText");
        String t2 = MacFileHelper.readMiscSetting("discordRPCBottomText");

        if (t1 != null) customText1 = t1;
        if (t2 != null) customText2 = t2;
    }

    public void onEnable() {
        silent = getSettings().get(3).asToggle().state;

        tick = 0;
        DiscordRPCManager.start(silent ? "783894759671332864" : "783894759671332864");

        super.onEnable();
    }

    public void onDisable() {
        DiscordRPCManager.stop();

        super.onDisable();
    }

    @Subscribe
    public void onTick(EventTick event) {
        if (silent != getSettings().get(3).asToggle().state) {
            onDisable();
            onEnable();
        }

        if (tick % 40 == 0) {
            String text1 = customText1;
            String text2 = customText2;
            long start = 0;

            switch (getSettings().get(0).asMode().mode) {
                case 0:
                    if (mc.getCurrentServerEntry() != null && !mc.getCurrentServerEntry().address.contains(".*\\d.*"))
                        text1 = "Playing " + mc.getCurrentServerEntry().address;
                    else text1 = "Playing Singleplayer or homehosted server";

                    break;
                case 1:
                    if (mc.getCurrentServerEntry() != null && !mc.getCurrentServerEntry().address.contains(".*\\d.*"))
                        text1 = mc.getCurrentServerEntry().address;
                    else text1 = "Singleplayer or homehosted server";

                    break;
                case 2:
                    if (mc.getCurrentServerEntry() != null) text1 = "Multiplayer";
                    else text1 = "Singleplayer";

                    break;
                case 3:
                    text1 = mc.player.getEntityName() + " Ontop!";
                    break;
                case 4:
                    text1 = "Minecraft " + SharedConstants.getGameVersion().getName();
                    break;
                case 5:
                    text1 = mc.player.getEntityName();
                    break;
                case 6:
                    text1 = "<- bad client";
                    break;
            }

            ItemStack currentItem = mc.player.inventory.getMainHandStack();
            String itemName = currentItem.getItem() == Items.AIR ? "Nothing" :
                    (currentItem.getCount() > 1 ? currentItem.getCount() + " " : "") + currentItem.getItem().getName().getString();

            switch (getSettings().get(1).asMode().mode) {
                case 0:
                    text2 = (int) mc.player.getHealth() + " hp - Holding " + itemName;
                    break;
                case 1:
                    text2 = mc.player.getEntityName() + " - " + (int) mc.player.getHealth() + " hp";
                    break;
                case 2:
                    text2 = "Holding " + itemName;
                    break;
                case 3:
                    text2 = (int) mc.player.getHealth() + " hp - At " + mc.player.getBlockPos().toShortString();
                    break;
                case 4:
                    text2 = "At " + mc.player.getBlockPos().toShortString();
                    break;
            }

            switch (getSettings().get(2).asMode().mode) {
                case 0:
                    start = System.currentTimeMillis() - tick * 50;
                    break;
                case 1:
                    start = System.currentTimeMillis() - RandomUtils.nextInt(0, 86400000);
                    break;
                case 2:
                    start = 1590000000000l + tick * 100;
                    break;
            }

            DiscordRPC.discordUpdatePresence(
                    new DiscordRichPresence.Builder(text2)
                            .setBigImage("machacklogo", silent ? "Minecraft " + SharedConstants.getGameVersion().getName() : "MacHack " + MacHack.VERSION)
                            .setDetails(text1).setStartTimestamps(start).build());
        }

        if (tick % 200 == 0) {
            DiscordRPC.discordRunCallbacks();
        }

        tick++;
    }

    public void setText(String t1, String t2) {
        customText1 = t1;
        customText2 = t2;
    }
}
