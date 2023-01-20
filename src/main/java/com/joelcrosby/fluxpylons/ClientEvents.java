package com.joelcrosby.fluxpylons;

import com.joelcrosby.fluxpylons.rendering.DelayedRenderer;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ClientEvents {
    @SubscribeEvent
    public static void renderLevelStageEvent(RenderLevelStageEvent event) {
        // TODO: migrate from RenderLevelLastEvent
        
        DelayedRenderer.render(event.getPoseStack());
    }
}
