package com.joelcrosby.fluxpylons.item.upgrade.filter;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class UpgradeFilterContainer implements Container {
    public static final int SLOTS = 10;
    
    private final UpgradeFilterItemStackHandler items;
    
    public UpgradeFilterContainer() {
        this.items = createFilterInventory();
    }
    
    public UpgradeFilterItemStackHandler getItems() {
        return items;
    }

    public static UpgradeFilterItemStackHandler createFilterInventory() {
        return new UpgradeFilterItemStackHandler(UpgradeFilterContainer.SLOTS, ItemStack.EMPTY);
    }
    
    @Override
    public int getContainerSize() {
        return this.items.getSlots();
    }

    @Override
    public boolean isEmpty() {
        for (var i = 0; i < items.getSlots(); i++) {
            var inSlot = items.getStackInSlot(i);

            if (!inSlot.isEmpty()) return false;
        }

        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        return items.getStackInSlot(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        return items.extractItem(slot, amount, false);
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return items.extractItem(slot, items.getStackInSlot(slot).getCount(), true);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        items.setStackInSlot(slot, stack);
    }

    @Override
    public void setChanged() {}

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void clearContent() {
        for (var i = 0; i < this.items.getSlots(); i++)
            this.items.setStackInSlot(i, ItemStack.EMPTY);
    }
}
