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
package mac.hack.module;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import mac.hack.event.events.EventKeyPress;
import mac.hack.module.mods.*;
import org.lwjgl.glfw.GLFW;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;


import java.util.Comparator;

import com.google.common.eventbus.Subscribe;

public class ModuleManager {

	private static List<Module> mods = Arrays.asList(
			new AllahHaram(),
	//		new ACGameSense(),
			new Ambience(),
			new AntiHunger(),
			new AntiPacketKick(),
			new ArrowJuke(),
			new AutoAnvil(),
			new AutoAlign(),
			new AutoArmor(),
			new AutoDodge(),
			new AutoEat(),
			new AutoSwim(),
			new AutoTunnel(),
			new AutoDonkeyDupe(),
			new AutoReconnect(),
			new AutoRespawn(),
			new AutoBedrockBreak(),
			new AutoLog(),
		//	new AutoPiston(),
			new AutoSign(),
			new AutoSwitcher(),
			new AutoTrap(),
			new AutoTool(),
			new AutoTotem(),
			new AutoWither(),
			new AutoWalk(),
			new BetterPortal(),
			new BowBot(),
		//	new Burrow(),
			new BlockSelection(),
			new CameraClip(),
			new ChestESP(),
			new CityESP(),
			new ChunkSize(),
			new ClickGui(),
			new CleanChat(),
			new Colours(),
			new Criticals(),
			new CustomChat(),
			new CustomFOV(),
			new DiscordRPCMod(),
			new Dispenser32k(),
			new ElytraFly(),
			new EntityControl(),
			new ElytraReplace(),
			new ElytraSwap(),
			new ESP(),
			new EpearlAC(),
			new FakeLag(),
			new FakePlayer(),
			new FastUse(),
			new Flight(),
			new Freecam(),
			new Fullbright(),
			new FootXp(),
			new Ghosthand(),
			new HandProgress(),
			new HoleESP(),
			new HoleTP(),
			new PickReplace(),
			new Jesus(),
			new Killaura(),
			new MountBypass(),
			new MouseFriend(),
			new MobOwner(),
			new Nametags(),
			new Nofall(),
			new NoRender(),
			new NoSlow(),
			new Notebot(),
			new NotebotStealer(),
			new NoVelocity(),
			new Nuker(),
			new Notifications(),
			new HighwayNuker(),
			new LiquidRemover(),
			new OffhandCrash(),
			new OffHand(),
			new OldAnimations(),
			new PacketAutoCity(),
			new PacketFly(),
			new PlayerCrash(),
			new RenderAxis(),
			new SafeWalk(),
			new Scaffold(),
			new ShulkerView(),
			new ShulkerDrop(),
			new SourceESP(),
			new Speed(),
			new Spammer(),
			new SpeedMine(),
			new Sprint(),
			new StashFinder(),
			new SelfTrap(),
			new OffAxisAlarm(),
			new PortalESP(),
			new PopCounter(),
			new Peek(),
			new Step(),
			new Surround(),
			new TablistTweaks(),
			new ToggleMSGs(),
			new Timer(),
			new TunnelESP(),
			new Tracers(),
			new Trail(),
			new Trident(),
			new Trajectories(),
			new Twerk(),
			new Xray(),
			new Yaw(),
			new Zoom(),
			new HUD(),
			new AutoBuilder()

	).stream().sorted(Comparator.comparing(Module::getName, String.CASE_INSENSITIVE_ORDER)).collect(Collectors.toList());

	public static List<Module> getModules() {
		return mods;
	}

	public static Module getModule(Class<? extends Module> clazz) {
		for (Module module : mods) {
			if (module.getClass().equals(clazz)) {
				return module;
			}
		}

		return null;
	}

	public static Module getModuleByName(String name) {
		for (Module m : mods) {
			if (name.equalsIgnoreCase(m.getName()))
				return m;
		}
		return null;
	}

	public static List<Module> getModulesInCat(Category cat) {
		return mods.stream().filter(m -> m.getCategory().equals(cat)).collect(Collectors.toList());
	}

	@Subscribe
	public static void handleKeyPress(EventKeyPress eventKeyPress) {
		if (InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_KEY_F3))
			return;
		mods.stream().filter(m -> m.getKey() == eventKeyPress.getKey()).forEach(Module::toggle);
	}
}
