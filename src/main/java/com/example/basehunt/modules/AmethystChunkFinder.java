package com.example.basehunt.modules;

import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import net.minecraft.block.Blocks;

import java.util.List;

/** Flags chunks holding many large amethyst buds. Raise the threshold to ignore natural geodes. */
public class AmethystChunkFinder extends ChunkBlockFinder {
    public AmethystChunkFinder() {
        super("amethyst-chunk-finder",
            "Highlights chunks with a lot of large amethyst buds.",
            List.of(Blocks.LARGE_AMETHYST_BUD),
            6, new SettingColor(170, 90, 255));
    }
}
