package com.joelcrosby.fluxpylons.setup;

import com.joelcrosby.fluxpylons.FluxPylonsBlockEntities;
import com.joelcrosby.fluxpylons.FluxPylonsBlocks;
import com.joelcrosby.fluxpylons.FluxPylonsContainerMenus;
import com.joelcrosby.fluxpylons.crate.CrateGui;
import com.joelcrosby.fluxpylons.item.upgrade.filter.UpgradeFilterGui;
import com.joelcrosby.fluxpylons.item.upgrade.filter.UpgradeFluidFilterGui;
import com.joelcrosby.fluxpylons.network.PacketHandler;
import com.joelcrosby.fluxpylons.pipe.PipeRenderer;
import com.joelcrosby.fluxpylons.pipe.PipeUpgradeGui;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public final class Client {

    @SuppressWarnings("unused")
    public static void setup(final FMLClientSetupEvent event) {
        PacketHandler.register();
        
        ItemBlockRenderTypes.setRenderLayer(FluxPylonsBlocks.BASIC_PIPE, RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(FluxPylonsBlocks.ADV_PIPE, RenderType.cutout());

        BlockEntityRenderers.register(FluxPylonsBlockEntities.ADV_PIPE, PipeRenderer::new);
        BlockEntityRenderers.register(FluxPylonsBlockEntities.BASIC_PIPE, PipeRenderer::new);
        
        MenuScreens.register(FluxPylonsContainerMenus.CRATE_CONTAINER_MENU, CrateGui::new);
        MenuScreens.register(FluxPylonsContainerMenus.PIPE_UPGRADE_CONTAINER_MENU, PipeUpgradeGui::new);
        MenuScreens.register(FluxPylonsContainerMenus.UPGRADE_FILTER_CONTAINER_MENU, UpgradeFilterGui::new);
        MenuScreens.register(FluxPylonsContainerMenus.UPGRADE_FLUID_FILTER_CONTAINER_MENU, UpgradeFluidFilterGui::new);
    }
}
