package com.joelcrosby.fluxpylons.rendering;

import com.joelcrosby.fluxpylons.FluxPylons;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class RenderTypes extends RenderType {
    private final static ResourceLocation pylonBeam = new ResourceLocation(FluxPylons.ID + ":textures/misc/beam.png");
    
    public RenderTypes(String name, VertexFormat format, VertexFormat.Mode mode, int p_173181_, boolean p_173182_, boolean p_173183_, Runnable runnablePre, Runnable runnablePost) {
        super(name, format, mode, p_173181_, p_173182_, p_173183_, runnablePre, runnablePost);
    }

    public static final RenderType PYLON_BEAM = create("PylonBeam",
            DefaultVertexFormat.POSITION_COLOR_TEX, VertexFormat.Mode.QUADS, 256, false, false,
            RenderType.CompositeState.builder().setTextureState(new TextureStateShard(pylonBeam, false, false))
                    .setShaderState(ShaderStateShard.POSITION_COLOR_TEX_SHADER)
                    .setLayeringState(VIEW_OFFSET_Z_LAYERING)
                    .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                    .setDepthTestState(LEQUAL_DEPTH_TEST)
                    .setCullState(CULL)
                    .setLightmapState(NO_LIGHTMAP)
                    .setWriteMaskState(COLOR_WRITE)
                    .createCompositeState(false));
}
