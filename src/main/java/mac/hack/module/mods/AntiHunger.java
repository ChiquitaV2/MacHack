package mac.hack.module.mods;

import mac.hack.event.events.EventSendPacket;
import mac.hack.module.Category;
import mac.hack.module.Module;
import mac.hack.setting.base.SettingToggle;
import mac.hack.utils.FabricReflect;
import com.google.common.eventbus.Subscribe;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class AntiHunger extends Module {

    private boolean bool = false;

    public AntiHunger() {
        super("AntiHunger", KEY_UNBOUND, Category.PLAYER, "Minimizes the amount of hunger you use",
                new SettingToggle("Relaxed", false).withDesc("Only activates every other ticks, might fix getting fly kicked"));
    }

    @Subscribe
    public void onSendPacket(EventSendPacket event) {
        if (event.getPacket() instanceof PlayerMoveC2SPacket) {
            if (mc.player.getVelocity().y != 0 && !mc.options.keyJump.isPressed() && (!bool || !getSettings().get(0).asToggle().state)) {
                //if (((PlayerMoveC2SPacket) event.getPacket()).isOnGround()) event.setCancelled(true);
                boolean onGround = mc.player.fallDistance >= 0.1f;
                mc.player.setOnGround(onGround);
                FabricReflect.writeField(event.getPacket(), onGround, "field_12891", "onGround");
                bool = true;
            } else {
                bool = false;
            }
        }
    }

}