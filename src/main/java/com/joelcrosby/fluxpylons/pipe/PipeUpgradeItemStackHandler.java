package com.joelcrosby.fluxpylons.pipe;

import com.joelcrosby.fluxpylons.item.upgrade.UpgradeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;

public class PipeUpgradeItemStackHandler extends ItemStackHandler {

    public static final int SIZE = 10;

    public PipeUpgradeItemStackHandler() {
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
