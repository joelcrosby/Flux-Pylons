package com.joelcrosby.fluxpylons.pylon;

import com.joelcrosby.fluxpylons.rendering.DelayedRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class PylonBlockEntityRenderer implements BlockEntityRenderer<PylonBlockEntity> {

    @SuppressWarnings("unused")
    public PylonBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {
    }
    
    @Override
    public void render(PylonBlockEntity tile, float partialTicks, PoseStack matrixStack, MultiBufferSource source, int light, int overlay) {
        DelayedRenderer.add(tile);
    }
}
