package com.joelcrosby.fluxpylons.machine;

import com.joelcrosby.fluxpylons.FluxPylons;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.energy.IEnergyStorage;

public class SmelterGui extends AbstractContainerScreen<SmelterContainerMenu> {

    private final SmelterBlockEntity tile;
    public static final ResourceLocation TEXTURE = new ResourceLocation(FluxPylons.ID, "textures/gui/smelter.png");

    public SmelterGui(SmelterContainerMenu container, Inventory inv, Component titleIn) {
        super(container, inv, titleIn);

        this.imageWidth = 176;
        this.imageHeight = 166;

        this.tile = container.tile;
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks){
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);
    }
    
    @Override
    protected void renderBg(PoseStack poseStack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);

        var i = this.leftPos;
        var j = this.topPos;
        
        int power = getEnergyBar(44);
        int progress = getProgressBar(24);
        
        this.blit(poseStack, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);

        this.blit(poseStack, i + 89, j + 34, 176, 0, progress, 17);
        this.blit(poseStack, i + 9, j + (7 + (44 - power)), 176, 17 + (44 - power), 14, power);
    }

    public int getProgressBar(int widthPx) {
        var progress = this.tile.getProgress();
        if (progress == 100) return widthPx;
        return widthPx - (widthPx * (100 - progress) / 100);
    }
    
    public int getEnergyBar(int heightPx) {
        int stored = tile.getEnergy().map(IEnergyStorage::getEnergyStored).orElse(0);
        int max = tile.getEnergy().map(IEnergyStorage::getMaxEnergyStored).orElse(0);
        
        if (max == 0) return 0;
        
        return (((stored * 100 / max * 100) / 100) * heightPx) / 100;
    }

    @Override
    protected void renderLabels(PoseStack poseStack, int mouseX, int mouseY) {
        this.font.draw(poseStack, this.playerInventoryTitle.getString(), 8, this.imageHeight - 96 + 2, 4210752);
        this.font.draw(poseStack, this.title.getString(), 66, 6, 4210752);
    }

    @Override
    protected void renderTooltip(PoseStack matrixStack, int mouseX, int mouseY) {
        if (isHovering(9, 7, 16, 42, mouseX, mouseY)) {
            tile.getEnergy().ifPresent((storage -> {
                renderTooltip(matrixStack, Component.nullToEmpty(
                        storage.getEnergyStored()
                                + " FE / " + storage.getMaxEnergyStored()
                                + " FE"
                ), mouseX, mouseY);
            }));
        }

        if (isHovering(89, 33, 25, 20, mouseX, mouseY)) {
            renderTooltip(matrixStack, Component.nullToEmpty(this.tile.getProgress() + " %"), mouseX, mouseY);
        }
        
        super.renderTooltip(matrixStack,mouseX, mouseY);
    }
}
