package com.example.basehunt.modules;

import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import net.minecraft.block.Blocks;

import java.util.List;

/** Flags chunks with player-placed blocks that rarely generate naturally. */
public class PlacedBlockFinder extends ChunkBlockFinder {
    public PlacedBlockFinder() {
        super("placed-block-finder",
            "Highlights chunks with crafting tables, furnaces, beds, etc.",
            List.of(Blocks.CRAFTING_TABLE, Blocks.FURNACE, Blocks.BLAST_FURNACE, Blocks.SMOKER,
                Blocks.ENCHANTING_TABLE, Blocks.ANVIL, Blocks.BREWING_STAND, Blocks.RED_BED,
                Blocks.WHITE_BED, Blocks.BLACK_BED, Blocks.OBSIDIAN, Blocks.BEACON, Blocks.HOPPER),
            4, new SettingColor(255, 140, 0));
    }
}
