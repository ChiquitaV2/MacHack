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

import java.util.ArrayList;
import java.util.List;

import com.google.common.eventbus.Subscribe;

import mac.hack.event.events.EventSendPacket;
import mac.hack.event.events.EventTick;
import mac.hack.setting.base.SettingMode;
import mac.hack.setting.base.SettingSlider;
import mac.hack.setting.base.SettingToggle;
import mac.hack.module.Category;
import mac.hack.module.Module;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class FakeLag extends Module {

	public List<PlayerMoveC2SPacket> queue = new ArrayList<>();
	public long startTime = 0;

	public FakeLag() {
		super("FakeLag", KEY_UNBOUND, Category.MOVEMENT, "Stores up movement packets",
				new SettingMode("Mode: ", "Always", "Pulse"),
				new SettingToggle("Limit", false),
				new SettingSlider("Limit: ", 0, 15, 5, 1),
				new SettingSlider("Pulse: ", 0, 5, 1, 1));
	}

	@Override
	public void onEnable() {
		startTime = System.currentTimeMillis();
		queue.clear();
		super.onEnable();
	}

	@Override
	public void onDisable() {
		sendPackets();
		super.onDisable();
	}

	@Subscribe
	public void sendPacket(EventSendPacket event) {
		if (!(event.getPacket() instanceof PlayerMoveC2SPacket
				|| event.getPacket() instanceof PlayerMoveC2SPacket.PositionOnly
				|| event.getPacket() instanceof PlayerMoveC2SPacket.LookOnly
				|| event.getPacket() instanceof PlayerMoveC2SPacket.Both)) return;
		queue.add((PlayerMoveC2SPacket) event.getPacket());
		event.setCancelled(true);
	}

	@Subscribe
	public void onTick(EventTick event) {
		if (getSettings().get(0).asMode().mode == 0) {
			if (getSettings().get(1).asToggle().state &&
					System.currentTimeMillis() - startTime > getSettings().get(2).asSlider().getValue() * 1000) setToggled(false);
		} else if (getSettings().get(0).asMode().mode == 1) {
			if (System.currentTimeMillis() - startTime > getSettings().get(3).asSlider().getValue() * 1000) {
				setToggled(false);
				setToggled(true);
			}
		}
	}

	public void sendPackets() {
		for (PlayerMoveC2SPacket p: new ArrayList<>(queue)) {
			if (p instanceof PlayerMoveC2SPacket.LookOnly) continue;
			mc.player.networkHandler.sendPacket(p);
		}
		queue.clear();
	}
}
