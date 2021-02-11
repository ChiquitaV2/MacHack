package mac.hack.module.mods;

import com.google.common.eventbus.Subscribe;
import mac.hack.event.events.EventTick;
import mac.hack.mixin.IKeyBinding;
import mac.hack.module.Category;
import mac.hack.module.Module;
import mac.hack.setting.base.SettingMode;
import mac.hack.setting.base.SettingSlider;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

import java.util.Objects;

public class AutoEat extends Module {

    private int lastSlot = -1;
    private boolean eating = false;
    public AutoEat() {
        super("AutoEat", KEY_UNBOUND, Category.PLAYER, "Auto eats when food is low",
                new SettingMode("Mode", "Hunger", "Health"),
                new SettingSlider("Hunger", 1, 20, 18, 0),
                new SettingSlider("Health", 1, 20, 10, 0)
        );
    }

    private boolean isValid(ItemStack stack, int food) {
        return stack.getItem().getGroup() == ItemGroup.FOOD && (20 - food) >= Objects.requireNonNull(stack.getItem().getFoodComponent()).getHunger();
    }

    @Subscribe
    public void onTick(EventTick event) {
        assert mc.player != null;
        if (getSetting(0).asMode().mode == 0) {
            if (eating && (mc.player.getHungerManager().getFoodLevel() == 20)) {
                if (lastSlot != -1) {
                    mc.player.inventory.selectedSlot = lastSlot;
                    lastSlot = -1;
                }
                eating = false;
                KeyBinding.setKeyPressed(((IKeyBinding) mc.options.keyUse).getBoundKey(), false);
                return;
            }
        } else {
            if (eating && (mc.player.getHealth() + mc.player.getAbsorptionAmount() > getSetting(2).asSlider().getValue())) {
                if (lastSlot != -1) {
                    mc.player.inventory.selectedSlot = lastSlot;
                    lastSlot = -1;
                }
                eating = false;
                KeyBinding.setKeyPressed(((IKeyBinding) mc.options.keyUse).getBoundKey(), false);
                return;
            }
        }
        if (eating) return;
        if (getSetting(0).asMode().mode == 0) {
            if (mc.player.getHungerManager().getFoodLevel() < getSetting(1).asSlider().getValue()) {
                for (int i = 0; i < 9; i++) {
                    if (mc.player.inventory.getStack(i).isFood()) {
                        lastSlot = mc.player.inventory.selectedSlot;
                        mc.player.inventory.selectedSlot = i;
                        eating = true;
                        KeyBinding.setKeyPressed(((IKeyBinding) mc.options.keyUse).getBoundKey(), true);
                        return;
                    }
                }
            }
        } else {
            if (mc.player.getHealth() + mc.player.getAbsorptionAmount() <= getSetting(2).asSlider().getValue()) {
                for (int i = 0; i < 9; i++) {
                    if (mc.player.inventory.getStack(i).isFood()) {
                        lastSlot = mc.player.inventory.selectedSlot;
                        mc.player.inventory.selectedSlot = i;
                        eating = true;
                        KeyBinding.setKeyPressed(((IKeyBinding) mc.options.keyUse).getBoundKey(), true);
                        return;
                    }
                }
            }
        }
    }

    public boolean isEating() {
        return eating;
    }
}
