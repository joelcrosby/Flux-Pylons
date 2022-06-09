package com.joelcrosby.fluxpylons.item.upgrade.filter.common;

import com.joelcrosby.fluxpylons.FluxPylonsContainerMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ItemFilterContainerMenu extends BaseFilterContainerMenu {

    public ItemFilterContainerMenu(int windowId, Inventory playerInventory, Player player, FriendlyByteBuf data) {
        this(windowId, playerInventory, player, data.readItem());
    }

    public ItemFilterContainerMenu(int windowId, Inventory playerInventory, Player player, ItemStack filterItem) {
        super(FluxPylonsContainerMenus.UPGRADE_FILTER_CONTAINER_MENU, windowId, playerInventory, player, filterItem);
    }
}
