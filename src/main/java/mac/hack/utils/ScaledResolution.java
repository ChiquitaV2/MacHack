//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package mac.hack.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.MathHelper;

public class ScaledResolution {
    private final double scaledWidthD;
    private final double scaledHeightD;
    private int scaledWidth;
    private int scaledHeight;
    private int scaleFactor;

    public ScaledResolution(MinecraftClient minecraftClient) {
        this.scaledWidth = minecraftClient.getWindow().getWidth();
        this.scaledHeight = minecraftClient.getWindow().getScaledHeight();
        this.scaleFactor = 1;
        boolean flag = minecraftClient.forcesUnicodeFont();
        int i = minecraftClient.options.guiScale;
        if (i == 0) {
            i = 1000;
        }

        while(this.scaleFactor < i && this.scaledWidth / (this.scaleFactor + 1) >= 320 && this.scaledHeight / (this.scaleFactor + 1) >= 240) {
            ++this.scaleFactor;
        }

        if (flag && this.scaleFactor % 2 != 0 && this.scaleFactor != 1) {
            --this.scaleFactor;
        }

        this.scaledWidthD = (double)this.scaledWidth / (double)this.scaleFactor;
        this.scaledHeightD = (double)this.scaledHeight / (double)this.scaleFactor;
        this.scaledWidth = MathHelper.ceil(this.scaledWidthD);
        this.scaledHeight = MathHelper.ceil(this.scaledHeightD);
    }

    public int getScaledWidth() {
        return this.scaledWidth;
    }

    public int getScaledHeight() {
        return this.scaledHeight;
    }

    public double getScaledWidth_double() {
        return this.scaledWidthD;
    }

    public double getScaledHeight_double() {
        return this.scaledHeightD;
    }

    public int getScaleFactor() {
        return this.scaleFactor;
    }
}
