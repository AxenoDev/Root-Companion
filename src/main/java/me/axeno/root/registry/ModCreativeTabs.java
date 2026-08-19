package me.axeno.root.registry;

import me.axeno.root.Root;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeTabs {

    private static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Root.MODID);

    public static final RegistryObject<CreativeModeTab> ROOT_TAB = CREATIVE_TABS.register("root_tabs",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup." + Root.MODID + ".root_tabs"))
                    .icon(() -> ModItems.ROOT_HEART.get().getDefaultInstance())
                    .withSearchBar()
                    .displayItems((parameters, output) -> {
                        for (RegistryObject<? extends Item> item : ModItems.ITEMS.getEntries()) {
                            output.accept(item.get());
                        }
                    })
                    .build()
    );

    public static void register(IEventBus modBus) {
        CREATIVE_TABS.register(modBus);
    }
}
