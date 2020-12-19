package mac.hack.module.mods;

import com.google.common.eventbus.Subscribe;
import mac.hack.event.events.EventWorldRender;
import mac.hack.module.Category;
import mac.hack.module.Module;
import mac.hack.setting.base.SettingSlider;
import mac.hack.setting.base.SettingToggle;
import mac.hack.utils.EntityUtil;
import mac.hack.utils.EntityUtils;
import mac.hack.utils.RenderUtils;
import net.minecraft.util.math.Box;

public class RenderAxis extends Module {
    public RenderAxis() {
        super("RenderAxis", KEY_UNBOUND, Category.RENDER, "Renders the axis",
                new SettingSlider("R: ", 0.0D, 255.0D, 255.0D, 0),
                new SettingSlider("G: ", 0.0D, 255.0D, 255.0D, 0),
                new SettingSlider("B: ", 0.0D, 255.0D, 255.0D, 0),
                new SettingSlider("I", 0.0f, 1.0f, 1.0f, 1),
                new SettingToggle("Thick", false),
                new SettingSlider("Length", 10, 80, 30, 0)
        );
    }

    @Subscribe
    public void onWorldRender(EventWorldRender event) {
        float r = (float) (getSetting(0).asSlider().getValue() / 255.0D);
        float g = (float) (getSetting(1).asSlider().getValue() / 255.0D);
        float b = (float) (getSetting(2).asSlider().getValue() / 255.0D);
        float i = (float) (getSetting(3).asSlider().getValue());
        switch (EntityUtils.determineHighway()) {
            case 1:
                if (!getSetting(4).asToggle().state) RenderUtils.drawFilledBox(new Box(mc.player.getX() + getSetting(5).asSlider().getValue(), mc.player.getY(), 0.4975, mc.player.getX() - getSetting(5).asSlider().getValue(), mc.player.getY() - 0.05, 0.5025), r, g, b, i);
                else if (getSetting(4).asToggle().state) RenderUtils.drawFilledBox(new Box(mc.player.getX() + getSetting(5).asSlider().getValue(), mc.player.getY(), 1, mc.player.getX() - getSetting(5).asSlider().getValue(), mc.player.getY() - 1, 0), r, g, b, i);
                break;
            case 4:
                if (!getSetting(4).asToggle().state) RenderUtils.drawFilledBox(new Box(mc.player.getX() + getSetting(5).asSlider().getValue(), mc.player.getY(), -0.4975, mc.player.getX() - getSetting(5).asSlider().getValue(), mc.player.getY() - 0.05, -0.5025), r, g, b, i);
                else if (getSetting(4).asToggle().state) RenderUtils.drawFilledBox(new Box(mc.player.getX() + getSetting(5).asSlider().getValue(), mc.player.getY(), -1, mc.player.getX() - getSetting(5).asSlider().getValue(), mc.player.getY() - 1, 0), r, g, b, i);
                break;
            case 7:
                if (!getSetting(4).asToggle().state) RenderUtils.drawFilledBox(new Box(-0.4975, mc.player.getY(), mc.player.getZ() + getSetting(5).asSlider().getValue(), -0.5025, mc.player.getY() - 0.05, mc.player.getZ() - getSetting(5).asSlider().getValue()), r, g, b, i);
                else if (getSetting(4).asToggle().state) RenderUtils.drawFilledBox(new Box(-1, mc.player.getY(), mc.player.getZ() + getSetting(5).asSlider().getValue(), 0, mc.player.getY() - 1, mc.player.getZ() - getSetting(5).asSlider().getValue()), r, g, b, i);
                break;
            case 8:
                if (!getSetting(4).asToggle().state) RenderUtils.drawFilledBox(new Box(0.4975, mc.player.getY(), mc.player.getZ() + getSetting(5).asSlider().getValue(), 0.5025, mc.player.getY() - 0.05, mc.player.getZ() - getSetting(5).asSlider().getValue()), r, g, b, i);
                else if (!getSetting(4).asToggle().state) RenderUtils.drawFilledBox(new Box(0, mc.player.getY(), mc.player.getZ() + getSetting(5).asSlider().getValue(), 1, mc.player.getY() - 1, mc.player.getZ() - getSetting(5).asSlider().getValue()), r, g, b, i);
                break;
        }
    }
}
