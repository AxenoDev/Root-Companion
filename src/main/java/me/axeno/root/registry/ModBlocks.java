package me.axeno.root.registry;

import me.axeno.root.Root;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Root.MODID);

    private ModBlocks() {
    }

    public static void register(IEventBus modBus) {
        BLOCKS.register(modBus);
    }

    public static RegistryObject<Block> registerBlock(String name, Supplier<? extends Block> supplier) {
        return BLOCKS.register(name, supplier);
    }

    public static RegistryObject<Block> registerBlock(String name, BlockBehaviour.Properties properties) {
        return registerBlock(name, () -> new Block(properties));
    }
}
