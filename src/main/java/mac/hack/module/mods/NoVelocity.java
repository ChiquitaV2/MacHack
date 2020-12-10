package mac.hack.module.mods;
import mac.hack.event.events.EventReadPacket;
import mac.hack.event.events.EventTick;
import mac.hack.module.Category;
import mac.hack.module.Module;
import mac.hack.setting.base.SettingSlider;
import mac.hack.setting.base.SettingToggle;
import mac.hack.utils.FabricReflect;
import com.google.common.eventbus.Subscribe;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
/**
 * @author sl First Module utilizing EventBus!
 */
public class NoVelocity extends Module {
	public NoVelocity() {
		super("NoVelocity", KEY_UNBOUND, Category.PLAYER, "If you take some damage, you don't move.",
				new SettingToggle("Knockback", true).withDesc("Reduces knockback from other entites").withChildren(
						new SettingSlider("VelXZ", 0, 100, 0, 1).withDesc("How much horizontal velocity"),
						new SettingSlider("VelY", 0, 100, 0, 1).withDesc("How much vertical velocity")),
				new SettingToggle("Explosions", true).withDesc("Reduces explosion velocity").withChildren(
						new SettingSlider("VelXZ", 0, 100, 0, 1).withDesc("How much horizontal velocity"),
						new SettingSlider("VelY", 0, 100, 0, 1).withDesc("How much vertical velocity")),
				new SettingToggle("Pushing", true).withDesc("Reduces how much you get pushed by entites").withChildren(
						new SettingSlider("Amount", 0, 100, 0, 1).withDesc("How much to reduce pushing")),
				new SettingToggle("Fluids", true).withDesc("Reduces how much you get pushed from fluids"));
	}
	public void onDisable() {
		mc.player.pushSpeedReduction = 0f;
		super.onDisable();
	}
	@Subscribe
	public void onTick(EventTick event) {
		if (getSetting(2).asToggle().state) {
			mc.player.pushSpeedReduction = (float) (1 - getSetting(2).asToggle().getChild(0).asSlider().getValue() / 100);
		}
	}
	@Subscribe
	public void readPacket(EventReadPacket event) {
		if (mc.player == null)
			return;
		if (event.getPacket() instanceof EntityVelocityUpdateS2CPacket && getSetting(0).asToggle().state) {
			EntityVelocityUpdateS2CPacket packet = (EntityVelocityUpdateS2CPacket) event.getPacket();
			if (packet.getId() == mc.player.getEntityId()) {
				double velXZ = getSetting(0).asToggle().getChild(0).asSlider().getValue() / 100;
				double velY = getSetting(0).asToggle().getChild(1).asSlider().getValue() / 100;
				FabricReflect.writeField(packet, (int) (packet.getVelocityX() * velXZ), "field_12563", "velocityX");
				FabricReflect.writeField(packet, (int) (packet.getVelocityY() * velY), "field_12562", "velocityY");
				FabricReflect.writeField(packet, (int) (packet.getVelocityZ() * velXZ), "field_12561", "velocityZ");
			}
		} else if (event.getPacket() instanceof ExplosionS2CPacket && getSetting(1).asToggle().state) {
			ExplosionS2CPacket packet = (ExplosionS2CPacket) event.getPacket();
			double velXZ = getSetting(1).asToggle().getChild(0).asSlider().getValue() / 100;
			double velY = getSetting(1).asToggle().getChild(1).asSlider().getValue() / 100;
			FabricReflect.writeField(event.getPacket(), (float) (packet.getPlayerVelocityX() * velXZ), "field_12176", "playerVelocityX");
			FabricReflect.writeField(event.getPacket(), (float) (packet.getPlayerVelocityY() * velY), "field_12182", "playerVelocityY");
			FabricReflect.writeField(event.getPacket(), (float) (packet.getPlayerVelocityZ() * velXZ), "field_12183", "playerVelocityZ");
		}
	}
// Fluid handling in MixinFlowableFluid.getVelocity_hasNext()
}