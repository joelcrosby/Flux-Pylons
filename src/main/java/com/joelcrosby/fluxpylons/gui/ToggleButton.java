package com.joelcrosby.fluxpylons.gui;

import com.joelcrosby.fluxpylons.FluxPylons;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;

public class ToggleButton extends BasicButton {
    protected final Screen screen;
    protected final ResourceLocation[] textures;
    protected final String[] tooltips;
    protected int texturePosition;
    
    private static final ResourceLocation BtnBase = new ResourceLocation(FluxPylons.ID, "textures/gui/buttons/btn_base.png");
    private static final ResourceLocation BtnBaseHover = new ResourceLocation(FluxPylons.ID, "textures/gui/buttons/btn_base_hover.png");
    
    public ToggleButton(Screen screen, int x, int y, ResourceLocation[] textures, String[] tooltips, int index, OnPress onPress) {
        super(x, y, onPress);
        this.screen = screen;

        this.textures = textures;
        this.tooltips = tooltips;
        setTexturePosition(index);
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        super.render(stack, mouseX, mouseY, partialTicks);

        this.isHovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.setShaderTexture(0, isHovered ? BtnBaseHover : BtnBase);
        
        blit(stack, this.x, this.y, 0, 0, width, height, width, height);

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.setShaderTexture(0, textures[texturePosition]);

        blit(stack, this.x, this.y, 0, 0, width, height, width, height);
    }

    @Override
    public void onRenderToolTip(PoseStack stack, int x, int y) {
        if (isHovered) {
            screen.renderTooltip(stack, new TranslatableComponent(tooltips[texturePosition]), x, y);
        }
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
