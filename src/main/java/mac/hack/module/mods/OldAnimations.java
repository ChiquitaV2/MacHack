package mac.hack.module.mods;

import mac.hack.event.events.EventTick;
import mac.hack.mixin.FirstPersonRendererAccessor;
import mac.hack.module.Category;
import mac.hack.module.Module;
import mac.hack.setting.base.SettingToggle;
import com.google.common.eventbus.Subscribe;
import net.minecraft.item.Items;
import net.minecraft.item.ToolItem;

public class OldAnimations extends Module {

    public OldAnimations() {
        super("OldAnimations", KEY_UNBOUND, Category.RENDER, "Attempts to recreate the 1.7 animations.",
                new SettingToggle("1.7 Eat", false),
                new SettingToggle("1.7 Hold", false),
                new SettingToggle("1.7 Bow", false)
        );
    }

    @Subscribe
    public void tick(EventTick event) {
        FirstPersonRendererAccessor accessor = (FirstPersonRendererAccessor) mc.gameRenderer.firstPersonRenderer;

        // Refresh the item held in hand every tick
        accessor.setItemStackMainHand(mc.player.getMainHandStack());
        accessor.setItemStackOffHand(mc.player.getOffHandStack());

        // Set the item render height
        float FOOD_HEIGHT = 0.8f;
        float HELD_HEIGHT = 0.98f;
        float BOW_HEIGHT = 0.87f;
        if (getSetting(0).asToggle().state && mc.player.inventory.getMainHandStack().isFood()) {
            accessor.setEquippedProgressMainHand(FOOD_HEIGHT);
        }
        if (getSetting(1).asToggle().state && mc.player.inventory.getMainHandStack().getItem() instanceof ToolItem) {
            accessor.setEquippedProgressMainHand(HELD_HEIGHT);
        }
        if (getSetting(2).asToggle().state && mc.player.inventory.getMainHandStack().getItem() == Items.BOW) {
            accessor.setEquippedProgressMainHand(BOW_HEIGHT);
        }
    }
}
