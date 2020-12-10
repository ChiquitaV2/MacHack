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

import mac.hack.event.events.EventTick;

import com.google.common.collect.Sets;
import com.google.common.eventbus.Subscribe;

import java.util.Set;

import mac.hack.module.Category;
import mac.hack.module.Module;
import mac.hack.setting.base.SettingMode;
import mac.hack.setting.base.SettingSlider;
import mac.hack.setting.base.SettingToggle;
import mac.hack.utils.FabricReflect;
import org.lwjgl.glfw.GLFW;

import net.minecraft.item.Item;
import net.minecraft.item.Items;

public class FastUse extends Module {

	private static final Set<Item> THROWABLE = Sets.newHashSet(
			Items.SNOWBALL, Items.EGG, Items.EXPERIENCE_BOTTLE,
			Items.ENDER_EYE, Items.ENDER_PEARL, Items.SPLASH_POTION, Items.LINGERING_POTION);

	public FastUse() {
		super("FastUse", GLFW.GLFW_KEY_B, Category.PLAYER, "Allows you to use items faster",
				new SettingMode("Mode: ", "Single", "Multi"),
				new SettingSlider("Multi: ", 1, 100, 20, 0),
				new SettingToggle("Throwables Only", true),
				new SettingToggle("XP Only", false));
	}

	@Subscribe
	public void onTick(EventTick event) {
		if ((getSettings().get(2).asToggle().state && !THROWABLE.contains(mc.player.getMainHandStack().getItem()))
				|| (getSettings().get(3).asToggle().state && mc.player.getMainHandStack().getItem() != Items.EXPERIENCE_BOTTLE)) {
			return;
		}

		/* set rightClickDelay to 0 */
		FabricReflect.writeField(mc, 0, "field_1752", "itemUseCooldown");

		/* call rightClickMouse */
		if (getSettings().get(0).asMode().mode == 1 && mc.options.keyUse.isPressed()) {
			for (int i = 0; i < (int) getSettings().get(1).asSlider().getValue(); i++) {
				FabricReflect.invokeMethod(mc, "method_1583", "doItemUse");
			}
		}
	}
}
