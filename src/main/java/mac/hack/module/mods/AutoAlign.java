package mac.hack.module.mods;

import mac.hack.module.Category;
import mac.hack.module.Module;
import mac.hack.module.ModuleManager;
import mac.hack.setting.base.SettingMode;
import mac.hack.setting.base.SettingSlider;
import mac.hack.utils.MacLogger;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;

import static mac.hack.utils.EntityUtils.determineHighway;

public class AutoAlign extends Module {

    public AutoAlign() {
        super("AutoAlign", KEY_UNBOUND, Category.MISC, "Resets your yaw and then disables.",
                new SettingMode("Mode ", "Auto", "Choose"),
                new SettingSlider("Yaw", -179, 180, -90, 0));
    }

    public void onEnable() {
        MinecraftClient mc = MinecraftClient.getInstance();
        PlayerEntity player = mc.player;
        if (mc.world == null) return;

        if (getSettings().get(0).asMode().mode == 1) {
            player.yaw = (float) getSettings().get(1).asSlider().getValue();
        }
        else if (getSettings().get(0).asMode().mode == 0) {
            switch (determineHighway()) {
                case 1: player.yaw = -90; break;
                case 2: player.yaw = -45; break;
                case 3: player.yaw = -135; break;
                case 4: player.yaw = 90; break;
                case 5: player.yaw = 45; break;
                case 6: player.yaw = 135; break;
                case 7: player.yaw = 0; break;
                case 8: player.yaw = 180; break;
            }
        }
        ModuleManager.getModule(AutoAlign.class).toggle();
    }

}