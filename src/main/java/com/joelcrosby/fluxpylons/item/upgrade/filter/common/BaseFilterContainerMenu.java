package com.joelcrosby.fluxpylons.item.upgrade.filter.common;

import com.joelcrosby.fluxpylons.container.BaseContainerMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import org.apache.commons.lang3.tuple.Pair;

public abstract class BaseFilterContainerMenu extends BaseContainerMenu {
    protected final ItemStackHandler itemStackHandler;

    public ItemStack filterItem;

    @SuppressWarnings("unused")
    public BaseFilterContainerMenu(MenuType<?> menuType, int windowId, Inventory playerInventory, Player player, FriendlyByteBuf data) {
        this(menuType, windowId, player, data.readItem());
    }
    
    public BaseFilterContainerMenu(MenuType<?> menuType, int windowId, Player player, ItemStack filterItem) {
        super(menuType, windowId, player);
        
        this.itemStackHandler = BaseFilterItem.getInventory(filterItem);
        this.filterItem = filterItem;
        
        this.addOwnSlots();
        this.addPlayerInventory();
    }

    @Override
    protected int getSlotCount() {
        return 10;
    }

    @Override
    protected Pair<Integer, Integer> getPlayerInventoryPosition() {
        return Pair.of(8, 71);
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
    public void clicked(int slotId, int dragType, ClickType clickType, Player player) {
        if (slotId >= 0 && slotId < getSlotCount()) {
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
            if (index >= getSlotCount()) {
                for (int i = 0; i < getSlotCount(); i++) { // Prevents the same item from going in there more than once.
                    if (this.slots.get(i).getItem().equals(currentStack, false)) // Don't limit tags
                        return ItemStack.EMPTY;
                }
                if (!this.moveItemStackTo(currentStack, 0, getSlotCount(), false)) {
                    return ItemStack.EMPTY;
                }
            }
        }

        return stack;
    }
}
