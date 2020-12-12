package mac.hack.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class MacLogger {

	public static void infoMessage(String s) {
		try {
			MinecraftClient.getInstance().inGameHud.getChatHud()
					.addMessage(new LiteralText(getBHText(Formatting.GRAY) + "" + s));
		} catch (Exception e) {
			System.out.println("[MH] INFO: " + s);
		}
	}

	public static void warningMessage(String s) {
		try {
			MinecraftClient.getInstance().inGameHud.getChatHud()
					.addMessage(new LiteralText(getBHText(Formatting.GRAY) + "" + s));
		} catch (Exception e) {
			System.out.println("[MH] WARN: " + s);
		}
	}

	public static void errorMessage(String s) {
		try {
			MinecraftClient.getInstance().inGameHud.getChatHud()
					.addMessage(new LiteralText(getBHText(Formatting.GRAY) + "" + s));
		} catch (Exception e) {
			System.out.println("[MH] ERROR: " + s);
		}
	}

	public static void noPrefixMessage(String s) {
		try {
			MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(new LiteralText(s));
		} catch (Exception e) {
			System.out.println(s);
		}
	}

	public static void noPrefixMessage(Text text) {
		try {
			MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(text);
		} catch (Exception e) {
			System.out.println(text.asString());
		}
	}

	private static String getBHText(Formatting color) {
		return color + "\u00A77[\u00A7dMacHack\u00A77] \u00A7f";
	}
}