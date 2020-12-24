package mac.hack.utils;


import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.math.BlockPos;

import java.util.Arrays;

import static org.lwjgl.opengl.GL11.*;


public class TessellatorUtils extends Tessellator {
    public static TessellatorUtils INSTANCE = new TessellatorUtils();

    public TessellatorUtils() {
        super(0x200000);
    }

    public static void prepare(String mode_requested) {
        int mode = 0;

        if (mode_requested.equalsIgnoreCase("quads")) {
            mode = GL_QUADS;
        } else if (mode_requested.equalsIgnoreCase("lines")) {
            mode = GL_LINES;
        }

        prepareGL();
        begin(mode);
    }

    public static void prepareGL() {
//        GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        //GLStateManager.SourceFactor and GLStateManager.DestFactor don't exist anymore
        GlStateManager.blendFuncSeparate(770, 771, 1, 0);
        GlStateManager.lineWidth(1.5F);
        GlStateManager.disableTexture();
        GlStateManager.depthMask(false);
        GlStateManager.enableBlend();
        GlStateManager.disableDepthTest();
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        GlStateManager.enableAlphaTest();
        GlStateManager.color4f(1f,1f,1f,1.0f);
    }

    public static void begin(int mode) {
        INSTANCE.getBuffer().begin(mode, VertexFormats.POSITION_COLOR);
    }


    public static void release() {
        render();
        releaseGL();
    }

    public static void render() {
        INSTANCE.draw();
    }

    public static void releaseGL() {
        GlStateManager.enableCull();
        GlStateManager.depthMask(true);
        GlStateManager.enableTexture();
        GlStateManager.enableBlend();
        GlStateManager.enableDepthTest();
    }


    public static void draw_cube(BlockPos blockPos, int argb, String sides) {
        final int a = (argb >>> 24) & 0xFF;
        final int r = (argb >>> 16) & 0xFF;
        final int g = (argb >>> 8) & 0xFF;
        final int b = argb & 0xFF;
        draw_cube(blockPos, r, g, b, a, sides);
    }

    public static void draw_cube(float x, float y, float z, int argb, String sides) {
        final int a = (argb >>> 24) & 0xFF;
        final int r = (argb >>> 16) & 0xFF;
        final int g = (argb >>> 8) & 0xFF;
        final int b = argb & 0xFF;
        draw_cube(INSTANCE.getBuffer(), x, y, z, 1, 1, 1, r, g, b, a, sides);
    }

    public static void draw_cube(BlockPos blockPos, int r, int g, int b, int a, String sides) {
        draw_cube(INSTANCE.getBuffer(), blockPos.getX(), blockPos.getY(), blockPos.getZ(), 1, 1, 1, r, g, b, a, sides);
    }

    public static void draw_cube_line(BlockPos blockPos, int argb, String sides) {
        final int a = (argb >>> 24) & 0xFF;
        final int r = (argb >>> 16) & 0xFF;
        final int g = (argb >>> 8) & 0xFF;
        final int b = argb & 0xFF;
        draw_cube_line(blockPos, r, g, b, a, sides);
    }

    public static void draw_cube_line(float x, float y, float z, int argb, String sides) {
        final int a = (argb >>> 24) & 0xFF;
        final int r = (argb >>> 16) & 0xFF;
        final int g = (argb >>> 8) & 0xFF;
        final int b = argb & 0xFF;
        draw_cube_line(INSTANCE.getBuffer(), x, y, z, 1, .5645f, 1, r, g, b, a, sides);
    }
    public static void draw_cube_line_full(float x, float y, float z, int argb, String sides) {
        final int a = (argb >>> 24) & 0xFF;
        final int r = (argb >>> 16) & 0xFF;
        final int g = (argb >>> 8) & 0xFF;
        final int b = argb & 0xFF;
        draw_cube_line(INSTANCE.getBuffer(), x, y, z, 1, 1, 1, r, g, b, a, sides);
    }

    public static void draw_cube_line(BlockPos blockPos, int r, int g, int b, int a, String sides) {
        draw_cube_line(INSTANCE.getBuffer(), blockPos.getX(), blockPos.getY(), blockPos.getZ(), 1, 1, 1, r, g, b, a, sides);
    }

    public static BufferBuilder get_buffer_build() {
        return INSTANCE.getBuffer();
    }

    public static void draw_cube(final BufferBuilder buffer, float x, float y, float z, float w, float h, float d, int r, int g, int b, int a, String sides) {
        if (((boolean) Arrays.asList(sides.split("-")).contains("down")) || sides.equalsIgnoreCase("all")) {
            buffer.vertex(x + w, y, z).color(r, g, b, a).next();
            buffer.vertex(x + w, y, z + d).color(r, g, b, a).next();
            buffer.vertex(x, y, z + d).color(r, g, b, a).next();
            buffer.vertex(x, y, z).color(r, g, b, a).next();
        }

        if (((boolean) Arrays.asList(sides.split("-")).contains("up")) || sides.equalsIgnoreCase("all")) {
            buffer.vertex(x + w, y + h, z).color(r, g, b, a).next();
            buffer.vertex(x, y + h, z).color(r, g, b, a).next();
            buffer.vertex(x, y + h, z + d).color(r, g, b, a).next();
            buffer.vertex(x + w, y + h, z + d).color(r, g, b, a).next();
        }

        if (((boolean) Arrays.asList(sides.split("-")).contains("north")) || sides.equalsIgnoreCase("all")) {
            buffer.vertex(x + w, y, z).color(r, g, b, a).next();
            buffer.vertex(x, y, z).color(r, g, b, a).next();
            buffer.vertex(x, y + h, z).color(r, g, b, a).next();
            buffer.vertex(x + w, y + h, z).color(r, g, b, a).next();
        }

        if (((boolean) Arrays.asList(sides.split("-")).contains("south")) || sides.equalsIgnoreCase("all")) {
            buffer.vertex(x, y, z + d).color(r, g, b, a).next();
            buffer.vertex(x + w, y, z + d).color(r, g, b, a).next();
            buffer.vertex(x + w, y + h, z + d).color(r, g, b, a).next();
            buffer.vertex(x, y + h, z + d).color(r, g, b, a).next();
        }

        if (((boolean) Arrays.asList(sides.split("-")).contains("south")) || sides.equalsIgnoreCase("all")) {
            buffer.vertex(x, y, z).color(r, g, b, a).next();
            buffer.vertex(x, y, z + d).color(r, g, b, a).next();
            buffer.vertex(x, y + h, z + d).color(r, g, b, a).next();
            buffer.vertex(x, y + h, z).color(r, g, b, a).next();
        }

        if (((boolean) Arrays.asList(sides.split("-")).contains("south")) || sides.equalsIgnoreCase("all")) {
            buffer.vertex(x + w, y, z + d).color(r, g, b, a).next();
            buffer.vertex(x + w, y, z).color(r, g, b, a).next();
            buffer.vertex(x + w, y + h, z).color(r, g, b, a).next();
            buffer.vertex(x + w, y + h, z + d).color(r, g, b, a).next();
        }
    }

    public static void draw_cube_line(BufferBuilder buffer, float x, float y, float z, float w, float h, float d, int r, int g, int b, int a, String sides) {
        if (((boolean) Arrays.asList(sides.split("-")).contains("downwest")) || sides.equalsIgnoreCase("all")) {
            buffer.vertex(x, y, z).color(r, g, b, a).next();
            buffer.vertex(x, y, z + d).color(r, g, b, a).next();
        }

        if (((boolean) Arrays.asList(sides.split("-")).contains("upwest")) || sides.equalsIgnoreCase("all")) {
            buffer.vertex(x, y + h, z).color(r, g, b, a).next();
            buffer.vertex(x, y + h, z + d).color(r, g, b, a).next();
        }

        if (((boolean) Arrays.asList(sides.split("-")).contains("downeast")) || sides.equalsIgnoreCase("all")) {
            buffer.vertex(x + w, y, z).color(r, g, b, a).next();
            buffer.vertex(x + w, y, z + d).color(r, g, b, a).next();
        }

        if (((boolean) Arrays.asList(sides.split("-")).contains("upeast")) || sides.equalsIgnoreCase("all")) {
            buffer.vertex(x + w, y + h, z).color(r, g, b, a).next();
            buffer.vertex(x + w, y + h, z + d).color(r, g, b, a).next();
        }

        if (((boolean) Arrays.asList(sides.split("-")).contains("downnorth")) || sides.equalsIgnoreCase("all")) {
            buffer.vertex(x, y, z).color(r, g, b, a).next();
            buffer.vertex(x + w, y, z).color(r, g, b, a).next();
        }

        if (((boolean) Arrays.asList(sides.split("-")).contains("upnorth")) || sides.equalsIgnoreCase("all")) {
            buffer.vertex(x, y + h, z).color(r, g, b, a).next();
            buffer.vertex(x + w, y + h, z).color(r, g, b, a).next();
        }

        if (((boolean) Arrays.asList(sides.split("-")).contains("downsouth")) || sides.equalsIgnoreCase("all")) {
            buffer.vertex(x, y, z + d).color(r, g, b, a).next();
            buffer.vertex(x + w, y, z + d).color(r, g, b, a).next();
        }

        if (((boolean) Arrays.asList(sides.split("-")).contains("upsouth")) || sides.equalsIgnoreCase("all")) {
            buffer.vertex(x, y + h, z + d).color(r, g, b, a).next();
            buffer.vertex(x + w, y + h, z + d).color(r, g, b, a).next();
        }

        if (((boolean) Arrays.asList(sides.split("-")).contains("nortwest")) || sides.equalsIgnoreCase("all")) {
            buffer.vertex(x, y, z).color(r, g, b, a).next();
            buffer.vertex(x, y + h, z).color(r, g, b, a).next();
        }

        if (((boolean) Arrays.asList(sides.split("-")).contains("norteast")) || sides.equalsIgnoreCase("all")) {
            buffer.vertex(x + w, y, z).color(r, g, b, a).next();
            buffer.vertex(x + w, y + h, z).color(r, g, b, a).next();
        }

        if (((boolean) Arrays.asList(sides.split("-")).contains("southweast")) || sides.equalsIgnoreCase("all")) {
            buffer.vertex(x, y, z + d).color(r, g, b, a).next();
            buffer.vertex(x, y + h, z + d).color(r, g, b, a).next();
        }

        if (((boolean) Arrays.asList(sides.split("-")).contains("southeast")) || sides.equalsIgnoreCase("all")) {
            buffer.vertex(x + w, y, z + d).color(r, g, b, a).next();
            buffer.vertex(x + w, y + h, z + d).color(r, g, b, a).next();
        }
    }

}