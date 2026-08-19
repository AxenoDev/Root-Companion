package me.axeno.root;

import me.axeno.root.entity.RootEntity;
import me.axeno.root.registry.ModBlocks;
import me.axeno.root.registry.ModCreativeTabs;
import me.axeno.root.registry.ModEntityTypes;
import me.axeno.root.registry.ModItems;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Root.MODID)
public class Root {

    public static final String MODID = "root";

    public Root(FMLJavaModLoadingContext context) {
        IEventBus bus = context.getModEventBus();

        ModItems.register(bus);
        ModBlocks.register(bus);
        ModCreativeTabs.register(bus);
        ModEntityTypes.register(bus);

        bus.addListener(this::onEntityAttributeCreation);
    }

    private void onEntityAttributeCreation(EntityAttributeCreationEvent event) {
        event.put(ModEntityTypes.ROOT_ENTITY.get(), RootEntity.createAttributes().build());
    }

}
