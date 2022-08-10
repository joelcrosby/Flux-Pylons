package com.joelcrosby.fluxpylons.container;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nullable;

public class BaseEnergySlot extends SlotItemHandler {
    public BaseEnergySlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
    
    @Override
    public boolean mayPlace(@Nullable ItemStack stack){
        return stack.getCapability(CapabilityEnergy.ENERGY).isPresent();
    }
}
