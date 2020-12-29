package mac.hack.module.mods;

import com.google.common.eventbus.Subscribe;
import mac.hack.event.events.EventTick;
import mac.hack.event.events.EventWorldRender;
import mac.hack.module.Category;
import mac.hack.module.Module;
import mac.hack.setting.base.SettingSlider;
import mac.hack.utils.RenderUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;


public class CityESP extends Module
{
    private final List<BlockPos> poses = new ArrayList<>();
    public Vec3d prevPos;
    private double[] rPos;

    public CityESP()
    {
        super("CityESP", KEY_UNBOUND, Category.RENDER, "Renders the blocks that if broken, allow you to city someone",
                new SettingSlider("update: ", 0.0D, 20.0D, 1.0D, 0),
                new SettingSlider("Red: ", 0.0D, 255.0D, 255.0D, 0),
                new SettingSlider("Green: ", 0.0D, 255.0D, 10.0D, 0),
                new SettingSlider("Blue: ", 0.0D, 255.0D, 0.0D, 0));
    }

    @Subscribe
    public void onTick(EventTick event)
    {
        if (mc.player.age % 1 == 0 && this.isToggled())
        {
            this.update((int) this.getSettings().get(0).asSlider().getValue());
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
            for (int i = 0; i < 9; i++);
        }

        GL11.glEnable(2929);
        GL11.glEnable(3553);
        GL11.glDepthMask(true);
        GL11.glDisable(3042);
        GL11.glPopMatrix();
    }

    public void update(int range)
    {
        this.poses.clear();
        BlockPos player = mc.player.getBlockPos();
        this.prevPos = mc.player.getPos();
        List<AbstractClientPlayerEntity> playerList = mc.world.getPlayers();
        for (PlayerEntity target : playerList) {
            Vec3d vec = target.getPos();
            BlockPos targetX = new BlockPos(vec.add(1, 0, 0));
            BlockPos targetXMinus = new BlockPos(vec.add(-1, 0, 0));
            BlockPos targetZ = new BlockPos(vec.add(0, 0, 1));
            BlockPos targetZMinus = new BlockPos(vec.add(0, 0, -1));
            if(canBreak(targetX)) {
                this.poses.add(new BlockPos(targetX));
            }
            if(!canBreak(targetX) && canBreak(targetXMinus)) {
                this.poses.add(new BlockPos(targetXMinus));
            }
            if(!canBreak(targetX) && !canBreak(targetXMinus) && canBreak(targetZ)) {
                this.poses.add(new BlockPos(targetZ));
            }
            if(!canBreak(targetX) && !canBreak(targetXMinus) && !canBreak(targetZ) && canBreak(targetZMinus)) {
                this.poses.add(new BlockPos(targetZMinus));
            }
        }
    }
    private boolean canBreak(BlockPos pos) {
        BlockState state = mc.world.getBlockState(pos);
        return  !(state.getBlock() == Blocks.AIR || state.getBlock() == Blocks.WATER || state.getBlock() == Blocks.LAVA || state.getBlock() == Blocks.BEDROCK || state.getBlock() == Blocks.VOID_AIR || state.getBlock() == Blocks.CAVE_AIR);
    }

    public void drawFilledBlockBox(BlockPos blockPos, float r, float g, float b, float a)
    {
        double x = blockPos.getX();
        double y = blockPos.getY();
        double z = blockPos.getZ();

        float or = (float) (this.getSettings().get(1).asSlider().getValue() / 255.0D);
        float og = (float) (this.getSettings().get(2).asSlider().getValue() / 255.0D);
        float ob = (float) (this.getSettings().get(3).asSlider().getValue() / 255.0D);
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
    }

    public void onDisable () {
        this.poses.clear();
    }
}
