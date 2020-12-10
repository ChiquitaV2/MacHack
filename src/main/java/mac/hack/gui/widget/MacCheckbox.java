package mac.hack.gui.widget;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.AbstractPressableButtonWidget;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import org.lwjgl.opengl.GL11;

public class MacCheckbox extends AbstractPressableButtonWidget {

	public boolean checked;

	public MacCheckbox(int int_1, int int_2, Text text, boolean pressed) {
		super(int_1, int_2, 10 + MinecraftClient.getInstance().textRenderer.getWidth(text), 10, text);
		checked = pressed;
	}

	public void onPress() {
		MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
		checked = !checked;
	}

	public void renderButton(MatrixStack matrix, int mouseX, int mouseY, float delta) {
		MinecraftClient minecraftClient_1 = MinecraftClient.getInstance();
		GL11.glDepthMask(true);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		TextRenderer textRenderer = minecraftClient_1.textRenderer;
		int color = mouseX > x && mouseX < x + 10 && mouseY > y && mouseY < y + 10 ? 0xffc0c0c0 : 0xffe0e0e0;
		fill(matrix, x, y, x + 11, y + 11, color);
		fill(matrix, x, y, x + 1, y + 11, 0xff303030);
		fill(matrix, x, y + 10, x + 11, y + 11, 0xffb0b0b0);
		fill(matrix, x, y, x + 10, y + 1, 0xff303030);
		fill(matrix, x + 10, y, x + 11, y + 11, 0xffb0b0b0);
		if (checked)
			textRenderer.draw(matrix, "\u2714", x + 2, y + 2, 0x000000); //fill(x + 3, y + 5, x + 8, y + 6, 0xff000000);
		drawStringWithShadow(matrix, textRenderer, getMessage().getString(), x + 15, y + 2, 0xC0C0C0);
	}
}