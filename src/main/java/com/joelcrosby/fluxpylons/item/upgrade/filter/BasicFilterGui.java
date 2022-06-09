package com.joelcrosby.fluxpylons.item.upgrade.filter;

import com.joelcrosby.fluxpylons.item.upgrade.filter.common.BaseFilterGui;
import com.joelcrosby.fluxpylons.item.upgrade.filter.common.ItemFilterContainerMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class BasicFilterGui extends BaseFilterGui<ItemFilterContainerMenu> {
    public BasicFilterGui(ItemFilterContainerMenu container, Inventory inv, Component titleIn) {
        super(container, inv, titleIn);
    }
}
