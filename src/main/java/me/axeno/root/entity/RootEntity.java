package me.axeno.root.entity;

import lombok.Getter;
import me.axeno.root.entity.inventory.RootInventory;
import me.axeno.root.entity.popup.RootPopup;
import me.axeno.root.entity.popup.RootPopups;
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
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RootEntity extends TamableAnimal {
    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState sitDownAnimationState = new AnimationState();
    public final AnimationState sitIdleAnimationState = new AnimationState();
    public final AnimationState standUpAnimationState = new AnimationState();
    private static final int POPUP_MIN_COOLDOWN = 20 * 60 * 60;  // 1h
    private static final int POPUP_MAX_COOLDOWN = 20 * 60 * 60 * 2; // 2h
    private static final int POPUP_DURATION = 20 * 60 * 5; // 5minutes

    private int popupCooldown = randomCooldown();
    private int popupDuration = 0;

    private static final String NO_POPUP = "";

    private static final EntityDataAccessor<String> DATA_POPUP_ID = SynchedEntityData.defineId(RootEntity.class, EntityDataSerializers.STRING);

    @Getter
    private final RootInventory inventory = new RootInventory(this);

    public RootEntity(EntityType<? extends TamableAnimal> type, Level level) {
        super(type, level);
        setItemInHand(new ItemStack(Items.DIAMOND_AXE));
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

        if (this.level().isClientSide && !this.walkAnimation.isMoving()) {
            this.idleAnimationState.startIfStopped(this.tickCount);
        }
    }

    @Override
    protected void updateWalkAnimation(float pPartialTick) {
        float f;
        if (this.getPose() == Pose.STANDING) {
            f = Math.min(pPartialTick * 6.0F, 1.0F);
        } else {
            f = 0.0F;
        }

        this.walkAnimation.update(f, 0.2F);
    }

    private void setItemInHand(ItemStack stack) {
        this.setItemInHand(InteractionHand.MAIN_HAND, stack);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_POPUP_ID, NO_POPUP);
    }

    public boolean isPopupActive() {
        return !this.entityData.get(DATA_POPUP_ID).isEmpty();
    }

    @Nullable
    public RootPopup getActivePopup() {
        String id = this.entityData.get(DATA_POPUP_ID);
        return id.isEmpty() ? null : RootPopups.get(id);
    }

    public void setActivePopup(@Nullable RootPopup popup) {
        this.entityData.set(DATA_POPUP_ID, popup == null ? NO_POPUP : popup.getId());
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
    public void customServerAiStep() {
        super.customServerAiStep();
        if (isPopupActive()) {
            if (popupDuration > 0) {
                popupDuration--;
            }

            if (popupDuration <= 0) {
                setActivePopup(null);
                popupCooldown = randomCooldown();
            }

            return;
        }

        if (popupCooldown > 0) {
            popupCooldown--;
            return;
        }

        setActivePopup(RootPopups.random(this.random));
        popupDuration = POPUP_DURATION;
    }

    private int randomCooldown() {
        return POPUP_MIN_COOLDOWN + this.random.nextInt(POPUP_MAX_COOLDOWN - POPUP_MIN_COOLDOWN + 1);
    }

    @Override
    public @NotNull InteractionResult mobInteract(Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (isPopupActive() && stack.isEmpty()) {
            RootPopup popup = getActivePopup();
            if (popup == null) {
                return InteractionResult.sidedSuccess(this.level().isClientSide);
            }
            if (this.level().isClientSide) {
                popup.onClientTrigger(this);
                return InteractionResult.SUCCESS;
            }

            if (player instanceof ServerPlayer serverPlayer) {
                popup.onServerTrigger(this, serverPlayer);
                this.setActivePopup(null);
                popupDuration = 0;
                popupCooldown = randomCooldown();
            }

            return InteractionResult.SUCCESS;
        }

        if (this.isOwnedBy(player) && stack.isEmpty() && !this.level().isClientSide) {
            if (player.isShiftKeyDown()) {
                this.openInventory((ServerPlayer) player);
            } else {
            }

            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(player, hand);
    }

    public void openInventory(ServerPlayer player) {
        NetworkHooks.openScreen(player, new SimpleMenuProvider(
                        (id, playerInv, p) -> new RootMenu(id, playerInv, this.inventory, this), this.getDisplayName()),
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
    }

    public void tameByOwner(Player player) {
        this.tame(player);
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(@NotNull ServerLevel level, @NotNull AgeableMob otherParent) {
        return null; // pour la reproduction -> donc y'a pas
    }
}
