package me.axeno.root.client.inventory;

import me.axeno.root.inventory.RootMenu;
import me.axeno.root.entity.inventory.RootInventory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class RootScreen extends AbstractContainerScreen<RootMenu> {

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/gui/container/horse.png");

    public RootScreen(RootMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        graphics.blit(TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight);

        int startX = 81;
        int startY = 19;
        for (int i = 0; i < RootInventory.INVENTORY_SIZE; i++) {
            int col = i % 3;
            int row = i / 3;

            drawSlotBackground(
                    graphics,
                    x + startX + col * 18 - 1,
                    y + startY + row * 18 - 1
            );
        }

        int entityBoxX = x + 44;
        int entityBoxY = y + 44;
        InventoryScreen.renderEntityInInventoryFollowsMouse(
                graphics,
                entityBoxX,
                entityBoxY,
                20,
                (float) (entityBoxX - mouseX),
                (float) (entityBoxY - 40 - mouseY),
                this.menu.getRoot()
        );
    }

    private void drawSlotBackground(GuiGraphics graphics, int x, int y) {
        graphics.fill(x, y, x + 18, y + 18, 0xFF8B8B8B);
        graphics.fill(x, y, x + 18, y + 1, 0xFF373737);
        graphics.fill(x, y, x + 1, y + 18, 0xFF373737);
        graphics.fill(x, y + 17, x + 18, y + 18, 0xFFFFFFFF);
        graphics.fill(x + 17, y, x + 18, y + 18, 0xFFFFFFFF);
        graphics.fill(x + 1, y + 1, x + 17, y + 17, 0xFF8B8B8B);
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
    }
}