package com.joelcrosby.fluxpylons.rendering;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nullable;

public class TankRenderer {
    public static void renderGuiTank(IFluidHandler fluidHandler, int tank, double x, double y, double zLevel, double width, double height) {
        var stack = fluidHandler.getFluidInTank(tank);
        var tankCapacity = fluidHandler.getTankCapacity(tank);

        renderGuiTank(stack, tankCapacity, x, y, zLevel, width, height);
    }
    
    public static void renderGuiTank(FluidStack stack, int tankCapacity, double x, double y, double zLevel, double width, double height) {
        // Originally Adapted from Ender IO by Silent's Mechanisms
        int amount;
        try {
            if (stack.getFluid() == null || stack.isEmpty()) {
                return;
            }
        } catch (Exception e) {
            return;
        }

        try {
            amount = stack.getAmount();
        } catch (Exception e) {
            amount = 0;
        }

        var icon = getFluidTexture(stack);
        if (icon == null) {
            return;
        }

        var renderAmount = (int) Math.max(Math.min(height, amount * height / tankCapacity), 1);
        var posY = (int) (y + height - renderAmount);

        RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_BLOCKS);
        var color = IClientFluidTypeExtensions.of(stack.getFluid()).getTintColor();
        float r = ((color >> 16) & 0xFF) / 255f;
        float g = ((color >> 8) & 0xFF) / 255f;
        float b = (color & 0xFF) / 255f;
        float a = ((color >> 24) & 0xFF) / 255f;

        RenderSystem.setShaderColor(r, g, b, a);
        RenderSystem.enableBlend();

        for (int i = 0; i < width; i += 16) {
            for (int j = 0; j < renderAmount; j += 16) {
                var drawWidth = (int) Math.min(width - i, 16);
                var drawHeight = Math.min(renderAmount - j, 16);

                var drawX = (int) (x + i);
                var drawY = posY + j;

                var minU = icon.getU0(); // min
                var maxU = icon.getU1(); // max
                var minV = icon.getV0(); // min
                var maxV = icon.getV1(); // max

                var tessellator = Tesselator.getInstance();
                var tes = tessellator.getBuilder();
                tes.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
                tes.vertex(drawX, drawY + drawHeight, 0).uv(minU, minV + (maxV - minV) * drawHeight / 16F).endVertex();
                tes.vertex(drawX + drawWidth, drawY + drawHeight, 0).uv(minU + (maxU - minU) * drawWidth / 16F, minV + (maxV - minV) * drawHeight / 16F).endVertex();
                tes.vertex(drawX + drawWidth, drawY, 0).uv(minU + (maxU - minU) * drawWidth / 16F, minV).endVertex();
                tes.vertex(drawX, drawY, 0).uv(minU, minV).endVertex();
                tessellator.end();
            }
        }

        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(1f, 1f, 1f,  1f);
    }

    @Nullable
    public static TextureAtlasSprite getFluidTexture(FluidStack stack) {
        var sprites = ForgeHooksClient.getFluidSprites(Minecraft.getInstance().level, BlockPos.ZERO, stack.getFluid().defaultFluidState());
        return sprites.length > 0 ? sprites[0] : null;
    }
}
