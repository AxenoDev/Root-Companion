package me.axeno.root.registry.utils;

import me.axeno.root.Root;
import me.axeno.root.registry.ModBlocks;
import me.axeno.root.registry.ModItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Function;
import java.util.function.Supplier;

public class RegistryHelper {
    private RegistryHelper()
    {
    }

    public static BlockBehaviour.Properties blockProps()
    {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.STONE)
                .strength(1.5F, 6.0F)
                .requiresCorrectToolForDrops();
    }

    public static BlockBehaviour.Properties blockProps(MapColor mapColor)
    {
        return blockProps().mapColor(mapColor);
    }

    public static BlockBehaviour.Properties blockProps(MapColor mapColor, float hardness, float resistance)
    {
        return BlockBehaviour.Properties.of()
                .mapColor(mapColor)
                .strength(hardness, resistance)
                .requiresCorrectToolForDrops();
    }

    public static Item.Properties itemProps()
    {
        return new Item.Properties();
    }

    public static RegistryObject<Block> registerBlockWithItem(
            String name,
            Supplier<? extends Block> blockSupplier,
            Function<Block, ? extends BlockItem> itemFactory
    )
    {
        RegistryObject<Block> block = ModBlocks.registerBlock(name, blockSupplier);
        ModItems.registerItem(name, () -> itemFactory.apply(block.get()));
        return block;
    }

    public static RegistryObject<Block> registerSimpleBlock(String name, BlockBehaviour.Properties properties)
    {
        return registerBlockWithItem(
                name,
                () -> new Block(properties),
                block -> new BlockItem(block, itemProps())
        );
    }

    public static RegistryObject<Block> registerSimpleBlock(String name)
    {
        return registerSimpleBlock(name, blockProps());
    }

    public static String id(String path)
    {
        return Root.MODID + ":" + path;
    }
}
