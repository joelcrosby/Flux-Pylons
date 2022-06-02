package com.joelcrosby.fluxpylons.item.upgrade.filter;

import com.joelcrosby.fluxpylons.network.NetworkManager;
import com.joelcrosby.fluxpylons.network.graph.GraphNode;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public class UpgradeFilterContainer implements Container {
    private final UpgradeFilterItemStackHandler items;
    
    public UpgradeFilterContainer(GraphNode node) {
        this.items = createFilterInventory(node.getLevel());
    }
    
    public UpgradeFilterItemStackHandler getItems() {
        return items;
    }

    public static UpgradeFilterItemStackHandler createFilterInventory(@Nullable Level level) {
        return new UpgradeFilterItemStackHandler() {
            @Override
            protected void onContentsChanged(int slot) {
                super.onContentsChanged(slot);
                
                if (level != null && !level.isClientSide) {
                    NetworkManager.get(level).setDirty();
                }
            }
        };
    }
    
    @Override
    public int getContainerSize() {
        return this.items.getSlots();
    }

    @Override
    public boolean isEmpty() {
        for (var i = 0; i < items.getSlots(); i++) {
            var inSlot = items.getStackInSlot(i);

            if (!inSlot.isEmpty()) return false;
        }

        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        return items.getStackInSlot(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        return items.extractItem(slot, amount, false);
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return items.extractItem(slot, items.getStackInSlot(slot).getCount(), true);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        items.setStackInSlot(slot, stack);
    }

    @Override
    public void setChanged() {}

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void clearContent() {
        for (var i = 0; i < this.items.getSlots(); i++)
            this.items.setStackInSlot(i, ItemStack.EMPTY);
    }
}
