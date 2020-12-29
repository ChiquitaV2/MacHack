package mac.hack.module.mods;

import mac.hack.MacHack;
import mac.hack.event.events.EventDrawOverlay;
import mac.hack.event.events.EventReadPacket;
import mac.hack.event.events.EventTick;
import mac.hack.module.Category;
import mac.hack.module.Module;
import mac.hack.module.ModuleManager;
import mac.hack.setting.base.SettingMode;
import mac.hack.setting.base.SettingSlider;
import mac.hack.setting.base.SettingToggle;
import mac.hack.utils.*;
import com.google.common.eventbus.Subscribe;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.shorts.ShortList;
import it.unimi.dsi.fastutil.shorts.ShortListIterator;
import net.minecraft.SharedConstants;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.*;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import net.minecraft.server.world.SimpleTickScheduler;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.*;
import net.minecraft.world.ChunkTickScheduler;
import net.minecraft.world.Heightmap;
import net.minecraft.world.LightType;
import net.minecraft.world.TickScheduler;
import net.minecraft.world.biome.source.BiomeArray;
import net.minecraft.world.chunk.*;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.StructureFeature;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.zip.DeflaterOutputStream;

import static java.lang.Math.round;

public class HUD extends Module {

	private long prevTime = 0;
	private double tps = 20;
	private long lastPacket = 0;
	private long timer = 0;
	private int chunksize = 0;

	public List<String> alertList = new ArrayList<>();

	public HUD() {
		super("HUD", KEY_UNBOUND, Category.CLIENT, "Shows stuff onscreen.",
				new SettingToggle("Arraylist", true).withDesc("Shows the module list").withChildren( // 0
						new SettingSlider("x", 1, 3840, 1, 0).withDesc("x coordinates"),
						new SettingSlider("y", 1, 3840, 11, 0).withDesc("y coordinates"),
						new SettingToggle("Right Align", true),
						new SettingSlider("Text Gap", 1, 10, 10, 0).withDesc("new line space distance")),
				new SettingToggle("Watermark", true).withDesc("Adds the MacHack watermark to the arraylist").withChildren( // 1
						new SettingSlider("x", 1, 3840, 1, 0).withDesc("x coordinates"),
						new SettingSlider("y", 1, 3840, 1, 0).withDesc("y coordinates"),
						new SettingToggle("Right Align", true)),
				new SettingToggle("FPS", true).withDesc("Shows your FPS").withChildren( // 2
						new SettingSlider("x", 1, 3840, 1, 0).withDesc("x coordinates"),
						new SettingSlider("y", 1, 3840, 250, 0).withDesc("y coordinates"),
						new SettingToggle("Right Align", true)),
				new SettingToggle("Ping", true).withDesc("Shows your ping").withChildren( // 3
						new SettingSlider("x", 1, 3840, 1, 0).withDesc("x coordinates"),
						new SettingSlider("y", 1, 3840, 240, 0).withDesc("y coordinates"),
						new SettingToggle("Right Align", true)),
				new SettingToggle("Coords", true).withDesc("Shows your coords and nether coords").withChildren( // 4
						new SettingSlider("x", 1, 3840, 1, 0).withDesc("x coordinates"),
						new SettingSlider("y", 1, 3840, 200, 0).withDesc("y coordinates"),
						new SettingToggle("Right Align", true),
						new SettingToggle("Show X", true),
						new SettingToggle("Show Y", true),
						new SettingToggle("Show Z", true),
						new SettingToggle("Show Nether", true)
				),
				new SettingToggle("TPS", true).withDesc("Shows the estimated server tps").withChildren( // 5
						new SettingSlider("x", 1, 3840, 1, 0).withDesc("x coordinates"),
						new SettingSlider("y", 1, 3840, 210, 0).withDesc("y coordinates"),
						new SettingToggle("Right Align", true)),
				new SettingToggle("Lag-Meter", true).withDesc("Shows when the server is lagging"), // 6
				new SettingToggle("IP", true).withDesc("Shows the current server IP you are on").withChildren( // 7
						new SettingSlider("x", 1, 3840, 1, 0).withDesc("x coordinates"),
						new SettingSlider("y", 1, 3840, 220, 0).withDesc("y coordinates"),
						new SettingToggle("Right Align", true)),
				new SettingToggle("Players", true).withDesc("Lists all the players in your render distance").withChildren( // 8
						new SettingSlider("x", 1, 3840, 1, 0).withDesc("x coordinates"),
						new SettingSlider("y", 1, 3840, 350, 0).withDesc("y coordinates"),
						new SettingToggle("Right Align", true),
						new SettingSlider("Text Gap", 1, 10, 10, 0).withDesc("new line space distance"),
						new SettingToggle("Show coordinates", false).withDesc("shows xyz coords of players near you"),
						new SettingSlider("Player distance", 1, 128, 60, 0).withDesc("Lower or raise player distance")),
				new SettingToggle("Armor", true).withDesc("Shows your current armor").withChildren( // 9
						new SettingMode("Damage", "Number", "Bar", "Both").withDesc("How to show the armor durability")),
				new SettingToggle("Time", true).withDesc("Shows the current time").withChildren( // 10
						new SettingToggle("Time Zone", false).withDesc("Shows your time zone in the time"),
						new SettingToggle("Year", false).withDesc("Shows the current year in the time"),
						new SettingToggle("Month/Day", false).withDesc("Shows the current day and month in the time"),
						new SettingToggle("Seconds", false).withDesc("adds seconds to time"),
						new SettingToggle("AM/PM", true).withDesc("adds AM/PM marker to time"),
						new SettingSlider("x", 1, 3840, 1, 0).withDesc("x coordinates"),
						new SettingSlider("y", 1, 3840, 230, 0).withDesc("y coordinates"),
						new SettingToggle("Right Align", true)),
				new SettingToggle("BPS", true).withDesc("Shows your block per second speed").withChildren( // 11
						new SettingSlider("x", 1, 3840, 1, 0).withDesc("x coordinates"),
						new SettingSlider("y", 1, 3840, 260, 0).withDesc("y coordinates"),
						new SettingToggle("Right Align", true)),
				new SettingToggle("Online", true).withDesc("Shows count of players online").withChildren( // 12
						new SettingSlider("x", 1, 3840, 1, 0).withDesc("x coordinates"),
						new SettingSlider("y", 1, 3840, 270, 0).withDesc("y coordinates"),
						new SettingToggle("Right Align", true)),
				new SettingToggle("Welcome", true).withDesc("Shows your username").withChildren( // 13
						new SettingSlider("x", 1, 3840, 1, 0).withDesc("x coordinates"),
						new SettingSlider("y", 1, 3840, 190, 0).withDesc("y coordinates"),
						new SettingToggle("Right Align", true),
						new SettingToggle("Legacy", false)),
				new SettingToggle("Biome", true).withDesc("Shows your current biome").withChildren( // 14
						new SettingSlider("x", 1, 3840, 1, 0).withDesc("x coordinates"),
						new SettingSlider("y", 1, 3840, 280, 0).withDesc("y coordinates"),
						new SettingToggle("Right Align", true)),
				new SettingToggle("Inventory", true).withDesc("Shows your inventory on your screen").withChildren( // 15
						new SettingSlider("x", 1, 3840, 571, 0).withDesc("x coordinates"),
						new SettingSlider("y", 1, 3840, 459, 0).withDesc("y coordinates"),
						new SettingMode("Style", "GUI Color", "Black", "Clear").withDesc("Color of the background")),
				new SettingToggle("Chunk Size", true).withDesc("Shows the size of the chunk you are standing in").withChildren( // 15
						new SettingSlider("x", 1, 3840, 1, 0).withDesc("x coordinates"),
						new SettingSlider("y", 1, 3840, 290, 0).withDesc("y coordinates"),
						new SettingToggle("Right Align", true)),
				new SettingToggle("Saturation", true).withDesc("Shows your saturation level").withChildren( // 15
						new SettingSlider("x", 1, 3840, 1, 0).withDesc("x coordinates"),
						new SettingSlider("y", 1, 3840, 300, 0).withDesc("y coordinates"),
						new SettingToggle("Right Align", true)),
				new SettingToggle("Totems", true).withDesc("Shows how many totems you have in your inventory").withChildren( // 15
						new SettingSlider("x", 1, 3840, 1, 0).withDesc("x coordinates"),
						new SettingSlider("y", 1, 3840, 310, 0).withDesc("y coordinates"),
						new SettingToggle("Right Align", true),
						new SettingToggle("Screen Alert", false).withChildren(
								new SettingSlider("Value", 0, 10, 3, 0)
						)),
				new SettingToggle("Beds", true).withDesc("Shows how many beds you have in your inventory").withChildren( // 15
						new SettingSlider("x", 1, 3840, 1, 0).withDesc("x coordinates"),
						new SettingSlider("y", 1, 3840, 320, 0).withDesc("y coordinates"),
						new SettingToggle("Right Align", true),
						new SettingToggle("Screen Alert", false).withChildren(
								new SettingSlider("Value", 0, 10, 3, 0)
						)),
				new SettingToggle("Durability", true).withDesc("Shows how many totems you have in your inventory").withChildren( // 15
						new SettingSlider("x", 1, 3840, 1, 0).withDesc("x coordinates"),
						new SettingSlider("y", 1, 3840, 330, 0).withDesc("y coordinates"),
						new SettingToggle("Right Align", true)),
				new SettingSlider("HueBright", 0, 1, 1, 2).withDesc("Rainbow Hue"), // 15
				new SettingSlider("HueSat", 0, 1, 0.5, 2).withDesc("Rainbow Saturation"), // 16
				new SettingSlider("HueSpeed", 0.1, 50, 10, 1).withDesc("Rainbow Speed"), // 17
				new SettingToggle("Impact+", true), //18
				new SettingToggle("Player Model", false).withChildren( //25
						new SettingSlider("Size", 10, 100, 25, 0),
						new SettingSlider("x", 1, 3840, 80, 0).withDesc("x coordinates"),
						new SettingSlider("y", 1, 3840, 190, 0).withDesc("y coordinates"),
						new SettingToggle("Legacy", false)
				),
				new SettingToggle("Direction", false).withChildren( //26
						new SettingSlider("x", 1, 3840, 1, 0).withDesc("x coordinates"),
						new SettingSlider("y", 1, 3840, 320, 0).withDesc("y coordinates")
				),
				new SettingToggle("Picks", true).withDesc("Shows how many beds you have in your inventory").withChildren( // 27
						new SettingSlider("x", 1, 3840, 1, 0).withDesc("x coordinates"),
						new SettingSlider("y", 1, 3840, 320, 0).withDesc("y coordinates"),
						new SettingToggle("Right Align", true),
						new SettingToggle("Screen Alert", false).withChildren(
								new SettingSlider("Value", 0, 10, 3, 0)
						)
				)/* r333mo WIP,
				new SettingToggle("NearPlayerRender", false).withDesc("Renders the nearest player to you.").withChildren(	// 28. Just following the pattern here! - r333mo
						new SettingSlider("Size", 10, 50, 25, 0).withDesc("Scale"),
						new SettingSlider("x", 1, 3840, 80, 0).withDesc("x coordinates"),
						new SettingSlider("y", 1, 3840, 190, 0).withDesc("y coordinates")
				)
				 */
		);
	}

	@Subscribe
	public void onDrawOverlay(EventDrawOverlay event) {

		int arrayCount = 0;
		if (getSetting(0).asToggle().state && !mc.options.debugEnabled || getSetting(1).asToggle().state && !mc.options.debugEnabled) {
			List<String> lines = new ArrayList<>();

			if (getSetting(0).asToggle().state) {
				for (Module m : ModuleManager.getModules())
					if (m.isToggled() && m.isDrawn() && !m.getName().equals("HUD") && !m.isHidden()) lines.add(m.getName());

				lines.sort((a, b) -> Integer.compare(mc.textRenderer.getWidth(b), mc.textRenderer.getWidth(a)));
			}
			if (getSetting(0).asToggle().getChild(2).asToggle().state) {
				for (String s : lines) {
					//if (s.equals("ElytraFly")) {
					//    s = "ElytraFly" + (mc.world.getRegistryKey().getValue().getPath().equalsIgnoreCase("the_nether") ? " \u00a77[\u00a7rNether\u00a77]" : "") + (mc.world.getRegistryKey().getValue().getPath().equalsIgnoreCase("the_end") ? " \u00a77[\u00a7rEnd\u00a77]" : "") + (mc.world.getRegistryKey().getValue().getPath().equalsIgnoreCase("overworld") ? " \u00a77[\u00a7rOverworld\u00a77]" : "");
					//}
					mc.textRenderer.drawWithShadow(event.matrix, s, (int) getSetting(0).asToggle().getChild(0).asSlider().getValue(), (int) getSetting(0).asToggle().getChild(1).asSlider().getValue() + (arrayCount * (int) getSetting(0).asToggle().getChild(3).asSlider().getValue()), ColorUtils.guiColour());
					arrayCount++;
				}
			} else{
				for (String s : lines) {
					//if (s.equals("ElytraFly")) {
					//    s = (mc.world.getRegistryKey().getValue().getPath().equalsIgnoreCase("the_nether") ? "\u00a77[\u00a7rNether\u00a77] \u00a7r" : "") + (mc.world.getRegistryKey().getValue().getPath().equalsIgnoreCase("the_end") ? "\u00a77[\u00a7rEnd\u00a77] \u00a7r" : "") + (mc.world.getRegistryKey().getValue().getPath().equalsIgnoreCase("overworld") ? "\u00a77[\u00a7rOverworld\u00a77] \u00a7r" : "") + "ElytraFly";
					//}
					mc.textRenderer.drawWithShadow(event.matrix, s, (int) getSetting(0).asToggle().getChild(0).asSlider().getValue() - mc.textRenderer.getWidth(s), (int) getSetting(0).asToggle().getChild(1).asSlider().getValue() + (arrayCount * (int) getSetting(0).asToggle().getChild(3).asSlider().getValue()), ColorUtils.guiColour());
					arrayCount++;
				}
			}

		}

		int playerarrayCount = 0;
		if (getSetting(8).asToggle().state && !mc.options.debugEnabled) {
			String radar_title = "Player Radar" + (getSetting(24).asToggle().state ? "" : "\u00a77:\u00a7r");
			if (getSetting(8).asToggle().getChild(2).asToggle().state) {
				mc.textRenderer.drawWithShadow(event.matrix, radar_title, (int) getSetting(8).asToggle().getChild(0).asSlider().getValue(), (int) getSetting(8).asToggle().getChild(1).asSlider().getValue(), ColorUtils.guiColour());
			} else {
				mc.textRenderer.drawWithShadow(event.matrix, radar_title, (int) getSetting(8).asToggle().getChild(0).asSlider().getValue() - mc.textRenderer.getWidth(radar_title), (int) getSetting(8).asToggle().getChild(1).asSlider().getValue(), ColorUtils.guiColour());
			}

			playerarrayCount++;

			for (Entity e : mc.world.getPlayers().stream().sorted((a, b) -> Double.compare(mc.player.getPos().distanceTo(a.getPos()), mc.player.getPos().distanceTo(b.getPos())))
					.collect(Collectors.toList())) {
				if (e == mc.player) continue;

				int dist = (int) round(mc.player.getPos().distanceTo(e.getPos()));

				String text = e.getDisplayName().getString() + (getSetting(8).asToggle().getChild(4).asToggle().state ? " \u00a77[\u00a7r" + e.getBlockPos().getX() + " " + e.getBlockPos().getY() + " " + e.getBlockPos().getZ() + "\u00a77]\u00a7r " : " ")
						+ "\u00a77(\u00a7r" + dist + "m\u00a77)\u00a7r";
				if (dist <= (int) getSetting(8).asToggle().getChild(5).asSlider().getValue()) {
					if (getSetting(8).asToggle().getChild(2).asToggle().state) {
						if (MacHack.friendMang.has(e.getDisplayName().getString())) {
							mc.textRenderer.drawWithShadow(event.matrix, text, (int) getSetting(8).asToggle().getChild(0).asSlider().getValue(), (int) getSetting(8).asToggle().getChild(1).asSlider().getValue() + (playerarrayCount * (int) getSetting(8).asToggle().getChild(3).asSlider().getValue()),
									new Color(85, 255, 255).getRGB());
							playerarrayCount++;
						} else {
							mc.textRenderer.drawWithShadow(event.matrix, text, (int) getSetting(8).asToggle().getChild(0).asSlider().getValue(), (int) getSetting(8).asToggle().getChild(1).asSlider().getValue() + (playerarrayCount * (int) getSetting(8).asToggle().getChild(3).asSlider().getValue()),
									new Color(255, 85, 85).getRGB());
							playerarrayCount++;
						}
					} else {
						if (MacHack.friendMang.has(e.getDisplayName().getString())) {
							mc.textRenderer.drawWithShadow(event.matrix, text, (int) getSetting(8).asToggle().getChild(0).asSlider().getValue() - mc.textRenderer.getWidth(text), (int) getSetting(8).asToggle().getChild(1).asSlider().getValue() + (playerarrayCount * (int) getSetting(8).asToggle().getChild(3).asSlider().getValue()),
									new Color(85, 255, 255).getRGB());
							playerarrayCount++;
						} else {
							mc.textRenderer.drawWithShadow(event.matrix, (((PlayerEntity) e).hasStatusEffect(StatusEffects.STRENGTH) ? "\u00a74" : "") + text, (int) getSetting(8).asToggle().getChild(0).asSlider().getValue() - mc.textRenderer.getWidth(text), (int) getSetting(8).asToggle().getChild(1).asSlider().getValue() + (playerarrayCount * (int) getSetting(8).asToggle().getChild(3).asSlider().getValue()),
									new Color(255, 85, 85).getRGB());
							playerarrayCount++;
						}
					}
				}
			}
		}


		if (getSetting(10).asToggle().state && !mc.options.debugEnabled) {
			if (getSetting(10).asToggle().getChild(7).asToggle().state) {
				String time_now = new SimpleDateFormat((getSetting(10).asToggle().getChild(2).asToggle().state ? "MMM dd " : "") + (getSetting(10).asToggle().getChild(1).asToggle().state ? "yyyy " : "") + "h:mm" + (getSetting(10).asToggle().getChild(3).asToggle().state ? ":ss" : "") + (getSetting(10).asToggle().getChild(4).asToggle().state ? " a" : "") + (getSetting(10).asToggle().getChild(0).asToggle().state ? " zzz" : "")).format(new Date());
				mc.textRenderer.drawWithShadow(event.matrix, "Time" + (getSetting(24).asToggle().state ? " \u00A7f" : "\u00a77: \u00a7r") + time_now,
						(int) getSetting(10).asToggle().getChild(5).asSlider().getValue(),
						(int) getSetting(10).asToggle().getChild(6).asSlider().getValue(),
						ColorUtils.guiColour());
			} else{
				String time_now = new SimpleDateFormat((getSetting(10).asToggle().getChild(2).asToggle().state ? "MMM dd " : "") + (getSetting(10).asToggle().getChild(1).asToggle().state ? "yyyy " : "") + "h:mm" + (getSetting(10).asToggle().getChild(3).asToggle().state ? ":ss" : "") + (getSetting(10).asToggle().getChild(4).asToggle().state ? " a" : "") + (getSetting(10).asToggle().getChild(0).asToggle().state ? " zzz" : "")).format(new Date());
				mc.textRenderer.drawWithShadow(event.matrix, "Time" + (getSetting(24).asToggle().state ? " \u00A7f" : "\u00a77: \u00a7r") + time_now,
						(int) getSetting(10).asToggle().getChild(5).asSlider().getValue() - mc.textRenderer.getWidth(time_now),
						(int) getSetting(10).asToggle().getChild(6).asSlider().getValue(),
						ColorUtils.guiColour());
			}
		}

		if (getSetting(1).asToggle().state && !mc.options.debugEnabled) {
			String watermark = MacHack.NAME + (getSetting(24).asToggle().state ? "\u00A7f" : "")  + MacHack.VERSION;
			if (getSetting(1).asToggle().getChild(2).asToggle().state) {
				mc.textRenderer.drawWithShadow(event.matrix, watermark, (int) getSetting(1).asToggle().getChild(0).asSlider().getValue(), (int) getSetting(1).asToggle().getChild(1).asSlider().getValue(), ColorUtils.guiColour());
			} else{
				mc.textRenderer.drawWithShadow(event.matrix, watermark, (int) getSetting(1).asToggle().getChild(0).asSlider().getValue() - mc.textRenderer.getWidth(watermark), (int) getSetting(1).asToggle().getChild(1).asSlider().getValue(), ColorUtils.guiColour());
			}
		}

		if (getSetting(4).asToggle().state && !mc.options.debugEnabled) {
			boolean nether = mc.world.getRegistryKey().getValue().getPath().equalsIgnoreCase("the_nether");
			BlockPos pos = mc.player.getBlockPos();
			Vec3d vec = mc.player.getPos();
			BlockPos pos2 = nether ? new BlockPos(vec.getX() * 8, vec.getY(), vec.getZ() * 8)
					: new BlockPos(vec.getX() / 8, vec.getY(), vec.getZ() / 8);

			String nether_coords = (getSetting(24).asToggle().state ? "\u00a77[\u00a7f" : " \u00a77[\u00a7r")
					+ (getSetting(4).asToggle().getChild(3).asToggle().state ? pos2.getX()+" " : "")
					+ (getSetting(4).asToggle().getChild(4).asToggle().state ? pos2.getY()+" " : "")
					+ (getSetting(4).asToggle().getChild(5).asToggle().state ? pos2.getZ()+"" : "")
					+ "\u00a77]";
			String coords = (getSetting(4).asToggle().getChild(3).asToggle().state ? "X" : "")
					+ (getSetting(4).asToggle().getChild(4).asToggle().state ? "Y" : "")
					+ (getSetting(4).asToggle().getChild(5).asToggle().state ? "Z" : "")
					+ (getSetting(24).asToggle().state ? " \u00A7f" : "\u00a77: \u00a7r")
					+ (getSetting(4).asToggle().getChild(3).asToggle().state ? pos.getX()+" " : "")
					+ (getSetting(4).asToggle().getChild(4).asToggle().state ? pos.getY()+" " : "")
					+ (getSetting(4).asToggle().getChild(5).asToggle().state ? pos.getZ()+" " : "")
					+ (getSetting(4).asToggle().getChild(6).asToggle().state ? nether_coords : "");

			if (getSetting(4).asToggle().getChild(2).asToggle().state) {
				mc.textRenderer.drawWithShadow(event.matrix, coords,
						(int) getSetting(4).asToggle().getChild(0).asSlider().getValue(),
						(int) getSetting(4).asToggle().getChild(1).asSlider().getValue(),
						ColorUtils.guiColour());
			} else{
				mc.textRenderer.drawWithShadow(event.matrix, coords,
						(int) getSetting(4).asToggle().getChild(0).asSlider().getValue() - mc.textRenderer.getWidth(coords),
						(int) getSetting(4).asToggle().getChild(1).asSlider().getValue(),
						ColorUtils.guiColour());

			}
		}

		if (getSetting(7).asToggle().state && !mc.options.debugEnabled) {
			String server = mc.getCurrentServerEntry() == null ? "Singleplayer" : mc.getCurrentServerEntry().address;
			String server1 = "IP" + (getSetting(24).asToggle().state ? " \u00a7f" : "\u00a77: \u00a7r") + server;
			if (getSetting(7).asToggle().getChild(2).asToggle().state) {
				mc.textRenderer.drawWithShadow(event.matrix, server1,
						(int) getSetting(7).asToggle().getChild(0).asSlider().getValue(),
						(int) getSetting(7).asToggle().getChild(1).asSlider().getValue(),
						ColorUtils.guiColour());
			} else{
				mc.textRenderer.drawWithShadow(event.matrix, server1,
						(int) getSetting(7).asToggle().getChild(0).asSlider().getValue() - mc.textRenderer.getWidth(server1),
						(int) getSetting(7).asToggle().getChild(1).asSlider().getValue(),
						ColorUtils.guiColour());
			}
		}


		if (getSetting(2).asToggle().state && !mc.options.debugEnabled) {
			int fps = (int) FabricReflect.getFieldValue(MinecraftClient.getInstance(), "field_1738", "currentFps");
			String fps1 = "FPS" + (getSetting(24).asToggle().state ? " \u00a7f" : "\u00a77: \u00a7r") + fps;
			if (getSetting(2).asToggle().getChild(2).asToggle().state) {
				mc.textRenderer.drawWithShadow(event.matrix, fps1,
						(int) getSetting(2).asToggle().getChild(0).asSlider().getValue(),
						(int) getSetting(2).asToggle().getChild(1).asSlider().getValue(),
						ColorUtils.guiColour());
			} else{
				mc.textRenderer.drawWithShadow(event.matrix, fps1,
						(int) getSetting(2).asToggle().getChild(0).asSlider().getValue() - mc.textRenderer.getWidth(fps1),
						(int) getSetting(2).asToggle().getChild(1).asSlider().getValue(),
						ColorUtils.guiColour());

			}
		}



		if (getSetting(3).asToggle().state && !mc.options.debugEnabled) {
			PlayerListEntry playerEntry = mc.player.networkHandler.getPlayerListEntry(mc.player.getGameProfile().getId());
			int ping = playerEntry == null ? 0 : playerEntry.getLatency();
			String ping1 = "Ping" + (getSetting(24).asToggle().state ? " \u00a7f" : "\u00a77: \u00a7r") + ping;
			if (getSetting(3).asToggle().getChild(2).asToggle().state) {
				mc.textRenderer.drawWithShadow(event.matrix, ping1,
						(int) getSetting(3).asToggle().getChild(0).asSlider().getValue(),
						(int) getSetting(3).asToggle().getChild(1).asSlider().getValue(),
						ColorUtils.guiColour());
			} else{
				mc.textRenderer.drawWithShadow(event.matrix, ping1,
						(int) getSetting(3).asToggle().getChild(0).asSlider().getValue() - mc.textRenderer.getWidth(ping1),
						(int) getSetting(3).asToggle().getChild(1).asSlider().getValue(),
						ColorUtils.guiColour());

			}
		}

		if (getSetting(11).asToggle().state && !mc.options.debugEnabled) {
			DecimalFormat decimalFormat = new DecimalFormat("0.0");
			final double deltaX = Math.abs(mc.player.getPos().getX() - mc.player.prevX);
			final double deltaZ = Math.abs(mc.player.getPos().getZ() - mc.player.prevZ);
			String bps = decimalFormat.format((deltaX + deltaZ) * 20);

			if (getSetting(11).asToggle().getChild(2).asToggle().state) {
				mc.textRenderer.drawWithShadow(event.matrix, "BPS" + (getSetting(24).asToggle().state ? " \u00a7f" : "\u00a77: \u00a7r") + bps,
						(int) getSetting(11).asToggle().getChild(0).asSlider().getValue(),
						(int) getSetting(11).asToggle().getChild(1).asSlider().getValue(),
						ColorUtils.guiColour());
			} else{
				mc.textRenderer.drawWithShadow(event.matrix, "BPS" + (getSetting(24).asToggle().state ? " \u00a7f" : "\u00a77: \u00a7r") + bps,
						(int) getSetting(11).asToggle().getChild(0).asSlider().getValue() - mc.textRenderer.getWidth(bps),
						(int) getSetting(11).asToggle().getChild(1).asSlider().getValue(),
						ColorUtils.guiColour());
			}
		}

		if (getSetting(5).asToggle().state && !mc.options.debugEnabled) {
			String suffix = (getSetting(24).asToggle().state ? "\u00a7f" : "\u00a77");
			if (lastPacket + 7500 < System.currentTimeMillis()) suffix += "....";
			else if (lastPacket + 5000 < System.currentTimeMillis()) suffix += "...";
			else if (lastPacket + 2500 < System.currentTimeMillis()) suffix += "..";
			else if (lastPacket + 1200 < System.currentTimeMillis()) suffix += ".";
			String tps1 = getColorString((int) tps, 18, 15, 12, 8, 4, false) + (getSetting(24).asToggle().state ? "\u00a7f" : "") + tps + suffix;
			if (getSetting(5).asToggle().getChild(2).asToggle().state) {
				mc.textRenderer.drawWithShadow(event.matrix, "TPS" + (getSetting(24).asToggle().state ? " \u00a7f" : "\u00a77: \u00a7r") + tps1,
						(int) getSetting(5).asToggle().getChild(0).asSlider().getValue(),
						(int) getSetting(5).asToggle().getChild(1).asSlider().getValue(),
						ColorUtils.guiColour());
			} else{
				mc.textRenderer.drawWithShadow(event.matrix, "TPS" + (getSetting(24).asToggle().state ? " \u00a7f" : "\u00a77: \u00a7r") + tps1,
						(int) getSetting(5).asToggle().getChild(0).asSlider().getValue() - mc.textRenderer.getWidth(tps1),
						(int) getSetting(5).asToggle().getChild(1).asSlider().getValue(),
						ColorUtils.guiColour());
			}

		}

		if (getSetting(13).asToggle().state && !mc.options.debugEnabled) {
			String welcome = "";

			if (getSetting(13).asToggle().getChild(3).asToggle().state) {
				welcome = "Welcome" + (getSetting(24).asToggle().state ? " \u00a7f" : "\u00a77, \u00a7r") + mc.player.getName().asString();
			} else {
				Calendar c = Calendar.getInstance();
				int timeOfDay = c.get(Calendar.HOUR_OF_DAY);

				if (timeOfDay >= 6 && timeOfDay < 12) {
					welcome = "Good Morning," + (getSetting(24).asToggle().state ? " \u00a7f" : "\u00a77, \u00a7r") + mc.player.getName().asString();
				} else if (timeOfDay >= 12 && timeOfDay < 17) {
					welcome = "Good Afternoon," + (getSetting(24).asToggle().state ? " \u00a7f" : "\u00a77, \u00a7r") + mc.player.getName().asString();
				} else if (timeOfDay >= 17 && timeOfDay < 22) {
					welcome = "Good Evening," + (getSetting(24).asToggle().state ? " \u00a7f" : "\u00a77, \u00a7r") + mc.player.getName().asString();
				} else if (timeOfDay >= 22 || timeOfDay < 6) {
					welcome = "Good Night," + (getSetting(24).asToggle().state ? " \u00a7f" : "\u00a77, \u00a7r") + mc.player.getName().asString();
				} else {
					welcome = "Hello," + mc.player.getName().asString() + ".. psst! something went wrong!";
				}
			}

			if (getSetting(13).asToggle().getChild(2).asToggle().state) {
				mc.textRenderer.drawWithShadow(event.matrix, welcome,
						(int) getSetting(13).asToggle().getChild(0).asSlider().getValue(),
						(int) getSetting(13).asToggle().getChild(1).asSlider().getValue(),
						ColorUtils.guiColour());
			} else{
				mc.textRenderer.drawWithShadow(event.matrix, welcome,
						(int) getSetting(13).asToggle().getChild(0).asSlider().getValue() - mc.textRenderer.getWidth(welcome),
						(int) getSetting(13).asToggle().getChild(1).asSlider().getValue(),
						ColorUtils.guiColour());
			}
		}


		if (getSetting(12).asToggle().state && !mc.options.debugEnabled) {
			String playercount = "Online" + (getSetting(24).asToggle().state ? " \u00a7f" : "\u00a77: \u00a7r") + mc.player.networkHandler.getPlayerList().size();
			if (getSetting(12).asToggle().getChild(2).asToggle().state) {
				mc.textRenderer.drawWithShadow(event.matrix, playercount,
						(int) getSetting(12).asToggle().getChild(0).asSlider().getValue(),
						(int) getSetting(12).asToggle().getChild(1).asSlider().getValue(),
						ColorUtils.guiColour());
			} else {
				mc.textRenderer.drawWithShadow(event.matrix, playercount,
						(int) getSetting(12).asToggle().getChild(0).asSlider().getValue() - mc.textRenderer.getWidth(playercount),
						(int) getSetting(12).asToggle().getChild(1).asSlider().getValue(),
						ColorUtils.guiColour());
			}
		}

		if (getSetting(14).asToggle().state && !mc.options.debugEnabled) {
			String biome = mc.world.getBiome(mc.player.getBlockPos()).getCategory().getName();
			String biome1 = biome.substring(0, 1).toUpperCase() + biome.substring(1);

			if (getSetting(14).asToggle().getChild(2).asToggle().state) {
				mc.textRenderer.drawWithShadow(event.matrix, "Biome" + (getSetting(24).asToggle().state ? " \u00a7f" : "\u00a77: \u00a7r") + biome1,
						(int) getSetting(14).asToggle().getChild(0).asSlider().getValue(),
						(int) getSetting(14).asToggle().getChild(1).asSlider().getValue(),
						ColorUtils.guiColour());
			} else {
				mc.textRenderer.drawWithShadow(event.matrix, "Biome" + (getSetting(24).asToggle().state ? " \u00a7f" : "\u00a77: \u00a7r") + biome1,
						(int) getSetting(14).asToggle().getChild(0).asSlider().getValue() - mc.textRenderer.getWidth(biome1),
						(int) getSetting(14).asToggle().getChild(1).asSlider().getValue(),
						ColorUtils.guiColour());
			}
		}



		if (getSetting(15).asToggle().state && !mc.options.debugEnabled) {
			if (mc.player == null)
				return;

			GL11.glPushMatrix();
			if (getSetting(15).asToggle().getChild(2).asMode().mode == 0) {
				RenderUtils.drawRect(
						(int) getSetting(15).asToggle().getChild(0).asSlider().getValue(),
						(int)getSetting(15).asToggle().getChild(1).asSlider().getValue(),
						(int) getSetting(15).asToggle().getChild(0).asSlider().getValue() + 146,
						(int) getSetting(15).asToggle().getChild(1).asSlider().getValue() + 50,
						ColorUtils.guiColour(),
						0.5f);
			} else if (getSetting(15).asToggle().getChild(2).asMode().mode == 1) {
				RenderUtils.drawRect(
						(int) getSetting(15).asToggle().getChild(0).asSlider().getValue(),
						(int)getSetting(15).asToggle().getChild(1).asSlider().getValue(),
						(int) getSetting(15).asToggle().getChild(0).asSlider().getValue() + 146,
						(int) getSetting(15).asToggle().getChild(1).asSlider().getValue() + 50,
						0x000000,
						0.5f);
			}

			for (int i = 0; i < 27; i++) {
				ItemStack itemStack = mc.player.inventory.main.get(i + 9);
				int offsetX = (int) getSetting(15).asToggle().getChild(0).asSlider().getValue() + (i % 9) * 16;
				int offsetY = (int) getSetting(15).asToggle().getChild(1).asSlider().getValue() + (i / 9) * 16;
				mc.getItemRenderer().renderGuiItemIcon(itemStack, offsetX, offsetY);
				mc.getItemRenderer().renderGuiItemOverlay(mc.textRenderer, itemStack, offsetX, offsetY);
			}

			mc.getItemRenderer().zOffset = 0.0F;
			GL11.glPopMatrix();
		}

		if (getSetting(16).asToggle().state && !mc.options.debugEnabled) {
			String chunksizevar = "Chunk Size" + (getSetting(24).asToggle().state ? " \u00a7f" : "\u00a77: \u00a7r") + (chunksize < 1000 ? chunksize + "B" : chunksize / 1000d + "KB");
			if (getSetting(16).asToggle().getChild(2).asToggle().state) {
				mc.textRenderer.drawWithShadow(event.matrix, chunksizevar,
						(int) getSetting(16).asToggle().getChild(0).asSlider().getValue(),
						(int) getSetting(16).asToggle().getChild(1).asSlider().getValue(),
						ColorUtils.guiColour());
			} else{
				mc.textRenderer.drawWithShadow(event.matrix, chunksizevar,
						(int) getSetting(16).asToggle().getChild(0).asSlider().getValue() - mc.textRenderer.getWidth(chunksizevar),
						(int) getSetting(16).asToggle().getChild(1).asSlider().getValue(),
						ColorUtils.guiColour());

			}
		}

		if (getSetting(17).asToggle().state && !mc.options.debugEnabled) {
			assert mc.player != null;
			float saturation_level = mc.player.getHungerManager().getSaturationLevel();
			DecimalFormat decimalFormat = new DecimalFormat("0.0");
			String saturationlevelvar = "Saturation" + (getSetting(24).asToggle().state ? " \u00a7f" : "\u00a77: \u00a7r") + decimalFormat.format(saturation_level);
			if (getSetting(17).asToggle().getChild(2).asToggle().state) {
				mc.textRenderer.drawWithShadow(event.matrix, saturationlevelvar,
						(int) getSetting(17).asToggle().getChild(0).asSlider().getValue(),
						(int) getSetting(17).asToggle().getChild(1).asSlider().getValue(),
						ColorUtils.guiColour());
			} else{
				mc.textRenderer.drawWithShadow(event.matrix, saturationlevelvar,
						(int) getSetting(17).asToggle().getChild(0).asSlider().getValue() - mc.textRenderer.getWidth(saturationlevelvar),
						(int) getSetting(17).asToggle().getChild(1).asSlider().getValue(),
						ColorUtils.guiColour());

			}
		}

		if (getSetting(18).asToggle().state && !mc.options.debugEnabled) {
			String totemcountvar = "Totems" + (getSetting(24).asToggle().state ? " \u00a7f" : "\u00a77: \u00a7r") + this.getTotems();
			if (getSetting(18).asToggle().getChild(2).asToggle().state) {
				mc.textRenderer.drawWithShadow(event.matrix, totemcountvar,
						(int) getSetting(18).asToggle().getChild(0).asSlider().getValue(),
						(int) getSetting(18).asToggle().getChild(1).asSlider().getValue(),
						ColorUtils.guiColour());
			} else{
				mc.textRenderer.drawWithShadow(event.matrix, totemcountvar,
						(int) getSetting(18).asToggle().getChild(0).asSlider().getValue() - mc.textRenderer.getWidth(totemcountvar),
						(int) getSetting(18).asToggle().getChild(1).asSlider().getValue(),
						ColorUtils.guiColour());

			}
		}

		if (getSetting(19).asToggle().state && !mc.options.debugEnabled) {
			String bedcountvar = "Beds" + (getSetting(24).asToggle().state ? " \u00a7f" : "\u00a77: \u00a7r") + this.getBeds();
			if (getSetting(19).asToggle().getChild(2).asToggle().state) {
				mc.textRenderer.drawWithShadow(event.matrix, bedcountvar,
						(int) getSetting(19).asToggle().getChild(0).asSlider().getValue(),
						(int) getSetting(19).asToggle().getChild(1).asSlider().getValue(),
						ColorUtils.guiColour());
			} else{
				mc.textRenderer.drawWithShadow(event.matrix, bedcountvar,
						(int) getSetting(19).asToggle().getChild(0).asSlider().getValue() - mc.textRenderer.getWidth(bedcountvar),
						(int) getSetting(19).asToggle().getChild(1).asSlider().getValue(),
						ColorUtils.guiColour());

			}
		}
		if (getSetting(20).asToggle().state && !mc.options.debugEnabled) {
			String dura;
			assert mc.player != null;
			if (mc.player.getMainHandStack().isDamageable()) {dura = "Durability" + (getSetting(24).asToggle().state ? " \u00a7f" : "\u00a77: \u00a7r") + (mc.player.getMainHandStack().getMaxDamage()-mc.player.getMainHandStack().getDamage());} else {
				dura = "Durability" + (getSetting(24).asToggle().state ? " \u00a7f" : "\u00a77: \u00a7r") + "-1";
			}
			if (getSetting(20).asToggle().getChild(2).asToggle().state) {
				mc.textRenderer.drawWithShadow(event.matrix, dura,
						(int) getSetting(20).asToggle().getChild(0).asSlider().getValue(),
						(int) getSetting(20).asToggle().getChild(1).asSlider().getValue(),
						ColorUtils.guiColour());
			} else{
				mc.textRenderer.drawWithShadow(event.matrix, dura,
						(int) getSetting(20).asToggle().getChild(0).asSlider().getValue() - mc.textRenderer.getWidth(dura),
						(int) getSetting(20).asToggle().getChild(1).asSlider().getValue(),
						ColorUtils.guiColour());

			}
		}

		if (getSetting(6).asToggle().state || getSetting(19).asToggle().getChild(3).asToggle().state || getSetting(18).asToggle().getChild(3).asToggle().state) {
			alertList.clear();
			if (getSetting(6).asToggle().state) {
				long time = System.currentTimeMillis();
				if (time - lastPacket > 500) {
					DecimalFormat round = new DecimalFormat("0.0");

					String text = "Server Lagging For " + (getSetting(24).asToggle().state ? "\u00a7f" : "") + (round.format((time - lastPacket) / 1000d)) + "s";
					alertList.add(text);
				}
			}
			if (getSetting(18).asToggle().getChild(3).asToggle().state && getSetting(18).asToggle().state) {
				double min_totems = getSetting(18).asToggle().getChild(3).asToggle().getChild(0).asSlider().getValue();
				int totem_count = this.getTotems();
				if (totem_count < min_totems) {
					String text = (getSetting(24).asToggle().state ? "\u00a7f" : "") + totem_count + (getSetting(24).asToggle().state ? " \u00a7r" : " ") + (totem_count == 1 ? "Totem Remaining" : "Totems Remaining");
					alertList.add(text);
				}
			}
			if (getSetting(19).asToggle().getChild(3).asToggle().state && getSetting(19).asToggle().state) {
				double min_beds = getSetting(19).asToggle().getChild(3).asToggle().getChild(0).asSlider().getValue();
				int bed_count = this.getBeds();
				if (bed_count < min_beds) {
					String text = (getSetting(24).asToggle().state ? "\u00a7f" : "") + bed_count + (getSetting(24).asToggle().state ? " \u00a7r" : " ") + (bed_count == 1 ? "Bed Remaining" : "Beds Remaining");
					alertList.add(text);
				}
			}
			int count2 = 0;
			for (String s : alertList) {
				mc.textRenderer.drawWithShadow(event.matrix, s, mc.getWindow().getScaledWidth() / 2 - mc.textRenderer.getWidth(s) / 2,
						2 + (count2 * 10), ColorUtils.guiColour());
				count2++;
			}

		}


		if (getSetting(9).asToggle().state && !mc.player.isCreative() && !mc.player.isSpectator() && !mc.options.debugEnabled) {
			GL11.glPushMatrix();

			int count = 0;
			int x1 = mc.getWindow().getScaledWidth() / 2;
			int y = mc.getWindow().getScaledHeight() -
					(mc.player.isSubmergedInWater() || mc.player.getAir() < mc.player.getMaxAir() ? 64 : 55);
			for (ItemStack is : mc.player.inventory.armor) {
				count++;
				if (is.isEmpty()) continue;
				int x = x1 - 90 + (9 - count) * 20 + 2;

				GL11.glEnable(GL11.GL_DEPTH_TEST);
				mc.getItemRenderer().zOffset = 200F;
				mc.getItemRenderer().renderGuiItemIcon(is, x, y);

				if (getSetting(9).asToggle().getChild(0).asMode().mode > 0) {
					mc.getItemRenderer().renderGuiItemOverlay(mc.textRenderer, is, x, y);
				}

				mc.getItemRenderer().zOffset = 0F;
				GL11.glDisable(GL11.GL_DEPTH_TEST);

				if (getSetting(9).asToggle().getChild(0).asMode().mode != 1) {
					GL11.glPushMatrix();
					GL11.glScaled(0.75, 0.75, 0.75);
					String s = is.getCount() > 1 ? "x" + is.getCount() : "";
					mc.textRenderer.drawWithShadow(event.matrix, s, (x + 19 - mc.textRenderer.getWidth(s)) * 1.333f, (y + 9) * 1.333f, ColorUtils.guiColour());

					if (is.isDamageable()) {
						String dur = is.getMaxDamage() - is.getDamage() + "";
						int durcolor = ColorUtils.guiColour();
						try {
							durcolor = MathHelper.hsvToRgb(((float) (is.getMaxDamage() - is.getDamage()) / is.getMaxDamage()) / 3.0F, 1.0F, 1.0F);
						} catch (Exception e) {
						}

						mc.textRenderer.drawWithShadow(event.matrix, dur, (x + 10 - mc.textRenderer.getWidth(dur) / 2) * 1.333f, (y - 3) * 1.333f, durcolor);
					}

					GL11.glPopMatrix();
				}
			}

			GL11.glEnable(GL11.GL_DEPTH_TEST);
			GL11.glPopMatrix();
		}
		if (getSetting(25).asToggle().state && !mc.options.debugEnabled) {
			if (getSetting(25).asToggle().getChild(3).asToggle().state) {
				InventoryScreen.drawEntity((int) getSetting(25).asToggle().getChild(1).asSlider().getValue(),
						(int) getSetting(25).asToggle().getChild(2).asSlider().getValue(),
						(int) getSetting(25).asToggle().getChild(0).asSlider().getValue(),
						0, -(int)mc.player.pitch, mc.player );
			} else {
				InventoryScreen.drawEntity((int) getSetting(25).asToggle().getChild(1).asSlider().getValue(),
						(int) getSetting(25).asToggle().getChild(2).asSlider().getValue(),
						(int) getSetting(25).asToggle().getChild(0).asSlider().getValue(),
						-(int)mc.player.yaw, -(int)mc.player.pitch, mc.player );
			}
		}

		if (getSetting(26).asToggle().state) {
			String FD = "ND";
			switch (EntityUtils.GetFacing()) {
				case North:
					FD = "N" + " (" + round(mc.player.yaw) + ")";
					break;
				case East:
					FD = "E" + " (" + round(mc.player.yaw) + ")";
					break;
				case South:
					FD = "S" + " (" + round(mc.player.yaw) + ")";
					break;
				case West:
					FD = "W" + " (" + round(mc.player.yaw) + ")";
					break;
			}
			if (!mc.options.debugEnabled)
				mc.textRenderer.drawWithShadow(event.matrix, "Facing: " + (getSetting(24).asToggle().state ? "\u00a7f" : "") + FD, (int) getSetting(26).asToggle().getChild(0).asSlider().getValue(), (int) getSetting(26).asToggle().getChild(1).asSlider().getValue(), ColorUtils.guiColour());
		}
		if (getSetting(27).asToggle().state && !mc.options.debugEnabled) {
			String pickcountvar = "Picks" + (getSetting(24).asToggle().state ? " \u00a7f" : "\u00a77: \u00a7r") + this.getPicks();
			if (getSetting(27).asToggle().getChild(2).asToggle().state) {
				mc.textRenderer.drawWithShadow(event.matrix, pickcountvar,
						(int) getSetting(27).asToggle().getChild(0).asSlider().getValue(),
						(int) getSetting(27).asToggle().getChild(1).asSlider().getValue(),
						ColorUtils.guiColour());
			} else{
				mc.textRenderer.drawWithShadow(event.matrix, pickcountvar,
						(int) getSetting(27).asToggle().getChild(0).asSlider().getValue() - mc.textRenderer.getWidth(pickcountvar),
						(int) getSetting(27).asToggle().getChild(1).asSlider().getValue(),
						ColorUtils.guiColour());

			}
		}
		if (getSetting(27).asToggle().getChild(2).asToggle().state && getSetting(27).asToggle().state) {
			double min_picks = getSetting(27).asToggle().getChild(3).asToggle().getChild(0).asSlider().getValue();
			int	pick_count = this.getPicks();
			if (pick_count < min_picks) {
				String text = (getSetting(24).asToggle().state ? "\u00a7f" : "") + pick_count + (getSetting(24).asToggle().state ? " \u00a7r" : " ") + (pick_count == 1 ? "Pick Remaining" : "Picks Remaining");
				alertList.add(text);
			}
		}

//		if (getSetting(28).asToggle().state && !mc.options.debugEnabled) { yo r333mo you commented out the setting but you didn't comment out the code so it blew up that's why
//			String playerType = "";
//			int responseTime = -1;
//			Entity p = null;
//			InventoryScreen.drawEntity((int) getSetting(28).asToggle().getChild(1).asSlider().getValue(), (
//							int) getSetting(28).asToggle().getChild(2).asSlider().getValue(),
//					(int) getSetting(28).asToggle().getChild(0).asSlider().getValue(),
//					-(int)mc.player.yaw, -(int)mc.player.pitch,
//					mc.world.getClosestPlayer(p, 100d));
//		}
	}

	@Subscribe
	public void readPacket(EventReadPacket event) {
		lastPacket = System.currentTimeMillis();
		if (event.getPacket() instanceof WorldTimeUpdateS2CPacket) {
			long time = System.currentTimeMillis();
			if (time < 500) return;
			long timeOffset = Math.abs(1000 - (time - prevTime)) + 1000;
			tps = round(MathHelper.clamp(20 / ((double) timeOffset / 1000), 0, 20) * 100d) / 100d;
			prevTime = time;
		}
	}

	@Subscribe
	public void onTick(EventTick event) {
		if (System.currentTimeMillis() - 1500 < timer)
			return;
		timer = System.currentTimeMillis();

		if (mc.world.getWorldChunk(mc.player.getBlockPos()) == null)
			return;
		new Thread(() -> {
			CompoundTag tag = serialize(mc.world, mc.world.getWorldChunk(mc.player.getBlockPos()));
			DataOutputStream output = new DataOutputStream(
					new BufferedOutputStream(new DeflaterOutputStream(new ByteArrayOutputStream(8096))));
			try {
				NbtIo.writeCompressed(tag, output);
			} catch (IOException e) {
			}
			chunksize = output.size();
		}).start();
	}
	private CompoundTag serialize(ClientWorld world, Chunk chunk) {
		ChunkPos chunkPos = chunk.getPos();
		CompoundTag compoundTag = new CompoundTag();
		CompoundTag compoundTag2 = new CompoundTag();
		compoundTag.putInt("DataVersion", SharedConstants.getGameVersion().getWorldVersion());
		compoundTag.put("Level", compoundTag2);
		compoundTag2.putInt("xPos", chunkPos.x);
		compoundTag2.putInt("zPos", chunkPos.z);
		compoundTag2.putLong("LastUpdate", world.getTime());
		compoundTag2.putLong("InhabitedTime", chunk.getInhabitedTime());
		compoundTag2.putString("Status", chunk.getStatus().getId());
		UpgradeData upgradeData = chunk.getUpgradeData();
		if (!upgradeData.isDone()) {
			compoundTag2.put("UpgradeData", upgradeData.toTag());
		}

		ChunkSection[] chunkSections = chunk.getSectionArray();
		ListTag listTag = new ListTag();
		LightingProvider lightingProvider = world.getChunkManager().getLightingProvider();
		boolean bl = chunk.isLightOn();

		CompoundTag compoundTag7;
		for (int i = -1; i < 17; ++i) {
			final int finalI = i;
			ChunkSection chunkSection = Arrays.stream(chunkSections).filter((chunkSectionx) -> {
				return chunkSectionx != null && chunkSectionx.getYOffset() >> 4 == finalI;
			}).findFirst().orElse(WorldChunk.EMPTY_SECTION);
			ChunkNibbleArray chunkNibbleArray = lightingProvider.get(LightType.BLOCK)
					.getLightSection(ChunkSectionPos.from(chunkPos, i));
			ChunkNibbleArray chunkNibbleArray2 = lightingProvider.get(LightType.SKY)
					.getLightSection(ChunkSectionPos.from(chunkPos, i));
			if (chunkSection != WorldChunk.EMPTY_SECTION || chunkNibbleArray != null || chunkNibbleArray2 != null) {
				compoundTag7 = new CompoundTag();
				compoundTag7.putByte("Y", (byte) (i & 255));
				if (chunkSection != WorldChunk.EMPTY_SECTION) {
					chunkSection.getContainer().write(compoundTag7, "Palette", "BlockStates");
				}

				if (chunkNibbleArray != null && !chunkNibbleArray.isUninitialized()) {
					compoundTag7.putByteArray("BlockLight", chunkNibbleArray.asByteArray());
				}

				if (chunkNibbleArray2 != null && !chunkNibbleArray2.isUninitialized()) {
					compoundTag7.putByteArray("SkyLight", chunkNibbleArray2.asByteArray());
				}

				listTag.add(compoundTag7);
			}
		}

		compoundTag2.put("Sections", listTag);
		if (bl) {
			compoundTag2.putBoolean("isLightOn", true);
		}

		BiomeArray biomeArray = chunk.getBiomeArray();
		if (biomeArray != null) {
			compoundTag2.putIntArray("Biomes", biomeArray.toIntArray());
		}

		ListTag listTag2 = new ListTag();
		Iterator<BlockPos> var21 = chunk.getBlockEntityPositions().iterator();

		CompoundTag compoundTag6;
		while (var21.hasNext()) {
			BlockPos blockPos = var21.next();
			compoundTag6 = chunk.getPackedBlockEntityTag(blockPos);
			if (compoundTag6 != null) {
				listTag2.add(compoundTag6);
			}
		}

		compoundTag2.put("TileEntities", listTag2);
		ListTag listTag3 = new ListTag();
		if (chunk.getStatus().getChunkType() == ChunkStatus.ChunkType.field_12807) {
			WorldChunk worldChunk = (WorldChunk) chunk;
			worldChunk.setUnsaved(false);

			for (int k = 0; k < worldChunk.getEntitySectionArray().length; ++k) {
				Iterator<Entity> var29 = worldChunk.getEntitySectionArray()[k].iterator();

				while (var29.hasNext()) {
					Entity entity = var29.next();
					CompoundTag compoundTag5 = new CompoundTag();
					if (entity.saveToTag(compoundTag5)) {
						worldChunk.setUnsaved(true);
						listTag3.add(compoundTag5);
					}
				}
			}
		} else {
			ProtoChunk protoChunk = (ProtoChunk) chunk;
			listTag3.addAll(protoChunk.getEntities());
			compoundTag2.put("Lights", toNbt(protoChunk.getLightSourcesBySection()));
			compoundTag6 = new CompoundTag();
			GenerationStep.Carver[] var30 = GenerationStep.Carver.values();
			int var32 = var30.length;

			for (int var34 = 0; var34 < var32; ++var34) {
				GenerationStep.Carver carver = var30[var34];
				BitSet bitSet = protoChunk.getCarvingMask(carver);
				if (bitSet != null) {
					compoundTag6.putByteArray(carver.toString(), bitSet.toByteArray());
				}
			}

			compoundTag2.put("CarvingMasks", compoundTag6);
		}

		compoundTag2.put("Entities", listTag3);
		TickScheduler<Block> tickScheduler = chunk.getBlockTickScheduler();
		if (tickScheduler instanceof ChunkTickScheduler) {
			compoundTag2.put("ToBeTicked", ((ChunkTickScheduler<Block>) tickScheduler).toNbt());
		} else if (tickScheduler instanceof SimpleTickScheduler) {
			compoundTag2.put("TileTicks", ((SimpleTickScheduler<Block>) tickScheduler).toNbt());
		}

		TickScheduler<Fluid> tickScheduler2 = chunk.getFluidTickScheduler();
		if (tickScheduler2 instanceof ChunkTickScheduler) {
			compoundTag2.put("LiquidsToBeTicked", ((ChunkTickScheduler<Fluid>) tickScheduler2).toNbt());
		} else if (tickScheduler2 instanceof SimpleTickScheduler) {
			compoundTag2.put("LiquidTicks", ((SimpleTickScheduler<Fluid>) tickScheduler2).toNbt());
		}

		compoundTag2.put("PostProcessing", toNbt(chunk.getPostProcessingLists()));
		compoundTag7 = new CompoundTag();
		Iterator<Entry<Heightmap.Type, Heightmap>> var33 = chunk.getHeightmaps().iterator();

		while (var33.hasNext()) {
			Map.Entry<Heightmap.Type, Heightmap> entry = var33.next();
			if (chunk.getStatus().getHeightmapTypes().contains(entry.getKey())) {
				compoundTag7.put(entry.getKey().getName(),
						new LongArrayTag(entry.getValue().asLongArray()));
			}
		}

		compoundTag2.put("Heightmaps", compoundTag7);
		compoundTag2.put("Structures",
				writeStructures(chunkPos, chunk.getStructureStarts(), chunk.getStructureReferences()));
		return compoundTag;
	}

	private CompoundTag writeStructures(ChunkPos pos, Map<StructureFeature<?>, StructureStart<?>> structureStarts,
										Map<StructureFeature<?>, LongSet> structureReferences) {
		CompoundTag compoundTag = new CompoundTag();
		CompoundTag compoundTag2 = new CompoundTag();
		Iterator<Map.Entry<StructureFeature<?>, StructureStart<?>>> var5 = structureStarts.entrySet().iterator();

		while (var5.hasNext()) {
			Entry<StructureFeature<?>, StructureStart<?>> entry = var5
					.next();
			compoundTag2.put(entry.getKey().getName(),
					entry.getValue().toTag(pos.x, pos.z));
		}

		compoundTag.put("Starts", compoundTag2);
		CompoundTag compoundTag3 = new CompoundTag();
		Iterator<Entry<StructureFeature<?>, LongSet>> var9 = structureReferences.entrySet().iterator();

		while (var9.hasNext()) {
			Entry<StructureFeature<?>, LongSet> entry2 = var9.next();
			compoundTag3.put(entry2.getKey().getName(),
					new LongArrayTag(entry2.getValue()));
		}

		compoundTag.put("References", compoundTag3);
		return compoundTag;
	}

	private ListTag toNbt(ShortList[] lists) {
		ListTag listTag = new ListTag();
		ShortList[] var2 = lists;
		int var3 = lists.length;

		for (int var4 = 0; var4 < var3; ++var4) {
			ShortList shortList = var2[var4];
			ListTag listTag2 = new ListTag();
			if (shortList != null) {
				ShortListIterator var7 = shortList.iterator();

				while (var7.hasNext()) {
					Short short_ = var7.nextShort();
					listTag2.add(ShortTag.of(short_));
				}
			}

			listTag.add(listTag2);
		}

		return listTag;
	}

	private int getTotems()
	{
		int c = 0;

		for (int i = 0; i < 45; ++i)
		{
			if (this.mc.player.inventory.getStack(i).getItem() == Items.TOTEM_OF_UNDYING)
			{
				++c;
			}
		}

		return c;
	}

	private int getBeds()
	{
		int c = 0;

		for (int i = 0; i < 45; ++i)
		{
			if (this.mc.player.inventory.getStack(i).getItem() == Items.BLACK_BED
					|| this.mc.player.inventory.getStack(i).getItem() == Items.BLUE_BED
					|| this.mc.player.inventory.getStack(i).getItem() == Items.BROWN_BED
					|| this.mc.player.inventory.getStack(i).getItem() == Items.CYAN_BED
					|| this.mc.player.inventory.getStack(i).getItem() == Items.GRAY_BED
					|| this.mc.player.inventory.getStack(i).getItem() == Items.GREEN_BED
					|| this.mc.player.inventory.getStack(i).getItem() == Items.LIGHT_BLUE_BED
					|| this.mc.player.inventory.getStack(i).getItem() == Items.LIGHT_GRAY_BED
					|| this.mc.player.inventory.getStack(i).getItem() == Items.LIME_BED
					|| this.mc.player.inventory.getStack(i).getItem() == Items.MAGENTA_BED
					|| this.mc.player.inventory.getStack(i).getItem() == Items.ORANGE_BED
					|| this.mc.player.inventory.getStack(i).getItem() == Items.PINK_BED
					|| this.mc.player.inventory.getStack(i).getItem() == Items.PURPLE_BED
					|| this.mc.player.inventory.getStack(i).getItem() == Items.RED_BED
					|| this.mc.player.inventory.getStack(i).getItem() == Items.WHITE_BED
					|| this.mc.player.inventory.getStack(i).getItem() == Items.YELLOW_BED
			)
			{
				++c;
			}
		}

		return c;
	}
	private int getPicks()
	{
		int c = 0;

		for (int i = 0; i < 45; ++i)
		{
			if (this.mc.player.inventory.getStack(i).getItem() == Items.BLACK_BED
					|| this.mc.player.inventory.getStack(i).getItem() == Items.DIAMOND_PICKAXE
					|| this.mc.player.inventory.getStack(i).getItem() == Items.GOLDEN_PICKAXE
					|| this.mc.player.inventory.getStack(i).getItem() == Items.IRON_PICKAXE
					|| this.mc.player.inventory.getStack(i).getItem() == Items.NETHERITE_PICKAXE
					|| this.mc.player.inventory.getStack(i).getItem() == Items.STONE_PICKAXE
					|| this.mc.player.inventory.getStack(i).getItem() == Items.WOODEN_PICKAXE
			)
			{
				++c;
			}
		}

		return c;
	}

	public String getColorString(int value, int best, int good, int mid, int bad, int worst, boolean rev) {
		if (!rev ? value > best : value < best) return "\u00a72";
		else if (!rev ? value > good : value < good) return "\u00a7a";
		else if (!rev ? value > mid : value < mid) return "\u00a7e";
		else if (!rev ? value > bad : value < bad) return "\u00a76";
		else if (!rev ? value > worst : value < worst) return "\u00a7c";
		else return "\u00a74";
	}

	public static int getRainbow(float sat, float bri, double speed, int offset) {
		double rainbowState = Math.ceil((System.currentTimeMillis() + offset) / speed);
		rainbowState %= 360.0;
		return Color.HSBtoRGB((float) (rainbowState / 360.0), sat, bri);
	}

	public static int getRainbowFromSettings(int offset) {
		Module ui = ModuleManager.getModule(HUD.class);

		if (ui == null) return getRainbow(0.5f, 0.5f, 10, 0);

		return getRainbow((float) ui.getSetting(21).asSlider().getValue(),
				(float) ui.getSetting(22).asSlider().getValue(),
				ui.getSetting(23).asSlider().getValue(),
				offset);
	}
}