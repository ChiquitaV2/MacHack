package mac.hack.module.mods;

import mac.hack.event.events.EventTick;
import mac.hack.event.events.EventWorldRender;
import mac.hack.module.Category;
import mac.hack.module.Module;
import mac.hack.setting.base.SettingMode;
import mac.hack.setting.base.SettingSlider;
import mac.hack.setting.base.SettingToggle;
import mac.hack.utils.ProjectileSimulator;
import mac.hack.utils.RenderUtils;
import com.google.common.eventbus.Subscribe;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.thrown.EggEntity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.entity.projectile.thrown.ExperienceBottleEntity;
import net.minecraft.entity.projectile.thrown.SnowballEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.apache.commons.lang3.tuple.Triple;

import java.util.ArrayList;
import java.util.List;

public class Trajectories extends Module {

    private final List<Triple<List<Vec3d>, Entity, BlockPos>> poses = new ArrayList<>();

    public Trajectories() {
        super("Trajectories", KEY_UNBOUND, Category.RENDER, "Shows the trajectories of projectiles",
                new SettingMode("Draw", "Line", "Dots").withDesc("How to draw the line where the projectile is going"), //0
                new SettingToggle("Throwables", true).withDesc("Shows snowballs/eggs/epearls"), //1
                new SettingToggle("XP Bottles", true).withDesc("Shows XP bottles"), //2
                new SettingToggle("Potions", true).withDesc("Shows splash/lingering potions"), //3
                new SettingToggle("Flying", true).withDesc("Shows trajectories for flying projectiles"), //4 4
                        new SettingToggle("Throwables", true).withDesc("Shows flying snowballs/eggs/epearls"),//5
                        new SettingToggle("XP Bottles", true).withDesc("Shows flying XP bottles"), //6
                        new SettingToggle("Potions", true).withDesc("Shows flying splash/lingering potions"), //7
                new SettingToggle("Other Players", false).withDesc("Show other players trajectories"), //5 8
                new SettingSlider("R: ", 0.0D, 255.0D, 255.0D, 0),//9
                new SettingSlider("G: ", 0.0D, 255.0D, 255.0D, 0),//10
                new SettingSlider("B: ", 0.0D, 255.0D, 255.0D, 0),//11
                new SettingSlider("Thick", 0.1, 5, 2, 2)); //7 12
    }

    @Subscribe
    public void onTick(EventTick event) {
        poses.clear();

        Entity entity = ProjectileSimulator.summonProjectile(
                mc.player, getSetting(1).asToggle().state, getSetting(2).asToggle().state, getSetting(3).asToggle().state);

        if (entity != null) {
            poses.add(ProjectileSimulator.simulate(entity));
        }

        if (getSetting(4).asToggle().state) {
            for (Entity e : mc.world.getEntities()) {
                if (e instanceof ProjectileEntity) {
                    if (!getSetting(5).asToggle().state
                            && (e instanceof SnowballEntity || e instanceof EggEntity || e instanceof EnderPearlEntity)) {
                        continue;
                    }

                    if (!getSetting(6).asToggle().state && e instanceof ExperienceBottleEntity) {
                        continue;
                    }

                    Triple<List<Vec3d>, Entity, BlockPos> p = ProjectileSimulator.simulate(e);

                    if (p.getLeft().size() >= 2) poses.add(p);
                }
            }
        }

        if (getSetting(8).asToggle().state) {
            for (PlayerEntity e : mc.world.getPlayers()) {
                if (e == mc.player) continue;
                Entity proj = ProjectileSimulator.summonProjectile(
                        e, getSetting(1).asToggle().state, getSetting(2).asToggle().state, getSetting(3).asToggle().state);

                if (proj != null) {
                    poses.add(ProjectileSimulator.simulate(proj));
                }
            }

        }
    }

    @Subscribe
    public void onWorldRender(EventWorldRender event) {
        float or = (float) (this.getSettings().get(9).asSlider().getValue() / 255.0D);
        float og = (float) (this.getSettings().get(10).asSlider().getValue() / 255.0D);
        float ob = (float) (this.getSettings().get(11).asSlider().getValue() / 255.0D);
        for (Triple<List<Vec3d>, Entity, BlockPos> t : poses) {
            if (t.getLeft().size() >= 2) {
                if (getSetting(0).asMode().mode == 0) {
                    for (int i = 1; i < t.getLeft().size(); i++) {
                        RenderUtils.drawLine(t.getLeft().get(i - 1).x, t.getLeft().get(i - 1).y, t.getLeft().get(i - 1).z,
                                t.getLeft().get(i).x, t.getLeft().get(i).y, t.getLeft().get(i).z, or, og, ob,
                                (float) getSetting(12).asSlider().getValue());
                    }
                } else {
                    for (Vec3d v : t.getLeft()) {
                        RenderUtils.drawFilledBox(new Box(v.x - 0.1, v.y - 0.1, v.z - 0.1, v.x + 0.1, v.y + 0.1, v.z + 0.1),
                                or, og, ob, 0.75f);
                    }
                }
            }

            if (t.getMiddle() != null) {
                RenderUtils.drawFilledBox(t.getMiddle().getBoundingBox(), or, og, ob, 0.75f);
            }

            if (t.getRight() != null) {
                RenderUtils.drawFilledBox(t.getRight(), or, og, ob, 0.75f);
            }
        }
    }
}