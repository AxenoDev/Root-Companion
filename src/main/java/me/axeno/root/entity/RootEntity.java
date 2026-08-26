package me.axeno.root.entity;

import lombok.Getter;
import me.axeno.root.entity.inventory.RootInventory;
import me.axeno.root.inventory.RootMenu;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RootEntity extends TamableAnimal {
    public static final EntityDataAccessor<Long> LAST_POSE_CHANGE_TICK = SynchedEntityData.defineId(RootEntity.class, EntityDataSerializers.LONG);
    public static final EntityDimensions STANDING_DIMENSIONS = EntityDimensions.scalable(0.75f, 1.5f);
    public static final EntityDimensions SITTING_DIMENSIONS = EntityDimensions.scalable(0.75f, 1.18f);
    protected static final int SIT_DOWN_DURATION_TICKS = 19;
    protected static final int STANDUP_DURATION_TICKS = 19;
    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState sitDownAnimationState = new AnimationState();
    public final AnimationState sitIdleAnimationState = new AnimationState();
    public final AnimationState standUpAnimationState = new AnimationState();
    @Getter
    private final RootInventory inventory = new RootInventory(this);

    public RootEntity(EntityType<? extends TamableAnimal> type, Level level) {
        super(type, level);
//        setItemInHand(new ItemStack(Items.DIAMOND_AXE));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return TamableAnimal.createMobAttributes()
                            .add(Attributes.MAX_HEALTH, 20.0D)
                            .add(Attributes.MOVEMENT_SPEED, 0.3D)
                            .add(Attributes.FOLLOW_RANGE, 24.0D);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide()) {
            this.setupSittingAnimationStates();
        }
    }

    protected void setupSittingAnimationStates() {
        if (this.isVisuallySitting()) {
            this.idleAnimationState.stop();
            this.standUpAnimationState.stop();
            if (this.isVisuallySittingDown()) {
                this.sitDownAnimationState.startIfStopped(this.tickCount);
                this.sitIdleAnimationState.stop();
            } else {
                this.sitDownAnimationState.stop();
                this.sitIdleAnimationState.startIfStopped(this.tickCount);
            }
        } else {
            this.sitDownAnimationState.stop();
            this.sitIdleAnimationState.stop();
            if (this.isStandingUp()) {
                this.idleAnimationState.stop();
                this.standUpAnimationState.startIfStopped(this.tickCount);
            } else {
                this.standUpAnimationState.stop();
                this.idleAnimationState.startIfStopped(this.tickCount);
            }
        }
    }

    public boolean isInPoseTransition() {
        long poseTime = this.getPoseTime();
        return poseTime < (long) (this.isSitting() ? SIT_DOWN_DURATION_TICKS : STANDUP_DURATION_TICKS);
    }

    public boolean isSitting() {
        return this.entityData.get(LAST_POSE_CHANGE_TICK) < 0L;
    }

    protected boolean isVisuallySittingDown() {
        return this.isSitting()
                && this.getPoseTime() < SIT_DOWN_DURATION_TICKS
                && this.getPoseTime() >= 0L;
    }

    public boolean isVisuallySitting() {
        return this.getPoseTime() < 0L != this.isSitting();
    }

    public boolean isFullySitting() {
        return this.isSitting()
                && this.getPoseTime() >= SIT_DOWN_DURATION_TICKS;
    }

    public boolean isSittingDown() {
        return this.isSitting()
                && this.getPoseTime() >= 0L
                && this.getPoseTime() < SIT_DOWN_DURATION_TICKS;
    }

    public boolean isStandingUp() {
        return !this.isSitting()
                && this.getPoseTime() >= 0L
                && this.getPoseTime() < STANDUP_DURATION_TICKS;
    }

    public long getPoseTime() {
        return this.level().getGameTime() - Math.abs(this.entityData.get(LAST_POSE_CHANGE_TICK));
    }

    public void sitDown() {
        if (this.isSitting()) return;
        this.setPose(Pose.SITTING);

        this.resetLastPoseChangeTick(-this.level().getGameTime());
        this.getNavigation().stop();

        this.setDeltaMovement(this.getDeltaMovement().multiply(0.0D, 1.0D, 0.0D));
    }

    public void standUp() {
        if (!this.isSitting()) return;
        this.setPose(Pose.STANDING);
        this.resetLastPoseChangeTick(this.level().getGameTime());
    }

    protected void resetLastPoseChangeTick(long lastPoseChangeTick) {
        this.entityData.set(LAST_POSE_CHANGE_TICK, lastPoseChangeTick);
    }

    public boolean refuseToMove() {
        return this.isSitting() || this.isInPoseTransition();
    }

    @Override
    public void travel(@NotNull Vec3 travelVector) {
        if (this.refuseToMove() && this.onGround()) {
//            this.setDeltaMovement(this.getDeltaMovement().multiply(0.0D, 1.0D, 0.0D));
            travelVector = travelVector.multiply(0.0D, 1.0D, 0.0D);
        }

        super.travel(travelVector);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(LAST_POSE_CHANGE_TICK, 0L);
    }

    @Override
    public void onSyncedDataUpdated(@NotNull EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
    }

    @Override
    public @NotNull EntityDimensions getDimensions(@NotNull Pose pose) {
        if (pose == Pose.SITTING)
            return SITTING_DIMENSIONS.scale(this.getScale());

        return STANDING_DIMENSIONS.scale(this.getScale());
    }

    @Override
    protected void updateWalkAnimation(float pPartialTick) {
        float f = 0.0f;
        if (this.getPose() == Pose.STANDING) f = Math.min(pPartialTick * 6.0F, 1.0F);
        this.walkAnimation.update(f, 0.2F);
    }

    private void setItemInHand(ItemStack stack) {
        this.setItemInHand(InteractionHand.MAIN_HAND, stack);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(3, new FollowOwnerGoal(this, 1.0D, 5.0F, 2.0F, false));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
    }

    @Override
    public @NotNull InteractionResult mobInteract(Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

//        if (isPopupActive() && stack.isEmpty()) {
//            if (this.level().isClientSide) {
//                DialogueManager.open(RootDialogues.OUI_BONJOUR);
//            }
//
//            return InteractionResult.sidedSuccess(this.level().isClientSide);
//        }

        if (this.isOwnedBy(player) && stack.isEmpty() && !this.level().isClientSide) {
            if (player.isShiftKeyDown()) {
                this.openInventory((ServerPlayer) player);
            } else {
                if (this.isFullySitting()) {
                    this.standUp();
                } else if (!this.isInPoseTransition() && !this.isSitting()) {
                    this.sitDown();
                }
            }

            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(player, hand);
    }

    public void openInventory(ServerPlayer player) {
        NetworkHooks.openScreen(player, new SimpleMenuProvider(
                                        (id, playerInv, p) -> new RootMenu(id, playerInv, this.inventory, this),
                                        this.getDisplayName()),
                                buf -> buf.writeInt(this.getId())
        );
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        ListTag list = new ListTag();
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (!stack.isEmpty()) {
                CompoundTag itemTag = new CompoundTag();
                itemTag.putInt("Slot", i);
                stack.save(itemTag);
                list.add(itemTag);
            }
        }
        tag.put("Inventory", list);
        tag.putLong("LastPoseTick", this.entityData.get(LAST_POSE_CHANGE_TICK));
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        ListTag list = tag.getList("Inventory", CompoundTag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag itemTag = list.getCompound(i);
            int slot = itemTag.getInt("Slot");
            if (slot >= 0 && slot < inventory.getContainerSize()) {
                inventory.setItem(slot, ItemStack.of(itemTag));
            }
        }

        long poseTick = tag.getLong("LastPoseTick");
        if (poseTick < 0L) this.setPose(Pose.SITTING);
        this.resetLastPoseChangeTick(poseTick);
    }

    public void tameByOwner(Player player) {
        this.tame(player);
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(@NotNull ServerLevel level, @NotNull AgeableMob otherParent) {
        return null; // pour la reproduction -> donc y'a pas
    }
}
