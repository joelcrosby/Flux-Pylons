package com.joelcrosby.fluxpylons.item;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;

public class UpgradeItemStackHandler extends ItemStackHandler {

    public static final int SIZE = 10;

    public UpgradeItemStackHandler() {
        super(SIZE);
    }
    
    @Override
    public int getSlotLimit(int slot) {
        return 1;
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        return stack.getItem() instanceof UpgradeItem;
    }
}
