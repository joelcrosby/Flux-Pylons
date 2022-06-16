package com.joelcrosby.fluxpylons.item.upgrade.filter;

import com.joelcrosby.fluxpylons.item.upgrade.filter.common.BaseFilterGui;
import com.joelcrosby.fluxpylons.item.upgrade.filter.common.FluidFilterContainerMenu;
import com.joelcrosby.fluxpylons.util.FluidStackRenderer;
import com.joelcrosby.fluxpylons.util.Vector2D;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class FluidFilterGui extends BaseFilterGui<FluidFilterContainerMenu> {
    
    
    public FluidFilterGui(FluidFilterContainerMenu container, Inventory inv, Component titleIn) {
        super(container, inv, titleIn);
    }

    @Override
    public void init() {
        super.init();
        
        var renderer = new BlockEntityWithoutLevelRenderer(minecraft.getBlockEntityRenderDispatcher(), minecraft.getEntityModels());
        
        
        
        this.itemRenderer = new FluidStackRenderer(minecraft.getTextureManager(),
                minecraft.getModelManager(),
                minecraft.getItemColors(),
                renderer,
                new Vector2D(43, 17),
                new Vector2D(132, 52),
                this);
    }
}
