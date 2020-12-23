package mac.hack.module.mods;

import com.google.common.eventbus.Subscribe;
import mac.hack.event.events.EventTick;
import mac.hack.module.Category;
import mac.hack.module.Module;
import mac.hack.module.ModuleManager;
import mac.hack.setting.base.SettingToggle;
import mac.hack.utils.EntityUtils;
import mac.hack.utils.MacLogger;
import mac.hack.utils.Timer;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.network.packet.s2c.play.DisconnectS2CPacket;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.LiteralText;

public class OffAxisAlarm extends Module {
    public OffAxisAlarm() {
        super("OffAxisAlarm", KEY_UNBOUND, Category.PLAYER, "Warns you if you're off axis.",
                new SettingToggle("LogOut", false));
    }

    Timer chatTimer = new Timer();
    Timer timer = new Timer();
    Timer logTimer = new Timer();

    @Subscribe
    public void onTick(EventTick event) {
        if (timer.passed(1000)) {
            switch (EntityUtils.determineHighway()) {
                case 1:
                    if (!(mc.player.getZ() > 0 && mc.player.getZ() < 1)) {
                        mc.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.ENTITY_ENDERMAN_DEATH, 1.0F));
                        if (chatTimer.passed(5000)) {
                            MacLogger.errorMessage("You're off axis!");
                            chatTimer.reset();
                        }
                        if (logTimer.passed(15000) && getSetting(0).asToggle().state) {
                            mc.player.networkHandler.onDisconnect(new DisconnectS2CPacket(new LiteralText("Your were off axis")));
                            logTimer.reset();
                        }
                        timer.reset();
                    }
                    break;
                case 4:
                    if (!(mc.player.getZ() < 0 && mc.player.getZ() > -1)) {
                        mc.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.ENTITY_ENDERMAN_DEATH, 1.0F));
                        if (chatTimer.passed(5000)) {
                            MacLogger.errorMessage("You're off axis!");
                            chatTimer.reset();
                        }
                        if (logTimer.passed(15000) && getSetting(0).asToggle().state) {
                            mc.player.networkHandler.onDisconnect(new DisconnectS2CPacket(new LiteralText("Your were off axis")));
                            logTimer.reset();
                        }
                        timer.reset();
                    }
                    break;
                case 7:
                    if (!(mc.player.getX() < 0 && mc.player.getX() > -1)) {
                        mc.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.ENTITY_ENDERMAN_DEATH, 1.0F));
                        if (chatTimer.passed(5000)) {
                            MacLogger.errorMessage("You're off axis!");
                            chatTimer.reset();
                        }
                        if (logTimer.passed(15000) && getSetting(0).asToggle().state) {
                            mc.player.networkHandler.onDisconnect(new DisconnectS2CPacket(new LiteralText("Your were off axis")));
                            logTimer.reset();
                        }
                        timer.reset();
                    }
                    break;
                case 8:
                    if (!(mc.player.getX() > 0 && mc.player.getX() < 1)) {
                        mc.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.ENTITY_ENDERMAN_DEATH, 1.0F));
                        if (chatTimer.passed(5000)) {
                            MacLogger.errorMessage("You're off axis!");
                            chatTimer.reset();
                        }
                        if (logTimer.passed(15000) && getSetting(0).asToggle().state) {
                            mc.player.networkHandler.onDisconnect(new DisconnectS2CPacket(new LiteralText("Your were off axis")));
                            logTimer.reset();
                        }
                        timer.reset();
                    }
                    break;
            }
        }
    }
}
