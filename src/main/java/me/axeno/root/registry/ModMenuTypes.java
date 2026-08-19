package me.axeno.root.registry;

import me.axeno.root.Root;
import me.axeno.root.entity.RootEntity;
import me.axeno.root.inventory.RootMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, Root.MODID);

    public static final RegistryObject<MenuType<RootMenu>> ROOT_MENU = MENU_TYPES.register(
            "root_inventory",
            () -> IForgeMenuType.create((windowId, inv, data) -> {
                int entityId = data.readInt();
                var entity = inv.player.level().getEntity(entityId);
                if (entity instanceof RootEntity root) {
                    return new RootMenu(windowId, inv, root.getInventory(), root);
                }
                throw new IllegalStateException("Root entity not found for id " + entityId);
            })
    );

    private ModMenuTypes() {}

    public static void register(IEventBus bus) {
        MENU_TYPES.register(bus);
    }
}
