package com.joelcrosby.fluxpylons.item.upgrade.filter.common;

import com.joelcrosby.fluxpylons.util.FluidHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;

public class FluidFilterSlotHandler extends FilterSlotHandler {

    public FluidFilterSlotHandler(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
    }
    
    @Override
    public void set(@Nonnull ItemStack stack)
    {
        if (!stack.isEmpty() && !this.getItemHandler().isItemValid(getSlotIndex(), stack)) {
            return;
        }
        
        var fluidStack = FluidHelper.getFromStack(stack, true).getValue();
        super.set(fluidStack.getFluid().getBucket().getDefaultInstance());
    }
}
