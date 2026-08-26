package me.axeno.root.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import me.axeno.noctisui.client.NoctisUIClient;
import me.axeno.noctisui.client.api.system.Render2DEngine;
import me.axeno.noctisui.client.api.system.render.font.FontAtlas;
import me.axeno.noctisui.client.utils.Color;
import me.axeno.root.Root;
import me.axeno.root.client.RootModel;
import me.axeno.root.client.dialogue.DialogueLayout;
import me.axeno.root.entity.RootEntity;
import me.axeno.root.entity.popup.RootPopup;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

public class RootRenderer extends MobRenderer<RootEntity, RootModel<RootEntity>> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(Root.MODID, "textures/entity/root.png");
    private static final float POPUP_Y = 1.60F;
    private static final float POPUP_SCALE = 0.008F;
    private static final float FONT_SIZE = 16F;
    private static final float PADDING_X = 10F;
    private static final float PADDING_Y = 6F;
    private static final int TEXT_COLOR = 0xFFFFFFFF;

    public RootRenderer(EntityRendererProvider.Context context) {
        super(context, new RootModel<>(context.bakeLayer(RootModel.LAYER_LOCATION)), 0.2F);
        this.addLayer(new ItemInHandLayer<>(this, context.getItemInHandRenderer()));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull RootEntity entity) {
        return TEXTURE;
    }

    @Override
    public void render(
            @NotNull RootEntity entity,
            float entityYaw,
            float partialTick,
            @NotNull PoseStack poseStack,
            @NotNull MultiBufferSource buffer,
            int packedLight
    ) {
        super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
        if (!entity.isPopupActive()) return;

        RootPopup popup = entity.getActivePopup();
        if (popup == null) return;
        renderPopup(poseStack, popup.getText());
    }

    private void renderPopup(PoseStack poseStack, @NotNull String message) {
        if (message.isEmpty()) return;
        Minecraft minecraft = Minecraft.getInstance();

        if (NoctisUIClient.getInstance() == null) return;
        if (NoctisUIClient.getInstance().getFonts() == null) return;

        FontAtlas font = NoctisUIClient.getInstance().getFonts().getPoppins();
        if (font == null) return;

        float textWidth = font.getWidth(message, FONT_SIZE);
        float textHeight = font.getLineHeight(FONT_SIZE);
        float width = textWidth + (PADDING_X * 2F);
        float height = textHeight + PADDING_Y * 2F;
        float left = -width / 2F;
        float top = -height;

        poseStack.pushPose();
        poseStack.translate(0.0D, POPUP_Y, 0.0D);
        poseStack.mulPose(minecraft.getEntityRenderDispatcher().cameraOrientation());
        poseStack.scale(-POPUP_SCALE, -POPUP_SCALE, POPUP_SCALE);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        RenderSystem.depthFunc(GL11.GL_LEQUAL);

        Render2DEngine.drawRect(
                poseStack,
                left,
                top,
                width,
                height,
                new Color(
                        DialogueLayout.PANEL_RED,
                        DialogueLayout.PANEL_GREEN,
                        DialogueLayout.PANEL_BLUE,
                        DialogueLayout.PANEL_ALPHA
                )
        );

        Render2DEngine.drawOutline(
                poseStack,
                left,
                top,
                width,
                height,
                new Color(
                        DialogueLayout.OUTLINE_RED,
                        DialogueLayout.OUTLINE_GREEN,
                        DialogueLayout.OUTLINE_BLUE,
                        DialogueLayout.OUTLINE_ALPHA
                )
        );

        font.render(
                poseStack,
                message,
                -textWidth / 2F,
                top + PADDING_Y,
                FONT_SIZE,
                TEXT_COLOR
        );

        RenderSystem.disableBlend();
        poseStack.popPose();
    }
}
