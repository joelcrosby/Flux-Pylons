package com.joelcrosby.fluxpylons.item.upgrade.filter.common;

import com.joelcrosby.fluxpylons.item.upgrade.filter.FluidFilterItem;
import com.joelcrosby.fluxpylons.util.FluidHelper;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

public class FluidFilterStackHandler extends ItemFilterStackHandler {
    public FluidFilterStackHandler(int size, ItemStack itemStack) {
        super(size, itemStack);
    }

    @Override
    public int getSlotLimit(int slot) {
        return 1;
    }

    @Override
    protected void onContentsChanged(int slot) {
        if (!stack.equals(ItemStack.EMPTY)) {
            FluidFilterItem.setInventory(stack, this);
        }
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        var fluidStack = FluidHelper.getFromStack(stack, true).getValue();
        
        if (fluidStack.isEmpty()) {
            return false;
        }
        
        for (var i = 0; i < getSlots(); i++) {
            var slotStack = getStackInSlot(i);

            if (slotStack.isEmpty()) continue;
            
            var fluidSlotStack = FluidHelper.getFromStack(slotStack, true).getValue();
            if (fluidSlotStack.isFluidEqual(fluidStack)) {
                return false;
            }
        }
        
        return true;
    }

    @Override
    public void setStackInSlot(int slot, @Nonnull ItemStack stack)
    {
        validateSlotIndex(slot);
        
        var fluidStack = FluidHelper.getFromStack(stack, true).getKey();
        this.stacks.set(slot, fluidStack);
        
        onContentsChanged(slot);
    }
}
