package me.axeno.root.item;

import me.axeno.root.client.dialogue.DialogueManager;
import me.axeno.root.client.dialogue.RootDialogues;
import me.axeno.root.entity.RootEntity;
import me.axeno.root.registry.ModEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class RootItem extends Item {
    public RootItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        BlockPos pos = context.getClickedPos().above();

        if (level.isClientSide) {
            DialogueManager.open(RootDialogues.FIRST_CONTACT);
            return InteractionResult.sidedSuccess(true);
        }

        if (level instanceof ServerLevel serverLevel) {
            RootEntity root = ModEntityTypes.ROOT_ENTITY.get().create(serverLevel);
            if (root != null) {
                root.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, 0.0F, 0.0F);
                if (player != null) {
                    root.tameByOwner(player);
                    if (!player.getAbilities().instabuild) {
                        context.getItemInHand().shrink(1);
                    }
                }
                serverLevel.addFreshEntity(root);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
