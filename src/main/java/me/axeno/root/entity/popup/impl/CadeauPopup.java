package me.axeno.root.entity.popup.impl;

import me.axeno.root.entity.RootEntity;
import me.axeno.root.entity.popup.RootPopup;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class CadeauPopup extends RootPopup {
    public CadeauPopup() {
        super("root-gift", "Eh, j'ai un cadeau pour toi !", 0.2D);
    }

    @Override
    public void onServerTrigger(RootEntity entity, ServerPlayer player) {
        player.getInventory().add(new ItemStack(Items.DIAMOND, 1));
    }
}
