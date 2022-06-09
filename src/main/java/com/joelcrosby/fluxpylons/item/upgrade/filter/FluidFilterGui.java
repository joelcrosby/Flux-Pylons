package com.joelcrosby.fluxpylons.item.upgrade.filter;

import com.joelcrosby.fluxpylons.item.upgrade.filter.common.BaseFilterGui;
import com.joelcrosby.fluxpylons.item.upgrade.filter.common.FluidFilterContainerMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class FluidFilterGui extends BaseFilterGui<FluidFilterContainerMenu> {
    public FluidFilterGui(FluidFilterContainerMenu container, Inventory inv, Component titleIn) {
        super(container, inv, titleIn);
    }
}
