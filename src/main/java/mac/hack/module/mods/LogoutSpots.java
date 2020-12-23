/*
package mac.hack.module.mods;


import com.google.common.eventbus.Subscribe;
import mac.hack.MacHack;
import mac.hack.event.events.EventPlayerLeave;
import mac.hack.event.events.EventWorldRenderEntity;
import mac.hack.module.Category;
import mac.hack.module.Module;
import mac.hack.setting.base.SettingSlider;
import mac.hack.utils.MacLogger;
import mac.hack.utils.MacNotify;
import mac.hack.utils.RenderUtils;
import net.minecraft.client.render.BufferBuilderStorage;
import net.minecraft.client.render.OutlineVertexConsumerProvider;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.entity.player.PlayerEntity;

public class LogoutSpots extends Module {

    public LogoutSpots() {
        super("LogoutSpots", KEY_UNBOUND, Category.RENDER, "draws box where player logged out",
                new SettingSlider("R: ", 0.0D, 255.0D, 50.0D, 0),
                new SettingSlider("G: ", 0.0D, 255.0D, 50.0D, 0),
                new SettingSlider("B: ", 0.0D, 255.0D, 50.0D, 0)
        );
    }


    @Subscribe
    public void onPlayerLeave(EventPlayerLeave event) {

        if (this.isToggled()) {
            if (!MacHack.friendMang.has(event.getName().toString())) {
                float or = (float) (this.getSettings().get(0).asSlider().getValue() / 255.0D);
                float og = (float) (this.getSettings().get(1).asSlider().getValue() / 255.0D);
                float ob = (float) (this.getSettings().get(2).asSlider().getValue() / 255.0D);
                event. = getOutline(event.buffers, or, og, ob);
                RenderUtils.drawOutlineBox(event.entity.getBoundingBox(), or, og, ob, 1f);
            }
        }

    }

    private VertexConsumerProvider getOutline(BufferBuilderStorage buffers, float r, float g, float b) {
        OutlineVertexConsumerProvider ovsp = buffers.getOutlineVertexConsumers();
        ovsp.setColor((int) (r * 255), (int) (g * 255), (int) (b * 255), 255);
        return ovsp;
    }
    public void onPlayerLeave(final EventPlayerLeave event) {
        if (LogoutSpots.mc.world == null) {
            return;
        }
        final PlayerEntity player = LogoutSpots.mc.world.getPlayerEntityByUUID(event.getUuid());
        if (player != null && LogoutSpots.mc.player != null && !LogoutSpots.mc.player.equals((Object)player)) {
            final AxisAlignedBB bb = player.getEntityBoundingBox();
            synchronized (this.spots) {
                if (this.spots.add(new LogoutPos(event.getUuid(), player.getName(), new Vec3d(bb.maxX, bb.maxY, bb.maxZ), new Vec3d(bb.minX, bb.minY, bb.minZ), bb, player)) && this.print_message.getValue()) {

                        MacLogger.infoMessage();
                       // Command.sendRawChatMessage(ColorTextUtils.getColor(this.color.getValue()) + String.format("%s has disconnected!", player.getName()));
                }
            }
        }
    }
}

 */