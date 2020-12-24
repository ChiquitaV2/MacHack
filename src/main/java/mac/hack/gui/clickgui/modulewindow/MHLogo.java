package mac.hack.gui.clickgui.modulewindow;

import mac.hack.module.ModuleManager;
import mac.hack.module.mods.ClickGui;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.lwjgl.opengl.GL11;

public class MHLogo {
    private static final Identifier identifier = new Identifier("machack", "machack3.png");

    public static void render(MatrixStack matrixStack) {
        if (ModuleManager.getModule(ClickGui.class).getSetting(6).asToggle().state) {
            GL11.glColor4f(2, 2, 2, 1);
            GL11.glEnable(GL11.GL_BLEND);
            MinecraftClient.getInstance().getTextureManager().bindTexture(identifier);
            DrawableHelper.drawTexture(matrixStack, 25, 230, 0, 0, 200, 200, 200, 200);
        }
    }
}
