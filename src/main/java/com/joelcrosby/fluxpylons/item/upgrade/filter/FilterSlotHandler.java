package com.joelcrosby.fluxpylons.item.upgrade.filter;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

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

    public FilterSlotHandler setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }
}
