package com.joelcrosby.fluxpylons.pipe;

import com.joelcrosby.fluxpylons.FluxPylons;
import com.joelcrosby.fluxpylons.network.PacketHandler;
import com.joelcrosby.fluxpylons.network.packets.PacketOpenScreen;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.Optional;

public class PipeUpgradeGui extends AbstractContainerScreen<PipeUpgradeContainerMenu> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(FluxPylons.ID, "textures/gui/pipe.png");

    public PipeUpgradeGui(PipeUpgradeContainerMenu container, Inventory inv, Component titleIn) {
        super(container, inv, titleIn);
        
        this.imageWidth = 176;
        this.imageHeight = 153;
    }
    
    @Override
    protected void renderBg(PoseStack poseStack, float partialTicks, int mouseX, int mouseY) {
        renderBackground(poseStack);
        
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);

        this.blit(poseStack, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    protected void renderLabels(PoseStack poseStack, int mouseX, int mouseY) {
        this.font.draw(poseStack, this.playerInventoryTitle.getString(), 8, this.imageHeight - 96 + 2, 4210752);
        this.font.draw(poseStack, this.title.getString(), 8, 6, 4210752);

        renderTooltip(poseStack, mouseX - leftPos, mouseY - topPos);
    }

    @Override
    public boolean mouseClicked(double x, double y, int btn) {
        if (btn == 1 && hoveredSlot != null) {
            var slot = hoveredSlot.getSlotIndex();
            PacketHandler.sendToServer(new PacketOpenScreen(slot));
            return true;
        }
        
        return super.mouseClicked(x, y, btn);
    }

    @Override
    protected void renderTooltip(PoseStack poseStack, ItemStack itemStack, int mouseX, int mouseY) {
        if (mouseY > this.imageHeight - 96 + 2) {
            super.renderTooltip(poseStack, itemStack, mouseX, mouseY);
            return;
        } 
        
        var components = itemStack.getTooltipLines(minecraft.player, TooltipFlag.Default.NORMAL);
        components.add(new TextComponent(""));
        components.add(new TranslatableComponent("item.fluxpylons.filter.tooltip.open-menu").setStyle(Style.EMPTY.applyFormat(ChatFormatting.DARK_GRAY)));
        
        renderTooltip(poseStack, components, Optional.empty(), mouseX, mouseY); 
    }
}
