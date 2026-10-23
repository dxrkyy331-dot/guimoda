package com.example.basehunt.modules;

import com.example.basehunt.BaseHuntAddon;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.ChunkDataEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.meteorclient.utils.render.RenderUtils;
import meteordevelopment.meteorclient.utils.network.MeteorExecutor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Counts selected blocks per chunk and highlights chunks that reach the threshold.
 * Subclasses only supply defaults.
 */
public abstract class ChunkBlockFinder extends Module {
    private final SettingGroup sg = settings.getDefaultGroup();

    private final Setting<List<Block>> blocks;
    private final Setting<Integer> minPerChunk;
    private final Setting<Boolean> tracers = sg.add(new BoolSetting.Builder()
        .name("tracers").description("Draw a line to flagged chunks.").defaultValue(true).build());
    private final Setting<Boolean> chat = sg.add(new BoolSetting.Builder()
        .name("chat-alerts").description("Print coordinates when a chunk is flagged.").defaultValue(true).build());
    private final Setting<ShapeMode> shapeMode = sg.add(new EnumSetting.Builder<ShapeMode>()
        .name("shape-mode").defaultValue(ShapeMode.Both).build());
    private final Setting<SettingColor> sideColor;
    private final Setting<SettingColor> lineColor;

    private record Hit(int count, BlockPos center) {}
    private final Map<ChunkPos, Hit> hits = new ConcurrentHashMap<>();

    protected ChunkBlockFinder(String name, String desc, List<Block> defaultBlocks, int defaultMin, SettingColor base) {
        super(BaseHuntAddon.CATEGORY, name, desc);

        blocks = sg.add(new BlockListSetting.Builder()
            .name("blocks").description("Blocks to count.").defaultValue(defaultBlocks).build());
        minPerChunk = sg.add(new IntSetting.Builder()
            .name("min-per-chunk").description("Blocks needed in one chunk to flag it.")
            .defaultValue(defaultMin).min(1).sliderRange(1, 64).build());
        sideColor = sg.add(new ColorSetting.Builder()
            .name("side-color").defaultValue(new SettingColor(base.r, base.g, base.b, 40)).build());
        lineColor = sg.add(new ColorSetting.Builder()
            .name("line-color").defaultValue(base).build());
    }

    @Override
    public void onActivate() {
        hits.clear();
        for (Chunk c : Utils.chunks()) scanAsync(c);
    }

    @Override
    public void onDeactivate() {
        hits.clear();
    }

    @EventHandler
    private void onChunk(ChunkDataEvent event) {
        scanAsync(event.chunk);
    }

    private void scanAsync(Chunk chunk) {
        MeteorExecutor.execute(() -> scan(chunk));
    }

    private void scan(Chunk chunk) {
        ChunkPos cp = chunk.getPos();
        List<Block> wanted = blocks.get();
        int count = 0;
        long sumY = 0;
        BlockPos.Mutable pos = new BlockPos.Mutable();

        ChunkSection[] sections = chunk.getSectionArray();
        for (int i = 0; i < sections.length; i++) {
            ChunkSection section = sections[i];
            if (section == null || section.isEmpty()) continue;
            if (!section.hasAny(s -> wanted.contains(s.getBlock()))) continue; // fast palette skip

            int baseY = chunk.getBottomY() + i * 16;
            for (int x = 0; x < 16; x++)
                for (int y = 0; y < 16; y++)
                    for (int z = 0; z < 16; z++) {
                        if (wanted.contains(section.getBlockState(x, y, z).getBlock())) {
                            count++;
                            sumY += baseY + y;
                        }
                    }
        }

        if (count >= minPerChunk.get()) {
            BlockPos center = new BlockPos(cp.getStartX() + 8, (int) (sumY / count), cp.getStartZ() + 8);
            Hit old = hits.put(cp, new Hit(count, center));
            if (old == null && chat.get()) {
                ChatUtils.info("(highlight)%s(default): %d blocks at %d, %d, %d", title, count,
                    center.getX(), center.getY(), center.getZ());
            }
        } else {
            hits.remove(cp);
        }
    }

    @EventHandler
    private void onRender(Render3DEvent event) {
        for (Hit h : hits.values()) {
            double x = h.center.getX() - 8, z = h.center.getZ() - 8, y = h.center.getY();
            event.renderer.box(x, y - 1, z, x + 16, y + 2, z + 16,
                sideColor.get(), lineColor.get(), shapeMode.get(), 0);

            if (tracers.get()) {
                event.renderer.line(RenderUtils.center.x, RenderUtils.center.y, RenderUtils.center.z,
                    h.center.getX() + 0.5, y + 0.5, h.center.getZ() + 0.5, lineColor.get());
            }
        }
    }

    @Override
    public String getInfoString() {
        return String.valueOf(hits.size());
    }
}
