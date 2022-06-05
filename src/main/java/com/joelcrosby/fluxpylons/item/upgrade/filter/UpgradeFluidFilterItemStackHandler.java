package com.joelcrosby.fluxpylons.item.upgrade.filter;

import com.joelcrosby.fluxpylons.util.FluidHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

public class UpgradeFluidFilterItemStackHandler extends ItemStackHandler {
    public ItemStack stack;

    public UpgradeFluidFilterItemStackHandler(int size, ItemStack itemStack) {
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
            UpgradeFluidFilterItem.setInventory(stack, this);
        }
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return !FluidHelper.getFromStack(stack, true).getValue().isEmpty();
    }

    @Override
    public void setStackInSlot(int slot, @Nonnull ItemStack stack)
    {
        validateSlotIndex(slot);
        
        var fluidStack = FluidHelper.getFromStack(stack, true).getKey();
        this.stacks.set(slot, fluidStack);
        
        onContentsChanged(slot);
    }
    
    @Override
    @Nonnull
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate)
    {
        setStackInSlot(slot, stack);

        return ItemStack.EMPTY;
    }
}
