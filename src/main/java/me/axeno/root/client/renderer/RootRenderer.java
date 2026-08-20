package me.axeno.root.client.renderer;

import me.axeno.root.Root;
import me.axeno.root.client.RootModel;
import me.axeno.root.entity.RootEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class RootRenderer extends MobRenderer<RootEntity, RootModel<RootEntity>> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Root.MODID, "textures/entity/root.png");

    public RootRenderer(EntityRendererProvider.Context context) {
        super(context, new RootModel<>(context.bakeLayer(RootModel.LAYER_LOCATION)), 0.2F);
        this.addLayer(new ItemInHandLayer<>(this, context.getItemInHandRenderer()));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull RootEntity entity) {
        return TEXTURE;
    }
}
