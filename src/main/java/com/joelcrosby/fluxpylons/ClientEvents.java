package com.joelcrosby.fluxpylons;

import com.joelcrosby.fluxpylons.rendering.DelayedRenderer;
import net.minecraftforge.client.event.RenderLevelLastEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ClientEvents {
    @SubscribeEvent
    public static void renderWorldLastEvent(RenderLevelLastEvent event) {
        DelayedRenderer.render(event.getPoseStack());
    }
}
