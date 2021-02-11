package mac.hack;

import com.google.common.eventbus.EventBus;
import mac.hack.module.Module;
import mac.hack.module.ModuleManager;
import mac.hack.module.mods.ClickGui;
import mac.hack.utils.FriendManager;
import mac.hack.utils.Rainbow;
import mac.hack.utils.file.MacFileHelper;
import mac.hack.utils.file.MacFileMang;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;


public class MacHack implements ModInitializer {

    public static final String VERSION = "B1503";
    public static final String NAME = "MacHack ";
    public static final int INTVERSION = 24;

    public static EventBus eventBus = new EventBus();


    public static FriendManager friendMang;
//	public static CapeUtils capeUtils;

    @Override
    public void onInitialize() {

        MacFileMang.init();
        MacFileHelper.readModules();

        ClickGui.clickGui.initWindows();
        MacFileHelper.readClickGui();
        MacFileHelper.readPrefix();
        MacFileHelper.readFriends();
        for (Module m : ModuleManager.getModules()) m.init();

        eventBus.register(new Rainbow());

        eventBus.register(new ModuleManager());

        if (!MacFileMang.fileExists("xrayblocks.txt")) {
            MacFileMang.createFile("xrayblocks.txt");
        }
        if (!MacFileMang.fileExists("nukerblocks.txt")) {
            MacFileMang.createFile("nukerblocks.txt");
        }
        if (!MacFileMang.fileExists("drawn.txt")) {
            MacFileMang.createFile("drawn.txt");
        }
        for (String s : MacFileMang.readFileLines("drawn.txt")) {
            for (Module m : ModuleManager.getModules()) {
                if (m.getName().toLowerCase().equals(s.toLowerCase())) {
                    m.setDrawn(false);
                }
            }
        }
        MinecraftClient.getInstance().execute(this::updateTitle);

        //if (MinecraftClient.getInstance().player.getName().toString() == "EskerePvP") MinecraftClient.getInstance().close();

        if (!MacFileMang.fileExists("cleanchat.txt")) {
            MacFileMang.createFile("cleanchat.txt");
            MacFileMang.appendFile("nigger", "cleanchat.txt");
            MacFileMang.appendFile("fag", "cleanchat.txt");
            MacFileMang.appendFile("discord.gg", "cleanchat.txt");
            MacFileMang.appendFile("retard", "cleanchat.txt");
            MacFileMang.appendFile("autism", "cleanchat.txt");
            MacFileMang.appendFile("chink", "cleanchat.txt");
            MacFileMang.appendFile("tranny", "cleanchat.txt");
            MacFileMang.appendFile("fuck", "cleanchat.txt");
            MacFileMang.appendFile("shit", "cleanchat.txt");
            MacFileMang.appendFile("nigga", "cleanchat.txt");

        }
    }

    private void updateTitle() {
        final Window window = MinecraftClient.getInstance().getWindow();
        window.setTitle(NAME + VERSION);
    }
}
