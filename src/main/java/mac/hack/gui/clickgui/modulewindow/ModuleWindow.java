package mac.hack.gui.clickgui.modulewindow;

import mac.hack.module.Module;
import mac.hack.module.ModuleManager;
import mac.hack.module.mods.ClickGui;
import mac.hack.setting.base.SettingBase;
import mac.hack.utils.ColorUtils;
import mac.hack.utils.RenderUtils;
import mac.hack.utils.Snow;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import org.apache.commons.lang3.tuple.Triple;
import org.lwjgl.glfw.GLFW;

import java.util.*;
import java.util.Map.Entry;

public class ModuleWindow extends ClickGuiWindow {

	public static boolean hiding;
	public List<Module> modList = new ArrayList<>();
	public LinkedHashMap<Module, Boolean> mods = new LinkedHashMap<>();

	private int len;

	private Set<Module> searchedModules;

	int MousePlayAnim = 0;

	int MousePlayAnim2 = 12;

	private ArrayList<Snow> _snowList = new ArrayList<Snow>();

	private Triple<Integer, Integer, String> tooltip = null;

	public ModuleWindow(List<Module> mods, int x1, int y1, int len, String title, ItemStack icon) {
		super(x1, y1, x1 + len, 0, title, icon);

		this.len = len;
		modList = mods;
		for (Module m : mods)
			this.mods.put(m, false);
		y2 = getHeight();
	}

	public void render(MatrixStack matrix, int mX, int mY) {
		super.render(matrix, mX, mY);

		TextRenderer textRend = mc.textRenderer;

		tooltip = null;
		int x = x1 + 1;
		int y = y1 + 13;
		x2 = x + len + 1;

		if (lmDown) {
			MousePlayAnim = 12;
		}

		if (rmDown) {
			MousePlayAnim2 = 0;
		}

		if (MousePlayAnim2 < 10) {
			MousePlayAnim2++;
			RenderUtils.DrawPolygon(mouseX, mouseY, MousePlayAnim2, 360, 0xffffffff);
		}

		if (MousePlayAnim > 0) {
			MousePlayAnim--;
			RenderUtils.DrawPolygon(mouseX, mouseY, MousePlayAnim, 360, 0xffffffff);
		}

		if (rmDown && mouseOver(x, y - 12, x + len, y)) {
			mc.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
			hiding = !hiding;
		}

		if (hiding) {
			y2 = y;
			return;
		}
		else {
			y2 = y + getHeight();
		}

		int curY = 0;
		for (Entry<Module, Boolean> m : new LinkedHashMap<>(mods).entrySet()) {
			if (m.getValue()) {
				fillGreySides(matrix, x, y + curY, x + len - 1, y + 12 + curY);
			}
			DrawableHelper.fill(matrix, x, y + curY, x + len, y + 12 + curY,
					mouseOver(x, y + curY, x + len, y + 12 + curY) ? ColorUtils.guiColour() + 0x00020202 - 0xcf000000 : 0x00000000);
			textRend.drawWithShadow(matrix, textRend.trimToWidth(m.getKey().getName(), len),
					x + 2, y + 2 + curY, m.getKey().isToggled() ? ColorUtils.guiColour() : 0xc0c0c0);

			//If they match: Module gets marked red
			if (searchedModules != null && searchedModules.contains(m.getKey()) && ModuleManager.getModule(ClickGui.class).getSetting(1).asToggle().state) {
				DrawableHelper.fill(matrix, m.getValue() ? x + 1 : x, y + curY + (m.getValue() ? 1 : 0),
						m.getValue() ? x + len - 3 : x + len, y + 12 + curY, 0x50ff0000);
			}

			/* Set which module settings show on */
			if (mouseOver(x, y + curY, x + len, y + 12 + curY)) {
				tooltip = Triple.of(x + len + 2, y + curY, m.getKey().getDesc());

				if (lmDown) m.getKey().toggle();
				if (rmDown) mods.replace(m.getKey(), !m.getValue());
				if (lmDown || rmDown)
					mc.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
			}

			curY += 12;

			/* draw settings */
			if (m.getValue()) {
				for (SettingBase s : m.getKey().getSettings()) {
					s.render(this, matrix, x, y + curY, len);

					if (!s.getDesc().isEmpty() && mouseOver(x, y + curY, x + len, y + s.getHeight(len) + curY)) {
						tooltip = s.getGuiDesc(this, x, y + curY, len);
					}

					fillGreySides(matrix, x, y + curY - 1, x + len - 1, y + curY + s.getHeight(len));

					curY += s.getHeight(len);
				}

				drawBindSetting(matrix, m.getKey(), keyDown, x, y + curY, textRend);
				curY += 12;
				//fill(x+len-1, y+(count*12), x+len, y+12+(count*12), 0x9f70fff0);
				drawHiddenSetting(matrix, m.getKey(), x, y + curY, textRend);
				curY += 12;
			}
		}
	}

	public void drawBindSetting(MatrixStack matrix, Module m, int key, int x, int y, TextRenderer textRend) {
		if (key >= 0 && mouseOver(x, y, x + len, y + 12))
			m.setKey((key != GLFW.GLFW_KEY_DELETE && key != GLFW.GLFW_KEY_ESCAPE && key != GLFW.GLFW_KEY_BACKSPACE) ? key : Module.KEY_UNBOUND);

		String name = m.getKey() < 0 ? "NONE" : InputUtil.fromKeyCode(m.getKey(), -1).getLocalizedText().getString();
		if (name == null)
			name = "KEY" + m.getKey();
		else if (name.isEmpty())
			name = "NONE";

		textRend.drawWithShadow(matrix, "Bind: " + name + (mouseOver(x, y, x + len, y + 12) ? "..." : ""), x + 2, y + 2,
				mouseOver(x, y, x + len, y + 12) ? 0xcfc3cf : 0xcfe0cf);
	}

	public void drawHiddenSetting(MatrixStack matrix, Module m, int x, int y, TextRenderer textRend) {
		if (lmDown && mouseOver(x, y, x + len, y + 12)) {
			mc.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
			m.setHidden(!m.isHidden());
		}
		textRend.drawWithShadow(matrix, "Hidden: " + m.isHidden(), x + 2, y + 2,
				mouseOver(x, y, x + len, y + 12) ? 0xcfc3cf : 0xcfe0cf);
	}

	public void fillReverseGrey(MatrixStack matrix, int x1, int y1, int x2, int y2) {
		DrawableHelper.fill(matrix, x1, y1, x1 + 1, y2 - 1, 0x90000000);
		DrawableHelper.fill(matrix, x1 + 1, y1, x2 - 1, y1 + 1, 0x90000000);
		DrawableHelper.fill(matrix, x1 + 1, y2 - 1, x2, y2, 0x90b0b0b0);
		DrawableHelper.fill(matrix, x2 - 1, y1 + 1, x2, y2 - 1, 0x90b0b0b0);
		DrawableHelper.fill(matrix, x1 + 1, y1 + 1, x2 - 1, y2 - 1, 0xff505059);
	}

	private void fillGreySides(MatrixStack matrix, int x1, int y1, int x2, int y2) {
		//DrawableHelper.fill(matrix, x1, y1, x1 + 1, y2 - 1, 0x90000000);
		//DrawableHelper.fill(matrix, x2 - 1, y1 + 1, x2, y2, 0x90b0b0b0);
	}

	protected void drawBar(MatrixStack matrix, int mX, int mY, TextRenderer textRend) {
		super.drawBar(matrix, mX, mY, textRend);
		textRend.draw(matrix, hiding ? "+" : "_", x2 - 11, y1 + (hiding ? 4 : 0), ColorUtils.textColor());
	}

	public Triple<Integer, Integer, String> getTooltip() {
		return tooltip;
	}

	public void setSearchedModule(Set<Module> mods) {
		searchedModules = mods;
	}

	public void setLen(int len) {
		this.len = len;
	}

	public int getHeight() {
		int h = 1;
		for (Entry<Module, Boolean> e : mods.entrySet()) {
			h += 12;
			if (e.getValue()) {
				for (SettingBase s : e.getKey().getSettings()) {
					h += s.getHeight(len);
				}
				h += 24;
			}
		}
		return h;
	}

}