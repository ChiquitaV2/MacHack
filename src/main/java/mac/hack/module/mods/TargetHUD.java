package mac.hack.module.mods;

import com.google.common.eventbus.Subscribe;
import mac.hack.MacHack;
import mac.hack.event.events.EventDrawOverlay;
import mac.hack.event.events.EventReadPacket;
import mac.hack.event.events.EventTick;
import mac.hack.module.Category;
import mac.hack.module.Module;
import mac.hack.setting.base.SettingSlider;
import mac.hack.setting.base.SettingToggle;
import mac.hack.utils.ColorUtils;
import mac.hack.utils.RenderUtils;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.Objects;

import static java.lang.Math.round;

//old code don't use new code is like 100 lines not 600 lol
public class TargetHUD extends Module {
    public TargetHUD() {
        super("TargetHUD", KEY_UNBOUND, Category.CLIENT, "Shows the opps pew pew ewp",
                new SettingSlider("x", 1, 3840, 1, 0).withDesc("x coordinates"),
                new SettingSlider("y", 1, 3840, 200, 0).withDesc("y coordinates"),
                new SettingToggle("Right Align", true)
        );
    }
    private HashMap<String, Integer> pops = new HashMap<>();

    private PlayerEntity closestTarget;
    private String lastTickTargetName;

    public void
    onDisable()
    {
        super.onDisable();
        pops.clear();
    }


    @Subscribe
    public void onDrawOverlay(EventDrawOverlay event) {
        if (mc.player == null)
            return;
        PlayerEntity target = null;
        for (PlayerEntity player : mc.world.getPlayers()) {
            if (player != mc.player && !MacHack.friendMang.has(player.getDisplayName().getString()))
                if (target == null) {
                    target = player;
                } else if (mc.player.distanceTo(target) > mc.player.distanceTo(player)) {
                    target = player;
                }
        }
        if (target == null)
            return;
        int Health = Math.round(target.getHealth() + target.getAbsorptionAmount());


        int ping = Objects.requireNonNull(mc.player.networkHandler.getPlayerListEntry(target.getName().getString())).getLatency();
        String Ping = ping + "";
        int dist = (int) round(mc.player.getPos().distanceTo(target.getPos()));
        String Distance = dist + "";
        String type = "";

        if(target.getEquippedStack(EquipmentSlot.HEAD).getItem() == Items.NETHERITE_HELMET
                || target.getEquippedStack(EquipmentSlot.HEAD).getItem() == Items.DIAMOND_HELMET
                && target.getEquippedStack(EquipmentSlot.CHEST).getItem() == Items.NETHERITE_CHESTPLATE
                || target.getEquippedStack(EquipmentSlot.CHEST).getItem() == Items.DIAMOND_CHESTPLATE
                && target.getEquippedStack(EquipmentSlot.LEGS).getItem() == Items.NETHERITE_LEGGINGS
                || target.getEquippedStack(EquipmentSlot.LEGS).getItem() == Items.DIAMOND_LEGGINGS
                && target.getEquippedStack(EquipmentSlot.FEET).getItem() == Items.NETHERITE_BOOTS
                || target.getEquippedStack(EquipmentSlot.FEET).getItem() == Items.DIAMOND_BOOTS
        )
        {
            type = "§c Threat";
        }
        else if(target.getEquippedStack(EquipmentSlot.HEAD).getItem() == Items.NETHERITE_HELMET
                || target.getEquippedStack(EquipmentSlot.HEAD).getItem() == Items.DIAMOND_HELMET
                && target.getEquippedStack(EquipmentSlot.CHEST).getItem() == Items.ELYTRA
                && target.getEquippedStack(EquipmentSlot.LEGS).getItem() == Items.NETHERITE_LEGGINGS
                || target.getEquippedStack(EquipmentSlot.LEGS).getItem() == Items.DIAMOND_LEGGINGS
                && target.getEquippedStack(EquipmentSlot.FEET).getItem() == Items.NETHERITE_BOOTS
                || target.getEquippedStack(EquipmentSlot.FEET).getItem() == Items.DIAMOND_BOOTS
        )
        {
            type = "§e Wasp";
        }
        else if(target.getEquippedStack(EquipmentSlot.HEAD).getItem() == Items.AIR
                && target.getEquippedStack(EquipmentSlot.CHEST).getItem() == Items.AIR
                && target.getEquippedStack(EquipmentSlot.LEGS).getItem() == Items.AIR
                && target.getEquippedStack(EquipmentSlot.FEET).getItem() == Items.AIR
        )
        {
            type = "§a Naked";
        }
        else {
            type = "§d New Friend";
        }

        GL11.glPushMatrix();
        RenderUtils.drawRect(
                (int) getSetting(0).asSlider().getValue(),
                (int) getSetting(1).asSlider().getValue(),
                (int) getSetting(0).asSlider().getValue() + 180,
                (int) getSetting(1).asSlider().getValue() + 120,
                0x000000,
                0.3f);
        mc.textRenderer.drawWithShadow(event.matrix, target.getDisplayName(),
                (int) getSetting(0).asSlider().getValue() + 50,
                (int) getSetting(1).asSlider().getValue() + 10,
                0xffffff);
        mc.textRenderer.drawWithShadow(event.matrix, type + "§7 | " + getPingColor(ping) + ping + "ms" + "§7 |§f " + dist + "m",
                (int) getSetting(0).asSlider().getValue() + 50,
                (int) getSetting(1).asSlider().getValue() + 30,ColorUtils.textColor());
        InventoryScreen.drawEntity((int) getSetting(0).asSlider().getValue() + 20,
                (int) getSetting(1).asSlider().getValue() + 110,
                50, -(int) target.yaw, -(int) target.pitch, target);
        ItemStack mainhand = target.getMainHandStack();
        int offsetX = (int) getSetting(0).asSlider().getValue() + 50;
        int offsetY = (int) getSetting(1).asSlider().getValue() + 40;
        mc.getItemRenderer().renderGuiItemIcon(mainhand, offsetX, offsetY);
        mc.getItemRenderer().renderGuiItemOverlay(mc.textRenderer, mainhand, offsetX, offsetY);
        ItemStack head = target.getEquippedStack(EquipmentSlot.HEAD);
        int offsetX1 = (int) getSetting(0).asSlider().getValue() + 68;
        int offsetY1 = (int) getSetting(1).asSlider().getValue() + 40;
        mc.getItemRenderer().renderGuiItemIcon(head, offsetX1, offsetY1);
        mc.getItemRenderer().renderGuiItemOverlay(mc.textRenderer, head, offsetX1, offsetY1);
        ItemStack chest = target.getEquippedStack(EquipmentSlot.CHEST);
        int offsetX2 = (int) getSetting(0).asSlider().getValue() + 86;
        int offsetY2 = (int) getSetting(1).asSlider().getValue() + 40;
        mc.getItemRenderer().renderGuiItemIcon(chest, offsetX2, offsetY2);
        mc.getItemRenderer().renderGuiItemOverlay(mc.textRenderer, chest, offsetX2, offsetY2);
        ItemStack leggs = target.getEquippedStack(EquipmentSlot.LEGS);
        int offsetX3 = (int) getSetting(0).asSlider().getValue() + 104;
        int offsetY3 = (int) getSetting(1).asSlider().getValue() + 40;
        mc.getItemRenderer().renderGuiItemIcon(leggs, offsetX3, offsetY3);
        mc.getItemRenderer().renderGuiItemOverlay(mc.textRenderer, leggs, offsetX3, offsetY3);
        ItemStack feet = target.getEquippedStack(EquipmentSlot.FEET);
        int offsetX4 = (int) getSetting(0).asSlider().getValue() + 122;
        int offsetY4 = (int) getSetting(1).asSlider().getValue() + 40;
        mc.getItemRenderer().renderGuiItemIcon(feet, offsetX4, offsetY4);
        mc.getItemRenderer().renderGuiItemOverlay(mc.textRenderer, feet, offsetX4, offsetY4);
        ItemStack offhand = target.getOffHandStack();
        int offsetX5 = (int) getSetting(0).asSlider().getValue() + 140;
        int offsetY5 = (int) getSetting(1).asSlider().getValue() + 40;
        mc.getItemRenderer().renderGuiItemIcon(offhand, offsetX5, offsetY5);
        mc.getItemRenderer().renderGuiItemOverlay(mc.textRenderer, offhand, offsetX5, offsetY5);
        String poped = "\u00a77Pops\u00a77: \u00a7r" + pops.get(target.getEntityName());
        mc.textRenderer.drawWithShadow(event.matrix, poped,
                (int) getSetting(0).asSlider().getValue() + 50,
                (int) getSetting(1).asSlider().getValue() + 100,
                ColorUtils.textColor());
        mc.getItemRenderer().zOffset = 0.0F;

        GL11.glPopMatrix();

        GL11.glPushMatrix();
        if (Health == 20) {
            mc.textRenderer.drawWithShadow(event.matrix, "20",
                    (int) getSetting(0).asSlider().getValue() + 160,
                    (int) getSetting(1).asSlider().getValue() + 100,
                    0xb055ff55);
            RenderUtils.drawRect(
                    (int) getSetting(0).asSlider().getValue() + 160,
                    (int) getSetting(1).asSlider().getValue() + 20,
                    (int) getSetting(0).asSlider().getValue() + 170,
                    (int) getSetting(1).asSlider().getValue() + 90,
                    0xb055ff55, 1.0F);
        }
        if (Health == 19) {
            mc.textRenderer.drawWithShadow(event.matrix, "19",
                    (int) getSetting(0).asSlider().getValue() + 160,
                    (int) getSetting(1).asSlider().getValue() + 100,
                    0xff55ff55);
            RenderUtils.drawRect(
                    (int) getSetting(0).asSlider().getValue() + 160,
                    (int) getSetting(1).asSlider().getValue() + 25,
                    (int) getSetting(0).asSlider().getValue() + 170,
                    (int) getSetting(1).asSlider().getValue() + 90,
                    0xb055ff55, 1.0F);
        }
        if (Health == 18) {
            mc.textRenderer.drawWithShadow(event.matrix, "18",
                    (int) getSetting(0).asSlider().getValue() + 160,
                    (int) getSetting(1).asSlider().getValue() + 100,
                    0xff00aa00);
            RenderUtils.drawRect(
                    (int) getSetting(0).asSlider().getValue() + 160,
                    (int) getSetting(1).asSlider().getValue() + 30,
                    (int) getSetting(0).asSlider().getValue() + 170,
                    (int) getSetting(1).asSlider().getValue() + 90,
                    0xb000aa00, 1.0F);
        }
        if (Health == 17) {
            mc.textRenderer.drawWithShadow(event.matrix, "17",
                    (int) getSetting(0).asSlider().getValue() + 160,
                    (int) getSetting(1).asSlider().getValue() + 100,
                    0xff00aa00);
            RenderUtils.drawRect(
                    (int) getSetting(0).asSlider().getValue() + 160,
                    (int) getSetting(1).asSlider().getValue() + 32,
                    (int) getSetting(0).asSlider().getValue() + 170,
                    (int) getSetting(1).asSlider().getValue() + 90,
                    0xb000aa00, 1.0F);
        }
        if (Health == 16) {
            mc.textRenderer.drawWithShadow(event.matrix, "16",
                    (int) getSetting(0).asSlider().getValue() + 160,
                    (int) getSetting(1).asSlider().getValue() + 100,
                    0xffffff55);
            RenderUtils.drawRect(
                    (int) getSetting(0).asSlider().getValue() + 160,
                    (int) getSetting(1).asSlider().getValue() + 36,
                    (int) getSetting(0).asSlider().getValue() + 170,
                    (int) getSetting(1).asSlider().getValue() + 90,
                    0xb0ffff55, 1.0F);
        }
        if (Health == 15) {
            mc.textRenderer.drawWithShadow(event.matrix, "15",
                    (int) getSetting(0).asSlider().getValue() + 160,
                    (int) getSetting(1).asSlider().getValue() + 100,
                    0xffffff55);
            RenderUtils.drawRect(
                    (int) getSetting(0).asSlider().getValue() + 160,
                    (int) getSetting(1).asSlider().getValue() + 40,
                    (int) getSetting(0).asSlider().getValue() + 170,
                    (int) getSetting(1).asSlider().getValue() + 90,
                    0xb0ffff55, 1.0F);
        }
        if (Health == 14) {
            mc.textRenderer.drawWithShadow(event.matrix, "14",
                    (int) getSetting(0).asSlider().getValue() + 160,
                    (int) getSetting(1).asSlider().getValue() + 100,
                    0xffffff55);
            RenderUtils.drawRect(
                    (int) getSetting(0).asSlider().getValue() + 160,
                    (int) getSetting(1).asSlider().getValue() + 43,
                    (int) getSetting(0).asSlider().getValue() + 170,
                    (int) getSetting(1).asSlider().getValue() + 90,
                    0xb0ffff55, 1.0F);
        }
        if (Health == 13) {
            mc.textRenderer.drawWithShadow(event.matrix, "13",
                    (int) getSetting(0).asSlider().getValue() + 160,
                    (int) getSetting(1).asSlider().getValue() + 100,
                    0xffffff55);
            RenderUtils.drawRect(
                    (int) getSetting(0).asSlider().getValue() + 160,
                    (int) getSetting(1).asSlider().getValue() + 48,
                    (int) getSetting(0).asSlider().getValue() + 170,
                    (int) getSetting(1).asSlider().getValue() + 90,
                    0xb0ffff55, 1.0F);
        }
        if (Health == 12) {
            mc.textRenderer.drawWithShadow(event.matrix, "12",
                    (int) getSetting(0).asSlider().getValue() + 160,
                    (int) getSetting(1).asSlider().getValue() + 100,
                    0xffffaa00);
            RenderUtils.drawRect(
                    (int) getSetting(0).asSlider().getValue() + 160,
                    (int) getSetting(1).asSlider().getValue() + 50,
                    (int) getSetting(0).asSlider().getValue() + 170,
                    (int) getSetting(1).asSlider().getValue() + 90,
                    0xb0ffaa00, 1.0F);
        }
        if (Health == 11) {
            mc.textRenderer.drawWithShadow(event.matrix, "11",
                    (int) getSetting(0).asSlider().getValue() + 160,
                    (int) getSetting(1).asSlider().getValue() + 100,
                    0xffffaa00);
            RenderUtils.drawRect(
                    (int) getSetting(0).asSlider().getValue() + 160,
                    (int) getSetting(1).asSlider().getValue() + 54,
                    (int) getSetting(0).asSlider().getValue() + 170,
                    (int) getSetting(1).asSlider().getValue() + 90,
                    0xb0ffaa00, 1.0F);
        }
        if (Health == 10) {
            mc.textRenderer.drawWithShadow(event.matrix, "10",
                    (int) getSetting(0).asSlider().getValue() + 160,
                    (int) getSetting(1).asSlider().getValue() + 100,
                    0xffffaa00);
            RenderUtils.drawRect(
                    (int) getSetting(0).asSlider().getValue() + 160,
                    (int) getSetting(1).asSlider().getValue() + 56,
                    (int) getSetting(0).asSlider().getValue() + 170,
                    (int) getSetting(1).asSlider().getValue() + 90,
                    0xb0ffaa00, 1.0F);
        }
        if (Health == 9) {
            mc.textRenderer.drawWithShadow(event.matrix, "9",
                    (int) getSetting(0).asSlider().getValue() + 160,
                    (int) getSetting(1).asSlider().getValue() + 100,
                    0xffffaa00);
            RenderUtils.drawRect(
                    (int) getSetting(0).asSlider().getValue() + 160,
                    (int) getSetting(1).asSlider().getValue() + 60,
                    (int) getSetting(0).asSlider().getValue() + 170,
                    (int) getSetting(1).asSlider().getValue() + 90,
                    0xb0ffaa00, 1.0F);
        }
        if (Health == 8) {
            mc.textRenderer.drawWithShadow(event.matrix, "8",
                    (int) getSetting(0).asSlider().getValue() + 160,
                    (int) getSetting(1).asSlider().getValue() + 100,
                    0xffff5555);
            RenderUtils.drawRect(
                    (int) getSetting(0).asSlider().getValue() + 160,
                    (int) getSetting(1).asSlider().getValue() + 64,
                    (int) getSetting(0).asSlider().getValue() + 170,
                    (int) getSetting(1).asSlider().getValue() + 90,
                    0xb0ff5555, 1.0F);
        }
        if (Health == 7) {
            mc.textRenderer.drawWithShadow(event.matrix, "7",
                    (int) getSetting(0).asSlider().getValue() + 160,
                    (int) getSetting(1).asSlider().getValue() + 100,
                    0xffff5555);
            RenderUtils.drawRect(
                    (int) getSetting(0).asSlider().getValue() + 160,
                    (int) getSetting(1).asSlider().getValue() + 68,
                    (int) getSetting(0).asSlider().getValue() + 170,
                    (int) getSetting(1).asSlider().getValue() + 90,
                    0xb0ff5555, 1.0F);
        }
        if (Health == 6) {
            mc.textRenderer.drawWithShadow(event.matrix, "6",
                    (int) getSetting(0).asSlider().getValue() + 160,
                    (int) getSetting(1).asSlider().getValue() + 100,
                    0xffff5555);
            RenderUtils.drawRect(
                    (int) getSetting(0).asSlider().getValue() + 160,
                    (int) getSetting(1).asSlider().getValue() + 70,
                    (int) getSetting(0).asSlider().getValue() + 170,
                    (int) getSetting(1).asSlider().getValue() + 90,
                    0xb0ff5555, 1.0F);
        }
        if (Health == 5) {
            mc.textRenderer.drawWithShadow(event.matrix, "5",
                    (int) getSetting(0).asSlider().getValue() + 160,
                    (int) getSetting(1).asSlider().getValue() + 100,
                    0xffaa0000);
            RenderUtils.drawRect(
                    (int) getSetting(0).asSlider().getValue() + 160,
                    (int) getSetting(1).asSlider().getValue() + 72,
                    (int) getSetting(0).asSlider().getValue() + 170,
                    (int) getSetting(1).asSlider().getValue() + 90,
                    0xb0aa0000, 1.0F);
        }
        if (Health == 4) {
            mc.textRenderer.drawWithShadow(event.matrix, "4",
                    (int) getSetting(0).asSlider().getValue() + 160,
                    (int) getSetting(1).asSlider().getValue() + 100,
                    0xffaa0000);
            RenderUtils.drawRect(
                    (int) getSetting(0).asSlider().getValue() + 160,
                    (int) getSetting(1).asSlider().getValue() + 76,
                    (int) getSetting(0).asSlider().getValue() + 170,
                    (int) getSetting(1).asSlider().getValue() + 90,
                    0xb0aa0000, 1.0F);
        }
        if (Health == 3) {
            mc.textRenderer.drawWithShadow(event.matrix, "3",
                    (int) getSetting(0).asSlider().getValue() + 160,
                    (int) getSetting(1).asSlider().getValue() + 100,
                    0xffaa0000);
            RenderUtils.drawRect(
                    (int) getSetting(0).asSlider().getValue() + 160,
                    (int) getSetting(1).asSlider().getValue() + 80,
                    (int) getSetting(0).asSlider().getValue() + 170,
                    (int) getSetting(1).asSlider().getValue() + 90,
                    0xb0aa0000, 1.0F);
        }
        if (Health == 2) {
            mc.textRenderer.drawWithShadow(event.matrix, "2",
                    (int) getSetting(0).asSlider().getValue() + 160,
                    (int) getSetting(1).asSlider().getValue() + 100,
                    0xffaa0000);
            RenderUtils.drawRect(
                    (int) getSetting(0).asSlider().getValue() + 160,
                    (int) getSetting(1).asSlider().getValue() + 84,
                    (int) getSetting(0).asSlider().getValue() + 170,
                    (int) getSetting(1).asSlider().getValue() + 90,
                    0xb0aa0000, 1.0F);
        }
        if (Health == 1) {
            mc.textRenderer.drawWithShadow(event.matrix, "1",
                    (int) getSetting(0).asSlider().getValue() + 160,
                    (int) getSetting(1).asSlider().getValue() + 100,
                    0xffaa0000);
            RenderUtils.drawRect(
                    (int) getSetting(0).asSlider().getValue() + 160,
                    (int) getSetting(1).asSlider().getValue() + 88,
                    (int) getSetting(0).asSlider().getValue() + 170,
                    (int) getSetting(1).asSlider().getValue() + 90,
                    0xb0aa0000, 1.0F);
        }
        if (Health > 20) {
            RenderUtils.drawRect(
                    (int) getSetting(0).asSlider().getValue() + 160,
                    (int) getSetting(1).asSlider().getValue() + 20,
                    (int) getSetting(0).asSlider().getValue() + 170,
                    (int) getSetting(1).asSlider().getValue() + 90,
                    0xb055ff55, 1.0F);
            RenderUtils.drawRect((int)getSetting(0).asSlider().getValue() + 160, (int) getSetting(1).asSlider().getValue() + 90,
                    (int)getSetting(0).asSlider().getValue() + 180, (int) getSetting(1).asSlider().getValue() + 20, 0xb055ff55, 0.5f);
            if (Health == 21) {
                mc.textRenderer.drawWithShadow(event.matrix, "21",
                        (int) getSetting(0).asSlider().getValue() + 160,
                        (int) getSetting(1).asSlider().getValue() + 100,
                        0xff55ffff);
                RenderUtils.drawRect(
                        (int) getSetting(0).asSlider().getValue() + 160,
                        (int) getSetting(1).asSlider().getValue() + 88,
                        (int) getSetting(0).asSlider().getValue() + 170,
                        (int) getSetting(1).asSlider().getValue() + 90,
                        0xb055ffff, 1.0F);
            }
            if (Health == 22) {
                mc.textRenderer.drawWithShadow(event.matrix, "22",
                        (int) getSetting(0).asSlider().getValue() + 160,
                        (int) getSetting(1).asSlider().getValue() + 100,
                        0xff55ffff);
                RenderUtils.drawRect(
                        (int) getSetting(0).asSlider().getValue() + 160,
                        (int) getSetting(1).asSlider().getValue() + 84,
                        (int) getSetting(0).asSlider().getValue() + 170,
                        (int) getSetting(1).asSlider().getValue() + 90,
                        0xb055ffff, 1.0F);
            }
            if (Health == 23) {
                mc.textRenderer.drawWithShadow(event.matrix, "23",
                        (int) getSetting(0).asSlider().getValue() + 160,
                        (int) getSetting(1).asSlider().getValue() + 100,
                        0xff55ffff);
                RenderUtils.drawRect(
                        (int) getSetting(0).asSlider().getValue() + 160,
                        (int) getSetting(1).asSlider().getValue() + 80,
                        (int) getSetting(0).asSlider().getValue() + 170,
                        (int) getSetting(1).asSlider().getValue() + 90,
                        0xb055ffff, 1.0F);
            }
            if (Health == 24) {
                mc.textRenderer.drawWithShadow(event.matrix, "24",
                        (int) getSetting(0).asSlider().getValue() + 160,
                        (int) getSetting(1).asSlider().getValue() + 100,
                        0xff55ffff);
                RenderUtils.drawRect(
                        (int) getSetting(0).asSlider().getValue() + 160,
                        (int) getSetting(1).asSlider().getValue() + 76,
                        (int) getSetting(0).asSlider().getValue() + 170,
                        (int) getSetting(1).asSlider().getValue() + 90,
                        0xb055ffff, 1.0F);
            }
            if (Health == 25) {
                mc.textRenderer.drawWithShadow(event.matrix, "25",
                        (int) getSetting(0).asSlider().getValue() + 160,
                        (int) getSetting(1).asSlider().getValue() + 100,
                        0xff55ffff);
                RenderUtils.drawRect(
                        (int) getSetting(0).asSlider().getValue() + 160,
                        (int) getSetting(1).asSlider().getValue() + 72,
                        (int) getSetting(0).asSlider().getValue() + 170,
                        (int) getSetting(1).asSlider().getValue() + 90,
                        0xb055ffff, 1.0F);
            }
            if (Health == 26) {
                mc.textRenderer.drawWithShadow(event.matrix, "26",
                        (int) getSetting(0).asSlider().getValue() + 160,
                        (int) getSetting(1).asSlider().getValue() + 100,
                        0xff55ffff);
                RenderUtils.drawRect(
                        (int) getSetting(0).asSlider().getValue() + 160,
                        (int) getSetting(1).asSlider().getValue() + 68,
                        (int) getSetting(0).asSlider().getValue() + 170,
                        (int) getSetting(1).asSlider().getValue() + 90,
                        0xb055ffff, 1.0F);
            }
            if (Health == 27) {
                mc.textRenderer.drawWithShadow(event.matrix, "27",
                        (int) getSetting(0).asSlider().getValue() + 160,
                        (int) getSetting(1).asSlider().getValue() + 100,
                        0xff55ffff);
                RenderUtils.drawRect(
                        (int) getSetting(0).asSlider().getValue() + 160,
                        (int) getSetting(1).asSlider().getValue() + 64,
                        (int) getSetting(0).asSlider().getValue() + 170,
                        (int) getSetting(1).asSlider().getValue() + 90,
                        0xb055ffff, 1.0F);
            }
            if (Health == 28) {
                mc.textRenderer.drawWithShadow(event.matrix, "28",
                        (int) getSetting(0).asSlider().getValue() + 160,
                        (int) getSetting(1).asSlider().getValue() + 100,
                        0xff55ffff);
                RenderUtils.drawRect(
                        (int) getSetting(0).asSlider().getValue() + 160,
                        (int) getSetting(1).asSlider().getValue() + 60,
                        (int) getSetting(0).asSlider().getValue() + 170,
                        (int) getSetting(1).asSlider().getValue() + 90,
                        0xb055ffff, 1.0F);
            }
            if (Health == 29) {
                mc.textRenderer.drawWithShadow(event.matrix, "29",
                        (int) getSetting(0).asSlider().getValue() + 160,
                        (int) getSetting(1).asSlider().getValue() + 100,
                        0xff55ffff);
                RenderUtils.drawRect(
                        (int) getSetting(0).asSlider().getValue() + 160,
                        (int) getSetting(1).asSlider().getValue() + 56,
                        (int) getSetting(0).asSlider().getValue() + 170,
                        (int) getSetting(1).asSlider().getValue() + 90,
                        0xb055ffff, 1.0F);
            }
            if (Health == 30) {
                mc.textRenderer.drawWithShadow(event.matrix, "30",
                        (int) getSetting(0).asSlider().getValue() + 160,
                        (int) getSetting(1).asSlider().getValue() + 100,
                        0xff55ffff);
                RenderUtils.drawRect(
                        (int) getSetting(0).asSlider().getValue() + 160,
                        (int) getSetting(1).asSlider().getValue() + 52,
                        (int) getSetting(0).asSlider().getValue() + 170,
                        (int) getSetting(1).asSlider().getValue() + 90,
                        0xb055ffff, 1.0F);
            }
            if (Health == 31) {
                mc.textRenderer.drawWithShadow(event.matrix, "31",
                        (int) getSetting(0).asSlider().getValue() + 160,
                        (int) getSetting(1).asSlider().getValue() + 100,
                        0xff55ffff);
                RenderUtils.drawRect(
                        (int) getSetting(0).asSlider().getValue() + 160,
                        (int) getSetting(1).asSlider().getValue() + 48,
                        (int) getSetting(0).asSlider().getValue() + 170,
                        (int) getSetting(1).asSlider().getValue() + 90,
                        0xb055ffff, 1.0F);
            }
            if (Health == 32) {
                mc.textRenderer.drawWithShadow(event.matrix, "32",
                        (int) getSetting(0).asSlider().getValue() + 160,
                        (int) getSetting(1).asSlider().getValue() + 100,
                        0xff55ffff);
                RenderUtils.drawRect(
                        (int) getSetting(0).asSlider().getValue() + 160,
                        (int) getSetting(1).asSlider().getValue() + 44,
                        (int) getSetting(0).asSlider().getValue() + 170,
                        (int) getSetting(1).asSlider().getValue() + 90,
                        0xb055ffff, 1.0F);
            }
            if (Health == 33) {
                mc.textRenderer.drawWithShadow(event.matrix, "33",
                        (int) getSetting(0).asSlider().getValue() + 160,
                        (int) getSetting(1).asSlider().getValue() + 100,
                        0xff55ffff);
                RenderUtils.drawRect(
                        (int) getSetting(0).asSlider().getValue() + 160,
                        (int) getSetting(1).asSlider().getValue() + 40,
                        (int) getSetting(0).asSlider().getValue() + 170,
                        (int) getSetting(1).asSlider().getValue() + 90,
                        0xb055ffff, 1.0F);
            }
            if (Health == 34) {
                mc.textRenderer.drawWithShadow(event.matrix, "34",
                        (int) getSetting(0).asSlider().getValue() + 160,
                        (int) getSetting(1).asSlider().getValue() + 100,
                        0xff55ffff);
                RenderUtils.drawRect(
                        (int) getSetting(0).asSlider().getValue() + 160,
                        (int) getSetting(1).asSlider().getValue() + 36,
                        (int) getSetting(0).asSlider().getValue() + 170,
                        (int) getSetting(1).asSlider().getValue() + 90,
                        0xb055ffff, 1.0F);
            }
            if (Health == 35) {
                mc.textRenderer.drawWithShadow(event.matrix, "35",
                        (int) getSetting(0).asSlider().getValue() + 160,
                        (int) getSetting(1).asSlider().getValue() + 100,
                        0xff55ffff);
                RenderUtils.drawRect(
                        (int) getSetting(0).asSlider().getValue() + 160,
                        (int) getSetting(1).asSlider().getValue() + 32,
                        (int) getSetting(0).asSlider().getValue() + 170,
                        (int) getSetting(1).asSlider().getValue() + 90,
                        0xb055ffff, 1.0F);
            }
            if (Health == 36) {
                mc.textRenderer.drawWithShadow(event.matrix, "36",
                        (int) getSetting(0).asSlider().getValue() + 160,
                        (int) getSetting(1).asSlider().getValue() + 100,
                        0xff55ffff);
                RenderUtils.drawRect(
                        (int) getSetting(0).asSlider().getValue() + 160,
                        (int) getSetting(1).asSlider().getValue() + 28,
                        (int) getSetting(0).asSlider().getValue() + 170,
                        (int) getSetting(1).asSlider().getValue() + 90,
                        0xb055ffff, 1.0F);
            }
        }
        GL11.glPopMatrix();
    }

    public static boolean isLiving(Entity e) {
        return e instanceof LivingEntity;
    }

    // I'm lazy so this is just pop counter line for line basically
    @Subscribe
    public void
    onReadPacket(EventReadPacket event)
    {
        if(event.getPacket() instanceof EntityStatusS2CPacket)
        {
            EntityStatusS2CPacket pack = (EntityStatusS2CPacket) event.getPacket();

            if(pack.getStatus() == 35)
            {
                handlePop(pack.getEntity(mc.world));
            }
        }
    }

    @Subscribe
    public void
    onTick(EventTick tick)
    {
        if(mc.world == null)
            return;

        mc.world.getPlayers().forEach(player -> {
            if(player.getHealth() <= 0)
            {
                if(pops.containsKey(player.getEntityName()))
                {
                    pops.remove(player.getEntityName(), pops.get(player.getEntityName()));
                }
            }
        });
    }
    private void
    handlePop(Entity entity)
    {
        if(pops == null)
            pops = new HashMap<>();

        if(entity == mc.player)
            return;

        if(pops.get(entity.getEntityName()) == null)
        {
            pops.put(entity.getEntityName(), 1);
        }
        else if(!(pops.get(entity.getEntityName()) == null))
        {
            int popc = pops.get(entity.getEntityName());
            popc += 1;
            pops.put(entity.getEntityName(), popc);
        }
    }

    private String getPingColor(int ping) {
        if (ping < 100) {
            return "\u00a7a";
        } else if (ping < 150) {
            return "\u00a7c";
        } else {
            return "\u00a74";
        }
    }

}
