package com.joelcrosby.fluxpylons.util;

import com.joelcrosby.fluxpylons.Utility;
import com.joelcrosby.fluxpylons.item.upgrade.filter.FluidFilterGui;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;

public class FluidStackRenderer extends ItemRenderer {
    private final Vector2D boundsTopLeft;
    private final Vector2D boundsBottomRight;
    private final AbstractContainerScreen<?> screen;

    public FluidStackRenderer(TextureManager textureManager,
                              ModelManager modelManager,
                              ItemColors itemColors,
                              BlockEntityWithoutLevelRenderer blockEntityWithoutLevelRenderer,
                              Vector2D boundsTopLeft,
                              Vector2D boundsBottomRight,
                              AbstractContainerScreen<?> screen) {
        
        super(textureManager, modelManager, itemColors, blockEntityWithoutLevelRenderer);
        this.boundsTopLeft = boundsTopLeft;
        this.boundsBottomRight = boundsBottomRight;
        this.screen = screen;
    }

    @Override
    public void renderGuiItem(ItemStack stack, int x, int y, BakedModel bakedModel) {
        if (!shouldRenderFluid(stack, x, y, true)) {
            super.renderGuiItem(stack, x, y, bakedModel);
            return;
        }
        
        var fluidHandlerLazyOptional = FluidUtil.getFluidHandler(stack);
        var fluidStack = FluidStack.EMPTY;
        var fluidHandler = fluidHandlerLazyOptional.resolve().get();
        
        for (int tank = 0; tank < fluidHandler.getTanks(); tank++) {
            fluidStack = fluidHandler.getFluidInTank(tank);
            if (!fluidStack.isEmpty())
                break;
        }
        
        var fluid = fluidStack.getFluid();
        var fluidStill = fluid.getAttributes().getStillTexture();
        var attributes = fluid.getAttributes();
        var fluidStillSprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(fluidStill);

        var fluidColor = attributes.getColor(fluidStack);

        var red = (float) (fluidColor >> 16 & 255) / 255.0F;
        var green = (float) (fluidColor >> 8 & 255) / 255.0F;
        var blue = (float) (fluidColor & 255) / 255.0F;

        RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_BLOCKS);

        var poseStack = RenderSystem.getModelViewStack();
        poseStack.pushPose();
        RenderSystem.setShaderColor(red, green, blue, 1.0f);
        var zLevel = 100;
        
        var uMin = fluidStillSprite.getU0();
        var uMax = fluidStillSprite.getU1();
        var vMin = fluidStillSprite.getV0();
        var vMax = fluidStillSprite.getV1();
        
        var tesselator = Tesselator.getInstance();
        var vertexBuffer = tesselator.getBuilder();

        vertexBuffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        vertexBuffer.vertex(x, y + 16.0D, zLevel).uv(uMin, vMax).endVertex();
        vertexBuffer.vertex(x + 16.0D, y + 16.0D, zLevel).uv(uMax, vMax).endVertex();
        vertexBuffer.vertex(x + 16.0D, y, zLevel).uv(uMax, vMin).endVertex();
        vertexBuffer.vertex(x, y, zLevel).uv(uMin, vMin).endVertex();
        tesselator.end();
        poseStack.popPose();
        
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.applyModelViewMatrix();
    }

    public boolean shouldRenderFluid(ItemStack stack, int x, int y, boolean includeCarried) {
        if (!(screen instanceof FluidFilterGui fluidScreen)) {
            return false;
        }

        if (fluidScreen.getMenu().getCarried().equals(stack)) {
            if (includeCarried)
                return false;
        }

        if (!(Utility.inBounds(boundsTopLeft.x,
                boundsTopLeft.y,
                boundsBottomRight.x - boundsTopLeft.x,
                boundsBottomRight.y - boundsTopLeft.y,
                x,
                y))) {
            return false;
        }
        
        var fluidHandlerLazyOptional = FluidUtil.getFluidHandler(stack);
        
        if (!fluidHandlerLazyOptional.isPresent()) {
            return false;
        }
        
        var fluidStack = FluidStack.EMPTY;
        var fluidHandler = fluidHandlerLazyOptional.resolve().get();
        
        for (int tank = 0; tank < fluidHandler.getTanks(); tank++) {
            fluidStack = fluidHandler.getFluidInTank(tank);
            if (!fluidStack.isEmpty())
                break;
        }
        
        if (fluidStack.isEmpty()) {
            return false;
        }

        var fluid = fluidStack.getFluid();
        
        if (fluid == null) {
            return false;
        }
        
        var fluidStill = fluid.getAttributes().getStillTexture();
        
        TextureAtlasSprite fluidStillSprite = null;
        
        if (fluidStill != null) {
            fluidStillSprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(fluidStill);
        }

        return fluidStillSprite != null;
    }
}
