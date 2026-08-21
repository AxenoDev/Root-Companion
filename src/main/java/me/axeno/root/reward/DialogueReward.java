package me.axeno.root.reward;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public enum DialogueReward {

    ROOT_FIRST_DIAMOND {
        @Override
        public void execute() {
            Minecraft minecraft = Minecraft.getInstance();
            Player player = minecraft.player;
            if (player == null) return;

            player.getInventory().add(new ItemStack(Items.DIAMOND, 1));
        }
    },

    ;

    public abstract void execute();
}