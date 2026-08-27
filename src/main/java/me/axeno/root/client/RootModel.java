package me.axeno.root.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import me.axeno.root.Root;
import me.axeno.root.entity.RootEntity;
import me.axeno.root.entity.animations.RootAnimations;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;

public class RootModel<T extends Entity> extends HierarchicalModel<T> implements ArmedModel {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(Root.MODID, "root_layer"), "main");
    private final ModelPart body;
    private final ModelPart chest;
    private final ModelPart head;
    private final ModelPart eyes;
    private final ModelPart rightHand;
    private final ModelPart leftHand;
    private final ModelPart rightLeg;
    private final ModelPart leftLeg;
    private final ModelPart root;

    public RootModel(ModelPart root) {
        this.root = root;
        this.body = root.getChild("body");
        this.chest = this.body.getChild("chest");
        this.head = this.chest.getChild("head");
        this.eyes = this.head.getChild("eyes");
        this.rightHand = this.chest.getChild("right_hand");
        this.leftHand = this.chest.getChild("left_hand");
        this.rightLeg = this.body.getChild("right_leg");
        this.leftLeg = this.body.getChild("left_leg");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));
        PartDefinition chest = body.addOrReplaceChild("chest", CubeListBuilder.create().texOffs(0, 18).addBox(-3.5F, -8.0F, -2.5F, 7.0F, 8.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -7.0F, 0.0F));
        PartDefinition head = chest.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.5F, -9.0F, -4.5F, 9.0F, 9.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -8.0F, 0.0F));
        PartDefinition eyes = head.addOrReplaceChild("eyes", CubeListBuilder.create().texOffs(36, 34).addBox(-4.25F, -2.0F, 4.5F, 4.0F, 4.0F, 2.0F, new CubeDeformation(-0.25F))
                                                                            .texOffs(36, 10).addBox(0.25F, -2.0F, 4.5F, 4.0F, 4.0F, 2.0F, new CubeDeformation(-0.25F)), PartPose.offset(0.0F, -5.5F, -9.5F));
        PartDefinition rightHand = chest.addOrReplaceChild("right_hand", CubeListBuilder.create().texOffs(0, 31).addBox(-1.5F, -1.0F, -1.5F, 3.0F, 6.0F, 3.0F, new CubeDeformation(0.0F))
                                                                                        .texOffs(24, 18).addBox(-2.0F, 4.5F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(-0.25F)), PartPose.offset(5.0F, -7.0F, 0.0F));
        PartDefinition leftHand = chest.addOrReplaceChild("left_hand", CubeListBuilder.create().texOffs(12, 31).addBox(-1.5F, -1.0F, -1.5F, 3.0F, 6.0F, 3.0F, new CubeDeformation(0.0F))
                                                                                      .texOffs(24, 26).addBox(-2.0F, 4.5F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(-0.25F)), PartPose.offset(-5.0F, -7.0F, 0.0F));
        PartDefinition rightLeg = body.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(24, 34).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 7.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(1.5F, -7.0F, 0.0F));
        PartDefinition leftLeg = body.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(36, 0).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 7.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.5F, -7.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void translateToHand(@NotNull HumanoidArm arm, @NotNull PoseStack poseStack) {
        this.body.translateAndRotate(poseStack);
        this.chest.translateAndRotate(poseStack);

        if (arm == HumanoidArm.RIGHT) {
            this.rightHand.translateAndRotate(poseStack);
        }
        else this.leftHand.translateAndRotate(poseStack);

        poseStack.translate(0.075D, -0.08D, 0.05D);
        poseStack.mulPose(new Quaternionf().rotateX((float) Math.toRadians(-30.0f)));
        poseStack.scale(0.8F, 0.8F, 0.8F);
    }

    @Override
    public void setupAnim(@NotNull Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);

        RootEntity rootEntity = (RootEntity) entity;
        applyHeadRotation(netHeadYaw, headPitch, ageInTicks);
        this.animate(rootEntity.sitDownAnimationState, RootAnimations.SIT_DOWN, ageInTicks, 1.0F);
        this.animate(rootEntity.standUpAnimationState, RootAnimations.STAND_UP, ageInTicks, 1.0F);
        this.animate(rootEntity.sitIdleAnimationState, RootAnimations.SIT_IDLE, ageInTicks, 0.25F);
        this.animate(rootEntity.idleAnimationState, RootAnimations.IDLE, ageInTicks, 0.5F);
        this.animateWalk(RootAnimations.WALK, limbSwing, limbSwingAmount, 3.0F, 2.5F);
    }

    private void applyHeadRotation(float netHeadYaw, float headPitch, float pAgeInTicks) {
        netHeadYaw = Mth.clamp(netHeadYaw, -30f, 30f);
        headPitch = Mth.clamp(headPitch, -8f, 8f);

        this.head.yRot = netHeadYaw * Mth.DEG_TO_RAD;
        this.head.xRot = headPitch * Mth.DEG_TO_RAD;
    }

    @Override
    public void renderToBuffer(@NotNull PoseStack poseStack, @NotNull VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.root().render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public @NotNull ModelPart root() {
        return this.root;
    }
}