package com.joelcrosby.fluxpylons.crate;

import com.joelcrosby.fluxpylons.FluxPylons;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class CrateGui extends AbstractContainerScreen<CrateContainerMenu> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(FluxPylons.ID, "textures/gui/crate.png");

    public CrateGui(CrateContainerMenu container, Inventory inv, Component titleIn) {
        super(container, inv, titleIn);
        this.imageWidth = 176;
        this.imageHeight = 222;
    }
    
    @Override
    protected void renderBg(PoseStack matrix, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, this.getTexture());

        this.blit(matrix, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    protected void renderLabels(PoseStack matrix, int mouseX, int mouseY) {
        this.font.draw(matrix, this.playerInventoryTitle.getString(), 8, this.imageHeight - 96 + 2, 4210752);
        this.font.draw(matrix, this.title.getString(), 8, 6, 4210752);
    }
    
    protected ResourceLocation getTexture() {
        return TEXTURE;
    }
}
