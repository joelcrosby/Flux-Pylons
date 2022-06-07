package com.joelcrosby.fluxpylons.item.upgrade.filter;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

public class FilterItemStackHandler extends ItemStackHandler  {
    public ItemStack stack;

    public FilterItemStackHandler(int size, ItemStack itemStack) {
        super(size);
        this.stack = itemStack;
    }

    @Override
    public int getSlotLimit(int slot) {
        return 1;
    }

    @Override
    protected void onContentsChanged(int slot) {
        if (!stack.equals(ItemStack.EMPTY)) {
            FilterItem.setInventory(stack, this);
        }
    }
}
