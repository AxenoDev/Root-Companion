package me.axeno.root.client;

import lombok.Getter;
import me.axeno.root.Root;
import me.axeno.root.client.inventory.RootScreen;
import me.axeno.root.client.renderer.RootRenderer;
import me.axeno.root.client.ui.RootFonts;
import me.axeno.root.registry.ModEntityTypes;
import me.axeno.root.registry.ModMenuTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = Root.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class RootClient {
    @Getter
    private static RootFonts fonts;

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        fonts = new RootFonts();
        fonts.reload(Minecraft.getInstance().getResourceManager());

        event.enqueueWork(() -> MenuScreens.register(ModMenuTypes.ROOT_MENU.get(), RootScreen::new));
    }

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntityTypes.ROOT_ENTITY.get(), RootRenderer::new);
    }

}
