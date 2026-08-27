package me.axeno.root.entity.inventory;

import me.axeno.root.entity.RootEntity;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class RootInventory extends SimpleContainer {
    public static final int SLOT_MAIN_HAND = 0;
    public static final int SLOT_UPGRADE_1 = 1;
    public static final int SLOT_UPGRADE_2 = 2;
    public static final int SLOT_INVENTORY_START = 4;
    public static final int INVENTORY_SIZE = 12;
    public static final int TOTAL_SIZE = SLOT_INVENTORY_START + INVENTORY_SIZE;

    private final RootEntity owner;

    public RootInventory(RootEntity owner) {
        super(TOTAL_SIZE);
        this.owner = owner;
    }

    @Override
    public boolean canPlaceItem(int slot, @NotNull ItemStack stack) {
        return true;
    }

    @Override
    public void setChanged() {
        super.setChanged();
        owner.setItemSlot(EquipmentSlot.MAINHAND, getItem(SLOT_MAIN_HAND));
    }

    public ItemStack addToStorage(ItemStack stack) {
        if (stack.isEmpty()) return ItemStack.EMPTY;

        ItemStack remaining = stack.copy();

        for (int slot = SLOT_INVENTORY_START; slot < getContainerSize(); slot++) {
            ItemStack existing = getItem(slot);

            if (existing.isEmpty()) continue;
            if (!ItemStack.isSameItemSameTags(existing, remaining)) continue;

            int maxStackSize = Math.min(existing.getMaxStackSize(), getMaxStackSize());
            int available = maxStackSize - existing.getCount();
            if (available <= 0) continue;

            int amount = Math.min(available, remaining.getCount());
            existing.grow(amount);
            remaining.shrink(amount);

            if (remaining.isEmpty()) {
                setChanged();
                return ItemStack.EMPTY;
            }
        }

        for (int slot = SLOT_INVENTORY_START; slot < getContainerSize(); slot++) {
            if (!getItem(slot).isEmpty()) continue;

            int amount = Math.min(remaining.getCount(), remaining.getMaxStackSize());
            ItemStack inserted = remaining.copy();
            inserted.setCount(amount);
            setItem(slot, inserted);
            remaining.shrink(amount);

            if (remaining.isEmpty()) {
                setChanged();
                return ItemStack.EMPTY;
            }
        }

        setChanged();
        return remaining;
    }
}