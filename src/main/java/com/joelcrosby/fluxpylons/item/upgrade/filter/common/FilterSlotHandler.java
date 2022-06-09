package com.joelcrosby.fluxpylons.item.upgrade.filter.common;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

public class FilterSlotHandler extends SlotItemHandler {
    protected boolean enabled = true;

    public FilterSlotHandler(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public boolean mayPickup(Player player) {
        return false;
    }

    @Override
    public boolean isActive() {
        return enabled;
    }

    @Override
    public void set(@Nonnull ItemStack stack)
    {
        if (!stack.isEmpty() && !this.getItemHandler().isItemValid(getSlotIndex(), stack)) {
            return;
        }
        
        super.set(stack);
    }
    
    public FilterSlotHandler setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }
}
