package mac.hack.module.mods;

import mac.hack.event.events.EventLoadChunk;
import mac.hack.event.events.EventReadPacket;
import mac.hack.event.events.EventUnloadChunk;
import mac.hack.event.events.EventWorldRender;
import mac.hack.module.Category;
import mac.hack.module.Module;
import mac.hack.setting.base.SettingSlider;
import mac.hack.setting.base.SettingToggle;
import mac.hack.utils.MacLogger;
import mac.hack.utils.RenderUtils;
import mac.hack.utils.file.MacFileHelper;
import com.google.common.eventbus.Subscribe;
import mac.hack.utils.file.MacFileMang;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.network.packet.s2c.play.ChunkDeltaUpdateS2CPacket;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.dimension.DimensionType;
import org.lwjgl.opengl.GL11;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;

public class SearchESP extends Module
{
    private final HashMap<DimensionType, ArrayBlockingQueue<BlockPos>> blocks = new HashMap<DimensionType, ArrayBlockingQueue<BlockPos>>();
    // Man, race condition is killing me wtf
    public Vec3d prevPos;
    private double[] rPos;
    private final Stack<WorldChunk> chunkStack = new Stack<>(); // Lol stacks is so cool
    private boolean running;

    private final Set<Block> highlightBlocks = new HashSet<>();


    public SearchESP()
    {
        super("SearchESP", KEY_UNBOUND, Category.RENDER, "ESP for blocks",
                new SettingSlider("R: ", 0.0D, 255.0D, 115.0D, 0),
                new SettingSlider("G: ", 0.0D, 255.0D, 0.0D, 0),
                new SettingSlider("B: ", 0.0D, 255.0D, 255.0D, 0),
                new SettingToggle("Debug", false)
        );
        new Thread(this::chunkyBoi).start();
    }

    public void setHighlight(Block... blocks) {
        Collections.addAll(this.highlightBlocks, blocks);
    }

    private boolean shown(BlockPos pos, DimensionType d) {
        ArrayBlockingQueue<BlockPos> booga = blocks.get(d);
        if (booga == null) {
            blocks.put(d, new ArrayBlockingQueue<BlockPos>(65455));
            return shown(pos, d);
        }
        for(BlockPos p : blocks.get(d)) {
            if (p.equals(pos))
                return true;
        }
        return false;
    }



    private void chunkyBoi() {
        while (true) {
            if (!chunkStack.isEmpty()) {
                WorldChunk chunk = chunkStack.pop();
                if (chunk == null)
                    continue;

                ChunkPos cPos = chunk.getPos();
                for (int i = 0; i < 16; i++) {
                    for (int j = 0; j < 16; j++) {
                        for (int k = 0; k < 255; k++) {
                            BlockPos pos = new BlockPos(cPos.x * 16 + i, k, cPos.z * 16 + j);
                            BlockState state = chunk.getBlockState(pos);
                            if (this.highlightBlocks.contains(state.getBlock()) && !shown(pos, mc.world.getDimension()))
                                blocks.get(mc.world.getDimension()).add(pos);
                        }
                    }
                }
            }
        }
    }

    @Subscribe
    public void onPacket(EventReadPacket e) {
        if (!isToggled()) return;
        if (e.getPacket() instanceof ChunkDeltaUpdateS2CPacket) {
            ChunkDeltaUpdateS2CPacket p = (ChunkDeltaUpdateS2CPacket)e.getPacket();
            p.visitUpdates((bp, bs) -> {
                if (mc.player == null)
                    return;
                DimensionType dimension = mc.player.world.getDimension();
                if (blocks.containsKey(dimension)) {
                    if (shown(bp, dimension)) {
                        if (!this.highlightBlocks.contains(bs.getBlock()))
                            this.blocks.get(dimension).remove(bp);
                    } else {
                        if (this.highlightBlocks.contains(bs.getBlock()))
                            this.blocks.get(dimension).add(new BlockPos(bp.getX(), bp.getY(), bp.getZ())); // do not event touch it
                    }
                } else {
                    blocks.put(dimension, new ArrayBlockingQueue<BlockPos>(65455));
                }
            });
        }
    }

    @Subscribe
    public void chunkLoaded(EventLoadChunk e) {
        WorldChunk chunk = e.getChunk();
        chunkStack.push(chunk);
    }

    @Subscribe
    public void chunkUnloaded(EventUnloadChunk e) {

    }

    @Subscribe
    public void onRender(EventWorldRender event) {
        if (!isToggled()) return;

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
            blue = 1.0F - blue;

        if (red > 1.0F)
            red = 1.0F - red;

        ArrayBlockingQueue<BlockPos> portals = this.blocks.get(mc.player.world.getDimension());
        if (portals != null) {
            for (BlockPos p : portals)
                if (mc.player.getPos().distanceTo(Vec3d.ofCenter(p)) < 128) // put max range here
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

        float or = (float) (this.getSettings().get(0).asSlider().getValue() / 255.0D);
        float og = (float) (this.getSettings().get(1).asSlider().getValue() / 255.0D);
        float ob = (float) (this.getSettings().get(2).asSlider().getValue() / 255.0D);
        if (getSetting(3).asToggle().state) {
            MacLogger.infoMessage(this.mc.world.getBlockState(blockPos).getEntries().toString());
        }
        RenderUtils.drawOutlineBox(blockPos,
                or,
                og,
                ob,
                1f);
    }
    public void onDisable () {
        blocks.clear();
    }

    @Override
    public void onEnable() {
        super.onEnable();
        for (String s : MacFileMang.readFileLines("searchblocks.txt")) {
            setHighlight(Registry.BLOCK.get(new Identifier(s)));
        }
    }
}