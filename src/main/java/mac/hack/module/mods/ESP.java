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

import mac.hack.MacHack;
import mac.hack.event.events.EventTick;
import mac.hack.setting.base.SettingToggle;
import mac.hack.module.Category;
import mac.hack.module.Module;
import mac.hack.utils.EntityUtils;
import com.google.common.eventbus.Subscribe;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.util.Formatting;

public class ESP extends Module {

	public ESP() {
		super("ESP", KEY_UNBOUND, Category.RENDER, "Allows you to see entities though walls.",
				new SettingToggle("Players", true).withDesc("Show Players"),
				new SettingToggle("Mobs", false).withDesc("Show Mobs"),
				new SettingToggle("Animals", false).withDesc("Show Animals"),
				new SettingToggle("Items", true).withDesc("Show Items"),
				new SettingToggle("Crystals", true).withDesc("Show End Crystals"),
				new SettingToggle("Vehicles", false).withDesc("Show Vehicles"));
	}

	@Override
	public void onDisable() {
		super.onDisable();
		for (Entity e: mc.world.getEntities()) {
			if (e != mc.player) {
				if (e.isGlowing()) e.setGlowing(false);
			}
		}
	}

	@Subscribe
	public void onTick(EventTick event) {
		for (Entity e: mc.world.getEntities()) {
			if (e instanceof PlayerEntity && e != mc.player && getSettings().get(0).asToggle().state) {
				if (MacHack.friendMang.has(e.getName().asString())) {
					EntityUtils.setGlowing(e, Formatting.AQUA, "friends");
				} else {
					EntityUtils.setGlowing(e, Formatting.RED, "players");
				}
			}

			else if (e instanceof Monster && getSettings().get(1).asToggle().state) {
				EntityUtils.setGlowing(e, Formatting.DARK_BLUE, "mobs");
			}

			else if (EntityUtils.isAnimal(e) && getSettings().get(2).asToggle().state) {
				EntityUtils.setGlowing(e, Formatting.GREEN, "passive");
			}

			else if (e instanceof ItemEntity && getSettings().get(3).asToggle().state) {
				EntityUtils.setGlowing(e, Formatting.GOLD, "items");
			}

			else if (e instanceof EndCrystalEntity && getSettings().get(4).asToggle().state) {
				EntityUtils.setGlowing(e, Formatting.LIGHT_PURPLE, "crystals");
			}

			else if ((e instanceof BoatEntity || e instanceof AbstractMinecartEntity) && getSettings().get(5).asToggle().state) {
				EntityUtils.setGlowing(e, Formatting.GRAY, "vehicles");
			}
			else {
				e.setGlowing(false);
			}
		}
	}
}
