package me.axeno.root.client.renderer;

import me.axeno.root.Root;
import me.axeno.root.entity.RootEntity;
import net.minecraft.client.model.ChickenModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class RootRenderer extends MobRenderer<RootEntity, ChickenModel<RootEntity>> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Root.MODID, "textures/entity/root.png");

    public RootRenderer(EntityRendererProvider.Context context) {
        super(context, new ChickenModel<>(context.bakeLayer(ModelLayers.CHICKEN)), 0.3F);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull RootEntity entity) {
        return TEXTURE;
    }
}
