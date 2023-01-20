package com.joelcrosby.fluxpylons.setup;

import com.joelcrosby.fluxpylons.ClientEvents;
import com.joelcrosby.fluxpylons.FluxPylons;
import com.joelcrosby.fluxpylons.FluxPylonsBlockEntities;
import com.joelcrosby.fluxpylons.FluxPylonsContainerMenus;
import com.joelcrosby.fluxpylons.crate.CrateGui;
import com.joelcrosby.fluxpylons.item.upgrade.filter.BasicFilterGui;
import com.joelcrosby.fluxpylons.item.upgrade.filter.FluidFilterGui;
import com.joelcrosby.fluxpylons.item.upgrade.filter.TagFilterGui;
import com.joelcrosby.fluxpylons.machine.SmelterGui;
import com.joelcrosby.fluxpylons.machine.WasherGui;
import com.joelcrosby.fluxpylons.network.PacketHandler;
import com.joelcrosby.fluxpylons.pipe.PipeRenderer;
import com.joelcrosby.fluxpylons.pipe.PipeUpgradeGui;
import com.joelcrosby.fluxpylons.pylon.PylonBlockEntityRenderer;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = FluxPylons.ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class Client {

    @SuppressWarnings("unused")
    public static void setup(final FMLClientSetupEvent event) {
        PacketHandler.register();

        MinecraftForge.EVENT_BUS.register(ClientEvents.class);
        
//        ItemBlockRenderTypes.setRenderLayer(FluxPylonsBlocks.BASIC_PIPE.get(), RenderType.cutout());
//        ItemBlockRenderTypes.setRenderLayer(FluxPylonsBlocks.ADV_PIPE.get(), RenderType.cutout());

        BlockEntityRenderers.register(FluxPylonsBlockEntities.ADV_PIPE.get(), PipeRenderer::new);
        BlockEntityRenderers.register(FluxPylonsBlockEntities.BASIC_PIPE.get(), PipeRenderer::new);

        MenuScreens.register(FluxPylonsContainerMenus.CRATE_CONTAINER_MENU.get(), CrateGui::new);
        MenuScreens.register(FluxPylonsContainerMenus.PIPE_UPGRADE_CONTAINER_MENU.get(), PipeUpgradeGui::new);
        MenuScreens.register(FluxPylonsContainerMenus.UPGRADE_FILTER_CONTAINER_MENU.get(), BasicFilterGui::new);
        MenuScreens.register(FluxPylonsContainerMenus.UPGRADE_FLUID_FILTER_CONTAINER_MENU.get(), FluidFilterGui::new);
        MenuScreens.register(FluxPylonsContainerMenus.UPGRADE_TAG_FILTER_CONTAINER_MENU.get(), TagFilterGui::new);
        MenuScreens.register(FluxPylonsContainerMenus.SMELTER_CONTAINER_MENU.get(), SmelterGui::new);
        MenuScreens.register(FluxPylonsContainerMenus.WASHER_CONTAINER_MENU.get(), WasherGui::new);
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(FluxPylonsBlockEntities.PYLON.get(), PylonBlockEntityRenderer::new);
    }
}
