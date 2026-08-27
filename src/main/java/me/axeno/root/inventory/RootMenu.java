package me.axeno.root.inventory;

import lombok.Getter;
import me.axeno.root.entity.RootEntity;
import me.axeno.root.entity.inventory.RootInventory;
import me.axeno.root.registry.ModMenuTypes;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class RootMenu extends AbstractContainerMenu {
    private static final int ROOT_SLOTS = RootInventory.TOTAL_SIZE;

    private final Container rootInventory;

    @Getter
    private final RootEntity root;

    public RootMenu(int windowId, Inventory playerInv, Container rootInventory, RootEntity root) {
        super(ModMenuTypes.ROOT_MENU.get(), windowId);
        this.rootInventory = rootInventory;
        this.root = root;

        rootInventory.startOpen(playerInv.player);

        this.addSlot(new Slot(rootInventory, RootInventory.SLOT_MAIN_HAND, 16, 10) {
            @Override
            public int getMaxStackSize() {
                return 1;
            }
        });

        this.addSlot(createUpgradeSlot(RootInventory.SLOT_UPGRADE_1, 16, 28));
        this.addSlot(createUpgradeSlot(RootInventory.SLOT_UPGRADE_2, 16, 46));

        int startX = 95;
        int startY = 10;
        for (int i = 0; i < RootInventory.INVENTORY_SIZE; i++) {
            int col = i % 4;
            int row = i / 4;
            this.addSlot(new Slot(rootInventory, RootInventory.SLOT_INVENTORY_START + i, startX + col * 18, startY + row * 18));
        }

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInv, 9 + col + row * 9, 8 + col * 18, 87 + row * 18));
            }
        }

        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInv, col, 8 + col * 18, 145));
        }
    }

    private Slot createUpgradeSlot(int slot, int x, int y) {
        return new Slot(rootInventory, slot, x, y) {
            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                return rootInventory.canPlaceItem(slot, stack);
            }

            @Override
            public int getMaxStackSize() {
                return 1;
            }
        };
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {

        ItemStack result = ItemStack.EMPTY;

        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            result = stackInSlot.copy();
            if (index < ROOT_SLOTS) {
                if (!this.moveItemStackTo(stackInSlot, ROOT_SLOTS, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (!this.moveItemStackTo(stackInSlot, 0, ROOT_SLOTS, false)) {
                    return ItemStack.EMPTY;
                }
            }
            if (stackInSlot.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return result;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return this.root.isAlive() && this.root.distanceToSqr(player) < 64.0D;
    }

    @Override
    public void removed(@NotNull Player player) {
        super.removed(player);
        this.rootInventory.stopOpen(player);
    }
}