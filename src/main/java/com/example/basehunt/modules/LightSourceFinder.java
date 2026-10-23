package com.example.basehunt.modules;

import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import net.minecraft.block.Blocks;

import java.util.List;

/** Flags chunks with lots of torches / lanterns / glowstone. */
public class LightSourceFinder extends ChunkBlockFinder {
    public LightSourceFinder() {
        super("light-source-finder",
            "Highlights chunks with many torches, lanterns and other light blocks.",
            List.of(Blocks.TORCH, Blocks.WALL_TORCH, Blocks.LANTERN, Blocks.SOUL_LANTERN,
                Blocks.GLOWSTONE, Blocks.SEA_LANTERN, Blocks.SHROOMLIGHT, Blocks.REDSTONE_LAMP),
            8, new SettingColor(255, 230, 80));
    }
}
