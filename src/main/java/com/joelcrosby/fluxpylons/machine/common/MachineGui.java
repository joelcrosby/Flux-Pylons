package com.joelcrosby.fluxpylons.machine.common;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.energy.IEnergyStorage;

import java.text.DecimalFormat;
import java.util.List;

public abstract class MachineGui<T extends MachineContainerMenu<E>, E extends MachineBlockEntity> extends AbstractContainerScreen<T>  {

    protected E tile;
    
    public MachineGui(T container, Inventory inv, Component titleIn) {
        super(container, inv, titleIn);

        this.tile = container.tile;
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks){
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);
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
                var formatter = new DecimalFormat("#,###");
                var stored = formatter.format(storage.getEnergyStored());
                var max = formatter.format(storage.getMaxEnergyStored());

                renderTooltip(matrixStack, Component.nullToEmpty(stored + " FE / " + max + " FE"), mouseX, mouseY);
            }));
        }

        if (isHovering(89, 33, 25, 20, mouseX, mouseY)) {
            renderTooltip(matrixStack, Component.nullToEmpty(this.tile.getProgress() + " %"), mouseX, mouseY);
        }

        super.renderTooltip(matrixStack,mouseX, mouseY);
    }

    public List<Component> getTooltips() {
        return List.of(Component.nullToEmpty(this.tile.getProgress() + " %"));
    }
    
    public Rect2i getTooltipArea() {
        return new Rect2i(79, 31, 17, 24);
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
}
