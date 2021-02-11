/*
 * This file is part of the MacHack distribution (https://github.com/BleachDrinker420/bleachhack-1.14/).
 * Copyright (c) 2019 Bleach.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package mac.hack.module.mods;

import com.google.common.eventbus.Subscribe;
import mac.hack.MacHack;
import mac.hack.event.events.EventWorldRender;
import mac.hack.module.Category;
import mac.hack.module.Module;
import mac.hack.setting.base.SettingSlider;
import mac.hack.setting.base.SettingToggle;
import mac.hack.utils.EntityUtils;
import mac.hack.utils.RenderUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.util.math.Vec3d;

public class Tracers extends Module {

    public Tracers() {
        super("Tracers", KEY_UNBOUND, Category.RENDER, "Shows lines to entities you select.",
                new SettingToggle("Players", true),
                new SettingToggle("Mobs", false),
                new SettingToggle("Animals", false),
                new SettingToggle("Items", false),
                new SettingToggle("Crystals", false),
                new SettingToggle("Vehicles", false),
                new SettingSlider("Thick: ", 0.1, 5, 1.5, 1));
    }

    @Subscribe
    public void onRender(EventWorldRender event) {
        final float thick = (float) getSettings().get(6).asSlider().getValue();

        for (Entity e : mc.world.getEntities()) {
            Vec3d vec = e.getPos();

            Vec3d vec2 = new Vec3d(0, 0, 75).rotateX(-(float) Math.toRadians(mc.cameraEntity.pitch))
                    .rotateY(-(float) Math.toRadians(mc.cameraEntity.yaw))
                    .add(mc.cameraEntity.getPos().add(0, mc.cameraEntity.getEyeHeight(mc.cameraEntity.getPose()), 0));

            if (e instanceof PlayerEntity && e != mc.player && e != mc.cameraEntity && getSettings().get(0).asToggle().state) {
                boolean friend = MacHack.friendMang.has(e.getName().asString());
                RenderUtils.drawLine(vec2.x, vec2.y, vec2.z, vec.x, vec.y, vec.z, friend ? 0.35f : 1f, friend ? 1f : 0f, friend ? 1f : 0f, thick);
                RenderUtils.drawLine(vec.x, vec.y, vec.z, vec.x, vec.y + (e.getHeight() / 1.1), vec.z, friend ? 0.35f : 1f, friend ? 1f : 0f, friend ? 1f : 0f, thick);
            } else if (e instanceof Monster && getSettings().get(1).asToggle().state) {
                RenderUtils.drawLine(vec2.x, vec2.y, vec2.z, vec.x, vec.y, vec.z, 0f, 0f, 0f, thick);
                RenderUtils.drawLine(vec.x, vec.y, vec.z, vec.x, vec.y + (e.getHeight() / 1.1), vec.z, 0f, 0f, 0f, thick);
            } else if (EntityUtils.isAnimal(e) && getSettings().get(2).asToggle().state) {
                RenderUtils.drawLine(vec2.x, vec2.y, vec2.z, vec.x, vec.y, vec.z, 0f, 1f, 0f, thick);
                RenderUtils.drawLine(vec.x, vec.y, vec.z, vec.x, vec.y + (e.getHeight() / 1.1), vec.z, 0f, 1f, 0f, thick);
            } else if (e instanceof ItemEntity && getSettings().get(3).asToggle().state) {
                RenderUtils.drawLine(vec2.x, vec2.y, vec2.z, vec.x, vec.y, vec.z, 1f, 0.7f, 0f, thick);
                RenderUtils.drawLine(vec.x, vec.y, vec.z, vec.x, vec.y + (e.getHeight() / 1.1), vec.z, 1f, 0.7f, 0f, thick);
            } else if (e instanceof EndCrystalEntity && getSettings().get(4).asToggle().state) {
                RenderUtils.drawLine(vec2.x, vec2.y, vec2.z, vec.x, vec.y, vec.z, 1f, 0f, 1f, thick);
                RenderUtils.drawLine(vec.x, vec.y, vec.z, vec.x, vec.y + (e.getHeight() / 1.1), vec.z, 1f, 0f, 1f, thick);
            } else if ((e instanceof BoatEntity || e instanceof AbstractMinecartEntity) && getSettings().get(5).asToggle().state) {
                RenderUtils.drawLine(vec2.x, vec2.y, vec2.z, vec.x, vec.y, vec.z, 0.5f, 0.5f, 0.5f, thick);
                RenderUtils.drawLine(vec.x, vec.y, vec.z, vec.x, vec.y + (e.getHeight() / 1.1), vec.z, 0.5f, 0.5f, 0.5f, thick);
            }
        }
    }
}
