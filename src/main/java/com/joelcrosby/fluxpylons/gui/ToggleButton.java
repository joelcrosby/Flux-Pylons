package com.joelcrosby.fluxpylons.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;

public class ToggleButton extends Button {
    private final ResourceLocation[] textures;
    private int texturePosition;
    
    private static final int sizeX = 16;
    private static final int sizeY = 16;
    
    public ToggleButton(int x, int y, ResourceLocation[] textures, int index, OnPress onPress) {
        super(x, y, sizeX, sizeY, TextComponent.EMPTY, onPress);

        this.textures = textures;
        setTexturePosition(index);
    }

    public ToggleButton(int x, int y, ResourceLocation[] textures, int index, OnPress onPress, OnTooltip onTooltip) {
        super(x, y, sizeX, sizeY, TextComponent.EMPTY, onPress, onTooltip);

        this.textures = textures;
        setTexturePosition(index);
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.setShaderTexture(0, textures[texturePosition]);
        
        blit(stack, this.x, this.y, 0, 0, width, height, width, height);
    }

    @Override
    public void renderToolTip(PoseStack stack, int x, int y) {
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

    public int getTexturePosition() {
        return texturePosition;
    }

    public void setTexturePosition(int texturePosition) {
        this.texturePosition = Math.min(texturePosition, textures.length);
    }

    public void nextTexturePosition() {
        if (texturePosition == textures.length)
            texturePosition = 0;
        else
            texturePosition++;
    }
}
