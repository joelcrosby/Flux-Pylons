package com.joelcrosby.fluxpylons.item.upgrade.filter.common;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

public class ItemFilterStackHandler extends ItemStackHandler  {
    public final ItemStack stack;

    public ItemFilterStackHandler(int size, ItemStack itemStack) {
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
            BaseFilterItem.setInventory(stack, this);
        }
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }

        for (var i = 0; i < getSlots(); i++) {
            var slotStack = getStackInSlot(i);

            if (slotStack.isEmpty()) continue;
            
            if (ItemHandlerHelper.canItemStacksStack(slotStack, stack)) {
                return false;
            }
        }

        return true;
    }
}
