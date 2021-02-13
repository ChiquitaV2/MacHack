package mac.hack.module.mods;

import mac.hack.event.events.EventClientMove;
import mac.hack.event.events.EventTick;
import mac.hack.utils.EntityUtil;
import mac.hack.module.Category;
import mac.hack.module.Module;
import com.google.common.eventbus.Subscribe;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket.Mode;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.MathHelper;

public class ElytraflyKami extends Module {

    public ElytraflyKami() { super("KamiElytraFly", KEY_UNBOUND, Category.MOVEMENT, "Elytra fly bypass for ecme no way");
    }

    @Subscribe
    public void onClientMove(EventClientMove event) {
        if (mc.player.isSubmergedInWater()) {
            mc.getNetworkHandler().sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.START_FALL_FLYING));
            return;
        }

        if (mc.options.keyJump.isPressed()) {
            EntityUtil.updateVelocityY(mc.player, mc.player.getVelocity().y + 0.08);
        } else if (mc.options.keySneak.isPressed()) {
            EntityUtil.updateVelocityY(mc.player, mc.player.getVelocity().y - 0.04);
        }

        if (mc.options.keyForward.isPressed()) {
            float yaw = (float) Math.toRadians(mc.player.yaw);
            mc.player.addVelocity(MathHelper.sin(yaw) * -0.05F, 0, MathHelper.cos(yaw) * 0.05F);
        } else if (mc.options.keyBack.isPressed()) {
            float yaw = (float) Math.toRadians(mc.player.yaw);
            mc.player.addVelocity(MathHelper.sin(yaw) * 0.05F, 0, MathHelper.cos(yaw) * -0.05F);
        }
    }
}