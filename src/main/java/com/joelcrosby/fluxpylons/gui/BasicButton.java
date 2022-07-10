package com.joelcrosby.fluxpylons.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.TextComponent;

public class BasicButton extends Button {
    private static final int sizeX = 16;
    private static final int sizeY = 16;

    
    public BasicButton(int x, int y, OnPress onPress) {
        super(x, y, sizeX, sizeY, TextComponent.EMPTY, onPress);
    }

    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        if (this.visible) {
            this.isHovered = pMouseX >= this.x && pMouseY >= this.y && pMouseX < this.x + this.width && pMouseY < this.y + this.height;
            this.renderButton(pPoseStack, pMouseX, pMouseY, pPartialTick);
        }
    }

    public void onRenderToolTip(PoseStack stack, int x, int y) {
        super.renderToolTip(stack, x, y);
    }
    
    @Override
    public void onClick(double x, double y) {
        super.onClick(x, y);
    }

    @Override
    public boolean mouseClicked(double x, double y, int button) {
        return super.mouseClicked(x, y, button);
    }

    @Override
    public void updateNarration(NarrationElementOutput elementOutput) {

    }
}
