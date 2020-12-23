package mac.hack.gui.clickgui;

import mac.hack.MacHack;
import mac.hack.command.Command;
import mac.hack.gui.clickgui.modulewindow.ClickGuiWindow;
import mac.hack.gui.clickgui.modulewindow.MHLogo;
import mac.hack.gui.clickgui.modulewindow.ModuleWindow;
import mac.hack.gui.window.AbstractWindowScreen;
import mac.hack.gui.window.Window;
import mac.hack.module.Category;
import mac.hack.module.Module;
import mac.hack.module.ModuleManager;
import mac.hack.module.mods.ClickGui;
import mac.hack.module.mods.UI;
import mac.hack.utils.ColorUtils;
import mac.hack.utils.file.MacFileHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClickGuiScreen extends AbstractWindowScreen {

	private int keyDown = -1;
	private boolean lmDown = false;
	private boolean rmDown = false;
	private boolean lmHeld = false;
	private int mwScroll = 0;
	private static final Identifier identifier = new Identifier("machack", "assets/machack/machack3.png");

	private TextFieldWidget searchField;

	public ClickGuiScreen() {
		super(new LiteralText("ClickGui"));
	}

	public void init() {
		searchField = new TextFieldWidget(textRenderer, 10, 14, 100, 12, LiteralText.EMPTY /* @LasnikProgram is author lol*/);
		searchField.visible = false;
		searchField.setMaxLength(20);
		searchField.setSuggestion("Search here");
		addButton(searchField);
	}

	public void initWindows() {
		int len = (int) ModuleManager.getModule(ClickGui.class).getSetting(0).asSlider().getValue();
		int i = 10;
		windows.add(new ModuleWindow(ModuleManager.getModulesInCat(Category.COMBAT), i, 35, len,
				StringUtils.capitalize(StringUtils.lowerCase(Category.COMBAT.toString())), new ItemStack(Items.ARMOR_STAND)));
		i += len + 5;
		windows.add(new ModuleWindow(ModuleManager.getModulesInCat(Category.MISC), i, 35, len,
				StringUtils.capitalize(StringUtils.lowerCase(Category.MISC.toString())), new ItemStack(Items.DIAMOND_SWORD)));
		i += len + 5;
		windows.add(new ModuleWindow(ModuleManager.getModulesInCat(Category.MOVEMENT), i, 35, len,
				StringUtils.capitalize(StringUtils.lowerCase(Category.MOVEMENT.toString())), new ItemStack(Items.BEACON)));
		i += len + 5;
		windows.add(new ModuleWindow(ModuleManager.getModulesInCat(Category.PLAYER), i, 35, len,
				StringUtils.capitalize(StringUtils.lowerCase(Category.PLAYER.toString())), new ItemStack(Items.GRASS_BLOCK)));
		i += len + 5;
		windows.add(new ModuleWindow(ModuleManager.getModulesInCat(Category.RENDER), i, 35, len,
				StringUtils.capitalize(StringUtils.lowerCase(Category.RENDER.toString())), new ItemStack(Items.POTION)));
		i += len + 5;
		windows.add(new ModuleWindow(ModuleManager.getModulesInCat(Category.WORLD), i, 35, len,
				StringUtils.capitalize(StringUtils.lowerCase(Category.WORLD.toString())), new ItemStack(Items.BEDROCK)));
		i += len + 5;
		windows.add(new ModuleWindow(ModuleManager.getModulesInCat(Category.EXPLOITS), i, 35, len,
				StringUtils.capitalize(StringUtils.lowerCase(Category.EXPLOITS.toString())), new ItemStack(Items.WRITABLE_BOOK)));
		i += len + 5;
		windows.add(new ModuleWindow(ModuleManager.getModulesInCat(Category.CHAT), i, 35, len,
				StringUtils.capitalize(StringUtils.lowerCase(Category.CHAT.toString())), new ItemStack(Items.SPAWNER)));
		i += len + 5;
		windows.add(new ModuleWindow(ModuleManager.getModulesInCat(Category.CLIENT), i, 35, len,
				StringUtils.capitalize(StringUtils.lowerCase(Category.CLIENT.toString())), new ItemStack(Items.PUFFERFISH)));
	}

	public boolean isPauseScreen() {
		return false;
	}

	public void onClose() {
		ModuleManager.getModule(ClickGui.class).setToggled(false);
		client.openScreen(null);
	}

	public void render(MatrixStack matrix, int mX, int mY, float float_1) {
		MacFileHelper.SCHEDULE_SAVE_CLICKGUI = true;
		searchField.visible = ModuleManager.getModule(ClickGui.class).getSetting(1).asToggle().state;
		MHLogo.render(matrix);
		this.renderBackground(matrix);
		String watermark = "MacHack " + (ModuleManager.getModule(UI.class).getSetting(24).asToggle().state ? "\u00A7f" : "")  + MacHack.VERSION;
		textRenderer.drawWithShadow(matrix, watermark, 1, 1, ColorUtils.guiColour());
		if (ModuleManager.getModule(ClickGui.class).getSetting(2).asToggle().state) {
			textRenderer.drawWithShadow(matrix,
					"Current prefix is: \"" + Command.PREFIX + "\" (" + Command.PREFIX + "help)", 2, height - 20, ColorUtils.guiColour());
			textRenderer.drawWithShadow(matrix, "Use " + Command.PREFIX + "guireset to reset the gui", 2, height - 10,
					ColorUtils.guiColour());
		}
		if (ModuleManager.getModule(ClickGui.class).getSetting(1).asToggle().state) {
			searchField.setSuggestion(searchField.getText().isEmpty() ? "Search here" : "");

			Set<Module> seachMods = new HashSet<>();
			if (!searchField.getText().isEmpty()) {
				for (Module m : ModuleManager.getModules()) {
					if (m.getName().toLowerCase().contains(searchField.getText().toLowerCase().replace(" ", ""))) {
						seachMods.add(m);
					}
				}
			}

			for (Window w : windows) {
				if (w instanceof ModuleWindow) {
					((ModuleWindow) w).setSearchedModule(seachMods);
				}
			}
		}

		int len = (int) ModuleManager.getModule(ClickGui.class).getSetting(0).asSlider().getValue();

		for (Window w : windows) {
			if (w instanceof ClickGuiWindow) {
				if (w instanceof ModuleWindow) {
					((ModuleWindow) w).setLen(len);
				}

				((ClickGuiWindow) w).updateKeys(mX, mY, keyDown, lmDown, rmDown, lmHeld, mwScroll);
			}
		}

		super.render(matrix, mX, mY, float_1);

		for (Window w : windows) {
			if (!ModuleManager.getModule(ClickGui.class).getSetting(3).asToggle().state) {
				if (w instanceof ClickGuiWindow) {
					Triple<Integer, Integer, String> tooltip = ((ClickGuiWindow) w).getTooltip();
					if (tooltip != null) {
						int tooltipY = tooltip.getMiddle();

						String[] split = tooltip.getRight().split("\n", -1 /* Adding -1 makes it keep empty splits */);
						ArrayUtils.reverse(split);
						for (String s: split) {
							/* Match lines to end of words after it reaches 22 characters long */
							Matcher mat = Pattern.compile(".{1,22}\\b\\W*").matcher(s);

							List<String> lines = new ArrayList<>();

							while (mat.find())
								lines.add(mat.group().trim());

							if (lines.isEmpty())
								lines.add(s);

							int start = tooltipY - lines.size() * 10;
							for (int l = 0; l < lines.size(); l++) {
								textRenderer.drawWithShadow(matrix, lines.get(l), tooltip.getLeft() + 2, start + (l * 10), ColorUtils.guiColour());
							}

							tooltipY -= lines.size() * 10;
						}
					}
				}
			} else if (ModuleManager.getModule(ClickGui.class).getSetting(3).asToggle().state && !ModuleManager.getModule(ClickGui.class).getSetting(2).asToggle().state) {
				if (w instanceof ClickGuiWindow) {
					Triple<Integer, Integer, String> tooltip = ((ClickGuiWindow) w).getTooltip();
					if (tooltip != null) {
						textRenderer.drawWithShadow(matrix, tooltip.getRight(), 2, height - 11, ColorUtils.guiColour());
					}
				}
			} else if (ModuleManager.getModule(ClickGui.class).getSetting(3).asToggle().state && ModuleManager.getModule(ClickGui.class).getSetting(2).asToggle().state) {
				if (w instanceof ClickGuiWindow) {
					Triple<Integer, Integer, String> tooltip = ((ClickGuiWindow) w).getTooltip();
					if (tooltip != null) {
						textRenderer.drawWithShadow(matrix, tooltip.getRight(), 2, height - 30, ColorUtils.guiColour());
					}
				}
			}
		}

		lmDown = false;
		rmDown = false;
		keyDown = -1;
		mwScroll = 0;
	}

	public boolean mouseClicked(double double_1, double double_2, int int_1) {
		if (int_1 == 0) {
			lmDown = true;
			lmHeld = true;
		}
		else if (int_1 == 1)
			rmDown = true;

		for (Window w : windows) {
			if (double_1 > w.x1 && double_1 < w.x2 && double_2 > w.y1 && double_2 < w.y2 && !w.closed) {
				w.onMousePressed((int) double_1, (int) double_2);
				break;
			}
		}

		return super.mouseClicked(double_1, double_2, int_1);
	}

	public boolean mouseReleased(double double_1, double double_2, int int_1) {
		if (int_1 == 0)
			lmHeld = false;
		return super.mouseReleased(double_1, double_2, int_1);
	}

	public boolean keyPressed(int int_1, int int_2, int int_3) {
		keyDown = int_1;
		return super.keyPressed(int_1, int_2, int_3);
	}
	public boolean mouseScrolled(double double_1, double double_2, double double_3) {
		mwScroll = (int) double_3;
		return super.mouseScrolled(double_1, double_2, double_3);
	}
	public void resetGui() {
		int x = 30;
		for (Window m : windows) {
			m.x1 = x;
			m.y2 = 35;
			x += (int) ModuleManager.getModule(ClickGui.class).getSetting(0).asSlider().getValue() + 5;
		}
	}
}