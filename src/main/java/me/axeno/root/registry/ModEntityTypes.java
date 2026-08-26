package me.axeno.root.registry;

import me.axeno.root.Root;
import me.axeno.root.entity.RootEntity;
import me.axeno.root.registry.utils.RegistryHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static me.axeno.root.entity.RootEntity.STANDING_DIMENSIONS;

public class ModEntityTypes {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Root.MODID);

    public static final RegistryObject<EntityType<RootEntity>> ROOT_ENTITY = ENTITY_TYPES.register("root",
            () -> EntityType.Builder.of(RootEntity::new, MobCategory.CREATURE)
                    .sized(STANDING_DIMENSIONS.width, STANDING_DIMENSIONS.height)
                    .clientTrackingRange(10)
                    .build(RegistryHelper.id("root")));

    private ModEntityTypes() {
    }

    public static void register(IEventBus bus) {
        ENTITY_TYPES.register(bus);
    }
}
