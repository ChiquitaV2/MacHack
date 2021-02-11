package mac.hack.module.mods;

import com.google.common.collect.Streams;
import com.google.common.eventbus.Subscribe;
import mac.hack.MacHack;
import mac.hack.event.events.EventTick;
import mac.hack.module.Category;
import mac.hack.module.Module;
import mac.hack.setting.base.SettingSlider;
import mac.hack.setting.base.SettingToggle;
import mac.hack.setting.other.SettingRotate;
import mac.hack.utils.CrystalUtils;
import mac.hack.utils.EntityUtils;
import mac.hack.utils.WorldUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket.Mode;
import net.minecraft.util.Hand;

import java.util.List;
import java.util.stream.Collectors;

public class Killaura extends Module {

    int oldSlot = -1;
    int counter = 0;
    private int delay = 0;

    public Killaura() {
        super("Killaura", KEY_UNBOUND, Category.COMBAT, "Automatically attacks entities",
                new SettingToggle("Players", true),
                new SettingToggle("Mobs", false),
                new SettingToggle("Animals", false),
                new SettingToggle("Armor Stands", false),
                new SettingRotate(true),
                new SettingToggle("Thru Walls", true),
                new SettingToggle("1.9 Delay", true),
                new SettingSlider("Range", 0, 6, 4.25, 2),
                new SettingSlider("CPS", 0, 20, 8, 0),
                new SettingToggle("AutoSword", false)
        );
    }

    public void
    onDisable() {
        super.onDisable();
        if (oldSlot != -1)
            mc.player.inventory.selectedSlot = oldSlot;
        oldSlot = -1;
    }

    @Subscribe
    public void onTick(EventTick event) {
        this.oldSlot = -1; //move to on onEnable
        delay++;
        int reqDelay = (int) Math.round(20 / getSetting(8).asSlider().getValue());

        List<Entity> targets = Streams.stream(mc.world.getEntities())
                .filter(e -> (e instanceof PlayerEntity && getSetting(0).asToggle().state
                        && !MacHack.friendMang.has(e.getName().asString()))
                        || (e instanceof Monster && getSetting(1).asToggle().state)
                        || (EntityUtils.isAnimal(e) && getSetting(2).asToggle().state)
                        || (e instanceof ArmorStandEntity && getSetting(3).asToggle().state))
                .sorted((a, b) -> Float.compare(a.distanceTo(mc.player), b.distanceTo(mc.player))).collect(Collectors.toList());

        for (Entity e : targets) {
            if (mc.player.distanceTo(e) > getSetting(7).asSlider().getValue()
                    || ((LivingEntity) e).getHealth() <= 0 || e.getEntityName().equals(mc.getSession().getUsername()) || e == mc.player.getVehicle()
                    || (!mc.player.canSee(e) && !getSetting(5).asToggle().state)) continue;

            if (getSetting(4).asRotate().state) {
                WorldUtils.facePosAuto(e.getX(), e.getY() + e.getHeight() / 2, e.getZ(), getSetting(4).asRotate());
            }

            if (((delay > reqDelay || reqDelay == 0) && !getSetting(6).asToggle().state) ||
                    (mc.player.getAttackCooldownProgress(mc.getTickDelta()) == 1.0f && getSetting(6).asToggle().state)) {
                boolean wasSprinting = mc.player.isSprinting();

                if (wasSprinting)
                    mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, Mode.STOP_SPRINTING));

                mc.interactionManager.attackEntity(mc.player, e);
                mc.player.swingHand(Hand.MAIN_HAND);

                if (wasSprinting)
                    mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, Mode.START_SPRINTING));

                delay = 0;
            }
        }
        if (getSetting(9).asToggle().state && mc.player.getMainHandStack().getItem() != Items.NETHERITE_SWORD)
            oldSlot = CrystalUtils.changeHotbarSlotToItem(Items.NETHERITE_SWORD);

        if (mc.player.getMainHandStack().getItem() != Items.NETHERITE_SWORD) {
            return;
        }
    }
}
