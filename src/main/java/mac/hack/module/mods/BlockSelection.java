package mac.hack.module.mods;

import com.google.common.eventbus.Subscribe;
import mac.hack.event.events.EventWorldRender;
import mac.hack.module.Category;
import mac.hack.module.Module;
import mac.hack.setting.base.SettingSlider;
import mac.hack.utils.RenderUtils;
import mac.hack.utils.WorldRenderUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.RaycastContext;

public class BlockSelection extends Module {
    public BlockSelection()
    {
        super("Block Selection", KEY_UNBOUND, Category.RENDER, "renders your selection better",
                new SettingSlider("Red: ", 0.0D, 255.0D, 255.0D, 0),
                new SettingSlider("Green: ", 0.0D, 255.0D, 255.0D, 0),
                new SettingSlider("Blue: ", 0.0D, 255.0D, 255.0D, 0));
    }

    @Subscribe
    public void onRenderWorld(EventWorldRender event) {
        if (mc.crosshairTarget == null || !(mc.crosshairTarget instanceof BlockHitResult)) return;

        BlockPos pos = ((BlockHitResult) mc.crosshairTarget).getBlockPos();
        BlockState state = mc.world.getBlockState(pos);
        float r =  (float) (this.getSetting(0).asSlider().getValue() / 255.0D);
        float g =  (float) (this.getSetting(1).asSlider().getValue() / 255.0D);
        float b =  (float) (this.getSetting(2).asSlider().getValue() / 255.0D);

            BlockPos blockpos = ((BlockHitResult) mc.crosshairTarget).getBlockPos();
            BlockState blockState = mc.world.getBlockState(blockpos);
            if (blockState.getMaterial() != Material.AIR && mc.world.getWorldBorder().contains(blockpos)) {
                RenderUtils.drawOutlineBox(blockpos, r, g, b, 0.6F);
            }
    }
}
