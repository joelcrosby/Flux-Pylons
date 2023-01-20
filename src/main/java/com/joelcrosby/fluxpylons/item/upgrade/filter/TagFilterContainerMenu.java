package com.joelcrosby.fluxpylons.item.upgrade.filter;

import com.joelcrosby.fluxpylons.FluxPylonsContainerMenus;
import com.joelcrosby.fluxpylons.item.upgrade.filter.common.BaseFilterContainerMenu;
import com.joelcrosby.fluxpylons.item.upgrade.filter.common.FilterSlotHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.tuple.Pair;

public class TagFilterContainerMenu extends BaseFilterContainerMenu {
    
    @SuppressWarnings("unused")
    public TagFilterContainerMenu(int windowId, Inventory playerInventory, Player player, FriendlyByteBuf data) {
        this(windowId, player, data.readItem());
    }
    
    public TagFilterContainerMenu(int windowId, Player player, ItemStack filterItem) {
        super(FluxPylonsContainerMenus.UPGRADE_TAG_FILTER_CONTAINER_MENU.get(), windowId, player, filterItem);
    }

    @Override
    protected int getSlotCount() {
        return 1;
    }

    @Override
    protected Pair<Integer, Integer> getPlayerInventoryPosition() {
        return Pair.of(8, 140);
    }

    @Override
    protected void addOwnSlots() {
        this.addSlot(new FilterSlotHandler(this.itemStackHandler, 0, 8, 18));
    }
}
