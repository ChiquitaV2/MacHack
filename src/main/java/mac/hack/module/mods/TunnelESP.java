package mac.hack.module.mods;

import mac.hack.event.events.EventTick;
import mac.hack.event.events.EventWorldRender;
import mac.hack.module.Category;
import mac.hack.module.Module;
import mac.hack.setting.base.SettingMode;
import mac.hack.setting.base.SettingSlider;
import mac.hack.utils.RenderUtils;
import com.google.common.eventbus.Subscribe;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;
import java.util.ArrayList;
import java.util.List;
public class TunnelESP extends Module
{
    private final List<BlockPos> poses = new ArrayList<>();
    public Vec3d prevPos;
    private double[] rPos;
    public TunnelESP()
    {
        super("TunnelESP", KEY_UNBOUND, Category.RENDER, "ESP for tunnels off the highway",
                new SettingSlider("Range", 0, 50, 20, 0),
                new SettingSlider("R: ", 0.0D, 255.0D, 50.0D, 0),
                new SettingSlider("G: ", 0.0D, 255.0D, 255.0D, 0),
                new SettingSlider("B: ", 0.0D, 255.0D, 50.0D, 0),
                new SettingMode("Mode", "Flat", "Boxes"),
                new SettingSlider("Tick Delay", 1, 20, 5, 0));
    }
    @Subscribe
    public void onTick(EventTick event)
    {
        if (mc.player.age % (int) this.getSettings().get(5).asSlider().getValue() == 0 && this.isToggled())
        {
            this.update((int) this.getSettings().get(0).asSlider().getValue());
        }
    }
    public void update(int range)
    {
        this.poses.clear();
        BlockPos player = mc.player.getBlockPos();
        this.prevPos = mc.player.getPos();
        for (int y = -Math.min(range, player.getY()); y < Math.min(range, 255 - player.getY()); ++y)
        {
            for (int x = -range; x < range; ++x)
            {
                for (int z = -range; z < range; ++z)
                {
                    BlockPos pos = player.add(x, y, z);
                    if (
                            (this.mc.world.getBlockState(pos).getBlock() == Blocks.AIR)
                                    && (this.mc.world.getBlockState(pos.up(1)).getBlock() == Blocks.AIR)
                                    && !(this.mc.world.getBlockState(pos.down(1)).getBlock() == Blocks.AIR)
                                    && !(this.mc.world.getBlockState(pos.up(2)).getBlock() == Blocks.AIR)
                                    && !(this.mc.world.getBlockState(pos.north(1)).getBlock() == Blocks.AIR)
                                    && !(this.mc.world.getBlockState(pos.south(1)).getBlock() == Blocks.AIR)
                                    && !(this.mc.world.getBlockState(pos.up(1).north(1)).getBlock() == Blocks.AIR)
                                    && !(this.mc.world.getBlockState(pos.up(1).south(1)).getBlock() == Blocks.AIR)
                                    && (this.mc.world.getBlockState(pos.west(1)).getBlock() == Blocks.AIR)
                                    && (this.mc.world.getBlockState(pos.west(1).up(1)).getBlock() == Blocks.AIR)
                                    && !(this.mc.world.getBlockState(pos.west(1).down(1)).getBlock() == Blocks.AIR)
                                    && !(this.mc.world.getBlockState(pos.west(1).up(2)).getBlock() == Blocks.AIR)
                                    && !(this.mc.world.getBlockState(pos.west(1).north(1)).getBlock() == Blocks.AIR)
                                    && !(this.mc.world.getBlockState(pos.west(1).south(1)).getBlock() == Blocks.AIR)
                                    && !(this.mc.world.getBlockState(pos.west(1).up(1).north(1)).getBlock() == Blocks.AIR)
                                    && !(this.mc.world.getBlockState(pos.west(1).up(1).south(1)).getBlock() == Blocks.AIR)
                                    ||
                                    (this.mc.world.getBlockState(pos).getBlock() == Blocks.AIR)
                                            && (this.mc.world.getBlockState(pos.up(1)).getBlock() == Blocks.AIR)
                                            && !(this.mc.world.getBlockState(pos.down(1)).getBlock() == Blocks.AIR)
                                            && !(this.mc.world.getBlockState(pos.up(2)).getBlock() == Blocks.AIR)
                                            && !(this.mc.world.getBlockState(pos.west(1)).getBlock() == Blocks.AIR)
                                            && !(this.mc.world.getBlockState(pos.east(1)).getBlock() == Blocks.AIR)
                                            && !(this.mc.world.getBlockState(pos.up(1).west(1)).getBlock() == Blocks.AIR)
                                            && !(this.mc.world.getBlockState(pos.up(1).east(1)).getBlock() == Blocks.AIR)
                                            && (this.mc.world.getBlockState(pos.north(1)).getBlock() == Blocks.AIR)
                                            && (this.mc.world.getBlockState(pos.north(1).up(1)).getBlock() == Blocks.AIR)
                                            && !(this.mc.world.getBlockState(pos.north(1).down(1)).getBlock() == Blocks.AIR)
                                            && !(this.mc.world.getBlockState(pos.north(1).up(2)).getBlock() == Blocks.AIR)
                                            && !(this.mc.world.getBlockState(pos.north(1).west(1)).getBlock() == Blocks.AIR)
                                            && !(this.mc.world.getBlockState(pos.north(1).east(1)).getBlock() == Blocks.AIR)
                                            && !(this.mc.world.getBlockState(pos.north(1).up(1).west(1)).getBlock() == Blocks.AIR)
                                            && !(this.mc.world.getBlockState(pos.north(1).up(1).east(1)).getBlock() == Blocks.AIR)
                    )
                    {
                        this.poses.add(pos);
                    }
                }
            }
        }
    }
    @Subscribe
    public void onRender(EventWorldRender event) {
        GL11.glPushMatrix();
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glDisable(2929);
        GL11.glDepthMask(false);
        GL11.glLineWidth(2.0F);
        float blue = (float) (System.currentTimeMillis() / 10L % 512L) / 255.0F;
        float red = (float) (System.currentTimeMillis() / 16L % 512L) / 255.0F;
        if (blue > 1.0F)
        {
            blue = 1.0F - blue;
        }
        if (red > 1.0F)
        {
            red = 1.0F - red;
        }
        for (BlockPos p : this.poses)
        {
            this.drawFilledBlockBox(p, red, 0.7F, blue, 0.25F);
        }
        GL11.glEnable(2929);
        GL11.glEnable(3553);
        GL11.glDepthMask(true);
        GL11.glDisable(3042);
        GL11.glPopMatrix();
    }
    public void drawFilledBlockBox(BlockPos blockPos, float r, float g, float b, float a)
    {
        double x = (double) blockPos.getX();
        double y = (double) blockPos.getY();
        double z = (double) blockPos.getZ();
        double y2 = (double) blockPos.up(1).getY();
        float or = (float) (this.getSettings().get(1).asSlider().getValue() / 255.0D);
        float og = (float) (this.getSettings().get(2).asSlider().getValue() / 255.0D);
        float ob = (float) (this.getSettings().get(3).asSlider().getValue() / 255.0D);
        if (getSettings().get(4).asMode().mode == 0) {
            RenderUtils.drawFilledBox(new Box(x, y, z, x + 1.0D, y, z + 1.0D), or, og, ob, a);
            RenderUtils.drawFilledBox(new Box(x, y, z, x + 1.0D, y, z + 1.0D), or, og, ob, a * 1.5F);
        } else {
            RenderUtils.drawFilledBox(new Box(x, y, z, x + 1.0D, y + 1.0D, z), or, og, ob, a);
            RenderUtils.drawFilledBox(new Box(x, y, z, x + 1.0D, y + 1.0D, z), or, og, ob, a * 1.5F);
            RenderUtils.drawFilledBox(new Box(x, y, z, x, y + 1.0D, z + 1.0D), or, og, ob, a);
            RenderUtils.drawFilledBox(new Box(x, y, z, x, y + 1.0D, z + 1.0D), or, og, ob, a * 1.5F);
            RenderUtils.drawFilledBox(new Box(x + 1.0D, y, z, x + 1.0D, y + 1.0D, z + 1.0D), or, og, ob, a);
            RenderUtils.drawFilledBox(new Box(x + 1.0D, y, z, x + 1.0D, y + 1.0D, z + 1.0D), or, og, ob, a * 1.5F);
            RenderUtils.drawFilledBox(new Box(x, y, z + 1.0D, x + 1.0D, y + 1.0D, z + 1.0D), or, og, ob, a);
            RenderUtils.drawFilledBox(new Box(x, y, z + 1.0D, x + 1.0D, y + 1.0D, z + 1.0D), or, og, ob, a * 1.5F);
            RenderUtils.drawFilledBox(new Box(x, y, z, x + 1.0D, y, z + 1.0D), or, og, ob, a);
            RenderUtils.drawFilledBox(new Box(x, y, z, x + 1.0D, y, z + 1.0D), or, og, ob, a * 1.5F);
            RenderUtils.drawFilledBox(new Box(x, y + 1.0D, z, x + 1.0D, y + 1.0D, z + 1.0D), or, og, ob, a);
            RenderUtils.drawFilledBox(new Box(x, y + 1.0D, z, x + 1.0D, y + 1.0D, z + 1.0D), or, og, ob, a * 1.5F);
            RenderUtils.drawFilledBox(new Box(x, y2, z, x + 1.0D, y2 + 1.0D, z), or, og, ob, a);
            RenderUtils.drawFilledBox(new Box(x, y2, z, x + 1.0D, y2 + 1.0D, z), or, og, ob, a * 1.5F);
            RenderUtils.drawFilledBox(new Box(x, y2, z, x, y2 + 1.0D, z + 1.0D), or, og, ob, a);
            RenderUtils.drawFilledBox(new Box(x, y2, z, x, y2 + 1.0D, z + 1.0D), or, og, ob, a * 1.5F);
            RenderUtils.drawFilledBox(new Box(x + 1.0D, y2, z, x + 1.0D, y2 + 1.0D, z + 1.0D), or, og, ob, a);
            RenderUtils.drawFilledBox(new Box(x + 1.0D, y2, z, x + 1.0D, y2 + 1.0D, z + 1.0D), or, og, ob, a * 1.5F);
            RenderUtils.drawFilledBox(new Box(x, y2, z + 1.0D, x + 1.0D, y2 + 1.0D, z + 1.0D), or, og, ob, a);
            RenderUtils.drawFilledBox(new Box(x, y2, z + 1.0D, x + 1.0D, y2 + 1.0D, z + 1.0D), or, og, ob, a * 1.5F);
            RenderUtils.drawFilledBox(new Box(x, y2, z, x + 1.0D, y2, z + 1.0D), or, og, ob, a);
            RenderUtils.drawFilledBox(new Box(x, y2, z, x + 1.0D, y2, z + 1.0D), or, og, ob, a * 1.5F);
            RenderUtils.drawFilledBox(new Box(x, y2 + 1.0D, z, x + 1.0D, y2 + 1.0D, z + 1.0D), or, og, ob, a);
            RenderUtils.drawFilledBox(new Box(x, y2 + 1.0D, z, x + 1.0D, y2 + 1.0D, z + 1.0D), or, og, ob, a * 1.5F);
        }
    }
    public void onDisable () {
        this.poses.clear();
    }
}