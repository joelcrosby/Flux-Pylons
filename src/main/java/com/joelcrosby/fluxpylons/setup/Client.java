package com.joelcrosby.fluxpylons.setup;

import com.joelcrosby.fluxpylons.FluxPylonsBlocks;
import com.joelcrosby.fluxpylons.FluxPylonsContainerMenus;
import com.joelcrosby.fluxpylons.crate.CrateGui;
import com.joelcrosby.fluxpylons.pipe.PipeGui;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public final class Client {

    @SuppressWarnings("unused")
    public static void setup(FMLClientSetupEvent event) {
        ItemBlockRenderTypes.setRenderLayer(FluxPylonsBlocks.BASIC_PIPE, RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(FluxPylonsBlocks.ADV_PIPE, RenderType.cutout());
        
        MenuScreens.register(FluxPylonsContainerMenus.CRATE_CONTAINER_MENU, CrateGui::new);
        MenuScreens.register(FluxPylonsContainerMenus.PIPE_CONTAINER_MENU, PipeGui::new);
    }
}
