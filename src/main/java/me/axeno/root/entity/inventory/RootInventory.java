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
    public static final int SLOT_UPGRADE_3 = 3;

    public static final int SLOT_INVENTORY_START = 4;
    public static final int INVENTORY_SIZE = 6;

    public static final int TOTAL_SIZE =
            SLOT_INVENTORY_START + INVENTORY_SIZE;

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
}