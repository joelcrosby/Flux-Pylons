package com.joelcrosby.fluxpylons.item.upgrade.filter;

import com.joelcrosby.fluxpylons.FluxPylonsContainerMenus;
import com.joelcrosby.fluxpylons.container.BaseContainerMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;

public class FilterContainerMenu extends BaseContainerMenu {
    protected final ItemStackHandler itemStackHandler;

    public ItemStack filterItem;

    public FilterContainerMenu(int windowId, Inventory playerInventory, Player player, FriendlyByteBuf data) {
        this(windowId, playerInventory, player, data.readItem());
    }
    
    public FilterContainerMenu(int windowId, Inventory playerInventory, Player player, ItemStack filterItem) {
        super(FluxPylonsContainerMenus.UPGRADE_FILTER_CONTAINER_MENU, windowId, player, 10);
        
        this.itemStackHandler = FilterItem.getInventory(filterItem);
        this.filterItem = ItemStack.EMPTY;
        
        this.addOwnSlots();
        this.addPlayerInventory(8, 71);
    }

    protected void addOwnSlots() {
        var off = 18 * 2;
        var y = 18;
        
        var slot = -1;
        
        for (var i = 0; i < this.itemStackHandler.getSlots() / 5; i++) {
            for (var j = 0; j < 5; j++) {
                slot++;
                this.addSlot(new FilterSlotHandler(this.itemStackHandler, slot, 8 + off + j * 18, y + i * 18));
            }
        }
    }
    
    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void clicked(int slotId, int dragType, ClickType clickType, Player player) {
        if (slotId >= 0 && slotId < slotCount) {
            return;
        }
        
        super.clicked(slotId, dragType, clickType, player);
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        var stack = ItemStack.EMPTY;
        var slot = this.slots.get(index);
        
        if (slot != null && slot.hasItem()) {
            var currentStack = slot.getItem().copy();
            
            if (ItemHandlerHelper.canItemStacksStack(currentStack, filterItem)) {
                return ItemStack.EMPTY;
            }
            
            currentStack.setCount(1);
            
            // Only do this if we click from the players inventory
            if (index >= slotCount) {
                for (int i = 0; i < slotCount; i++) { // Prevents the same item from going in there more than once.
                    if (this.slots.get(i).getItem().equals(currentStack, false)) // Don't limit tags
                        return ItemStack.EMPTY;
                }
                if (!this.moveItemStackTo(currentStack, 0, slotCount, false)) {
                    return ItemStack.EMPTY;
                }
            }
        }

        return stack;
    }
}
