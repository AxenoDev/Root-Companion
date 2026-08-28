package me.axeno.root.registry;

import me.axeno.root.Root;
import me.axeno.root.item.RootItem;
import me.axeno.root.registry.utils.RegistryHelper;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Root.MODID);
    public static final RegistryObject<Item> ROOT_HEART = registerItem("root_heart");
    public static final RegistryObject<Item> ROOT = registerItem("root", () -> new RootItem(RegistryHelper.itemProps()));
    public static final RegistryObject<Item> ROOT_SPAWN_EGG = ITEMS.register(
            "root_spawn_egg",
            () -> new ForgeSpawnEggItem(
                    ModEntityTypes.ROOT_ENTITY,
                    0xffffff,
                    0xffffff,
                    new Item.Properties()
            )
    );

    private ModItems() {
    }

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }

    public static RegistryObject<Item> registerItem(String name, Supplier<? extends Item> supplier) {
        return ITEMS.register(name, supplier);
    }

    public static RegistryObject<Item> registerItem(String name) {
        return registerItem(name, () -> new Item(RegistryHelper.itemProps()));
    }

    public static RegistryObject<BlockItem> registerBlock(String name, Supplier<? extends BlockItem> supplier) {
        return ITEMS.register(name, supplier);
    }

    public static RegistryObject<BlockItem> registerBlock(String name, Block block) {
        return registerBlock(name, () -> new BlockItem(block, RegistryHelper.itemProps()));
    }
}
