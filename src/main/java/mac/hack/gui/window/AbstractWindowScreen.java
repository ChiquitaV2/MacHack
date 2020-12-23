package mac.hack.gui.window;

import mac.hack.module.ModuleManager;
import mac.hack.module.mods.ClickGui;
import mac.hack.utils.Snow;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class AbstractWindowScreen extends Screen {

	public List<Window> windows = new ArrayList<>();

	private ArrayList<Snow> _snowList = new ArrayList<Snow>();

	public AbstractWindowScreen(Text text_1) {
		super(text_1);
	}

	public void render(MatrixStack matrix, int mouseX, int mouseY, float delta) {
		boolean close = true;
		int noneSelected = -1;
		int selected = -1;
		int count = 0;
		for (Window w : windows) {
			if (!w.closed) {
				close = false;
				if (!w.selected) {
					onRenderWindow(matrix, count, mouseX, mouseY);
				} else {
					selected = count;
				}

				if (noneSelected >= -1) noneSelected = count;
			}

			if (w.selected && !w.closed) {
				noneSelected = -2;
			}
			count++;
		}

		if (selected >= 0) onRenderWindow(matrix, selected, mouseX, mouseY);
		if (noneSelected >= 0) windows.get(noneSelected).selected = true;
		if (close) this.onClose();

		if (!_snowList.isEmpty() && ModuleManager.getModule(ClickGui.class).getSetting(5).asToggle().state)
		{
			_snowList.forEach(Snow::Update);

			if (!ModuleManager.getModule(ClickGui.class).isDrawn()) {
				_snowList.clear();
			}

			if (_snowList.size() >= 120) {
				for (int i = 120; i < _snowList.size(); i++) {
					_snowList.remove(i);
				}
			}
		}

		Random random = new Random();

		for (int i = 0; i < 100; ++i)
		{
			for (int y = 0; y < 3; ++y)
			{
				Snow snow = new Snow(25 * i, y * -50, random.nextInt(3) + 1, random.nextInt(2)+1);
				_snowList.add(snow);
			}
		}

		super.render(matrix, mouseX, mouseY, delta);
	}

	public void onRenderWindow(MatrixStack matrix, int window, int mX, int mY) {
		if (!windows.get(window).closed) {
			windows.get(window).render(matrix, mX, mY);
		}
	}

	public void drawButton(MatrixStack matrix, String text, int x1, int y1, int x2, int y2) {
		DrawableHelper.fill(matrix, x1, y1, x2 - 1, y2 - 1, 0xffb0b0b0);
		DrawableHelper.fill(matrix, x1 + 1, y1 + 1, x2, y2, 0xff000000);
		DrawableHelper.fill(matrix, x1 + 1, y1 + 1, x2 - 1, y2 - 1, 0xff858585);
		drawCenteredString(matrix, textRenderer, text, x1 + (x2 - x1) / 2, y1 + (y2 - y1) / 2 - 4, -1);
	}

	public void selectWindow(int window) {
		int count = 0;
		for (Window w : windows) {
			if (w.selected) {
				w.inactiveTime = 2;
			}

			w.selected = (count == window);
			count++;
		}
	}

	public boolean mouseClicked(double double_1, double double_2, int int_1) {
		/* Handle what window will be selected when clicking */
		int count = 0;
		int nextSelected = -1;
		for (Window w : windows) {
			if (w.selected) {
				w.onMousePressed((int) double_1, (int) double_2);
			}

			if (w.shouldClose((int) double_1, (int) double_2)) w.closed = true;

			if (w.inactiveTime <= 0 && double_1 > w.x1 && double_1 < w.x2 && double_2 > w.y1 && double_2 < w.y2 && !w.closed) {
				if (w.selected) {
					nextSelected = -1;
					break;
				} else {
					nextSelected = count;
				}
			}
			count++;
		}

		if (nextSelected >= 0) {
			for (Window w : windows) w.selected = false;
			windows.get(nextSelected).selected = true;
		}

		return super.mouseClicked(double_1, double_2, int_1);
	}

	public boolean mouseReleased(double double_1, double double_2, int int_1) {
		for (Window w : windows) {
			w.onMouseReleased((int) double_1, (int) double_2);
		}

		return super.mouseReleased(double_1, double_2, int_1);
	}

}