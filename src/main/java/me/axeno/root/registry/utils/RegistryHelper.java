package me.axeno.root.registry.utils;

import me.axeno.root.Root;
import net.minecraft.world.item.Item;

public class RegistryHelper {
    private RegistryHelper() {
    }

    public static Item.Properties itemProps() {
        return new Item.Properties();
    }

    public static String id(String path) {
        return Root.MODID + ":" + path;
    }
}
