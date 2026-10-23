package com.example.basehunt.modules;

import com.example.basehunt.BaseHuntAddon;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.render.RenderUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.entity.*;
import net.minecraft.util.math.BlockPos;

import java.util.List;

/**
 * Storage ESP + tracers. The "storage" setting is a checklist in the module's GUI:
 * pick exactly which container types to show.
 */
public class StorageTracers extends Module {
    private final SettingGroup sg = settings.getDefaultGroup();

    private final Setting<List<BlockEntityType<?>>> storage = sg.add(new StorageBlockListSetting.Builder()
        .name("storage").description("Which storage blocks to show.")
        .defaultValue(StorageBlockListSetting.STORAGE_BLOCKS).build());
    private final Setting<Boolean> boxes = sg.add(new BoolSetting.Builder()
        .name("boxes").defaultValue(true).build());
    private final Setting<Boolean> tracers = sg.add(new BoolSetting.Builder()
        .name("tracers").defaultValue(true).build());
    private final Setting<ShapeMode> shapeMode = sg.add(new EnumSetting.Builder<ShapeMode>()
        .name("shape-mode").defaultValue(ShapeMode.Both).build());
    private final Setting<Boolean> typeColors = sg.add(new BoolSetting.Builder()
        .name("color-by-type").description("Different color per container type.").defaultValue(true).build());
    private final Setting<SettingColor> fallback = sg.add(new ColorSetting.Builder()
        .name("color").description("Used when color-by-type is off.").defaultValue(new SettingColor(255, 255, 255)).build());
    private final Setting<Integer> maxDistance = sg.add(new IntSetting.Builder()
        .name("max-distance").description("0 = unlimited.").defaultValue(0).min(0).sliderMax(512).build());

    public StorageTracers() {
        super(BaseHuntAddon.CATEGORY, "storage-tracers", "Boxes and tracers for the storage types you pick.");
    }

    @EventHandler
    private void onRender(Render3DEvent event) {
        int max = maxDistance.get();
        for (BlockEntity be : Utils.blockEntities()) {
            if (!storage.get().contains(be.getType())) continue;

            BlockPos p = be.getPos();
            if (max > 0 && mc.player.squaredDistanceTo(p.getX() + .5, p.getY() + .5, p.getZ() + .5) > (double) max * max) continue;

            Color line = typeColors.get() ? colorFor(be) : fallback.get();
            Color side = new Color(line.r, line.g, line.b, 40);

            if (boxes.get()) {
                event.renderer.box(p, side, line, shapeMode.get(), 0);
            }
            if (tracers.get()) {
                event.renderer.line(RenderUtils.center.x, RenderUtils.center.y, RenderUtils.center.z,
                    p.getX() + .5, p.getY() + .5, p.getZ() + .5, line);
            }
        }
    }

    private Color colorFor(BlockEntity be) {
        if (be instanceof TrappedChestBlockEntity) return new Color(200, 30, 30);
        if (be instanceof ChestBlockEntity) return new Color(255, 160, 0);
        if (be instanceof EnderChestBlockEntity) return new Color(120, 0, 255);
        if (be instanceof ShulkerBoxBlockEntity) return new Color(255, 105, 180);
        if (be instanceof BarrelBlockEntity) return new Color(150, 100, 50);
        if (be instanceof HopperBlockEntity) return new Color(150, 150, 150);
        return new Color(0, 200, 200); // furnaces, dispensers, droppers, etc.
    }
}
