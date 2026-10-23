package com.example.basehunt;

import com.example.basehunt.modules.*;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.item.Items;

public class BaseHuntAddon extends MeteorAddon {
    public static final Category CATEGORY = new Category("Base Hunt", Items.AMETHYST_CLUSTER.getDefaultStack());

    @Override
    public void onInitialize() {
        Modules.get().add(new AmethystChunkFinder());
        Modules.get().add(new PlacedBlockFinder());
        Modules.get().add(new LightSourceFinder());
        Modules.get().add(new StorageTracers());
    }

    @Override
    public void onRegisterCategories() {
        Modules.registerCategory(CATEGORY);
    }

    @Override
    public String getPackage() {
        return "com.example.basehunt";
    }
}
