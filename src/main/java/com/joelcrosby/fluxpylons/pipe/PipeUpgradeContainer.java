package com.joelcrosby.fluxpylons.pipe;

import com.joelcrosby.fluxpylons.item.upgrade.UpgradeItem;
import com.joelcrosby.fluxpylons.network.NetworkManager;
import com.joelcrosby.fluxpylons.network.graph.GraphNode;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class PipeUpgradeContainer implements Container {
    private final PipeUpgradeItemStackHandler items;
    
    public PipeUpgradeContainer(GraphNode node) {
        this.items = createItemFilterInventory(node.getLevel());
    }
    
    public PipeUpgradeItemStackHandler getItems() {
        return items;
    }
    
    public List<UpgradeItem> getUpgrades() {
        var upgrades = new ArrayList<UpgradeItem>(items.getSlots());
        
        for (var i = 0; i < items.getSlots(); i++) {
            var inSlot = items.getStackInSlot(i);

            if (inSlot.isEmpty()) continue;

            var item = inSlot.getItem();

            if (item instanceof UpgradeItem) {
                upgrades.add((UpgradeItem) item);
            }
        }
        
        return upgrades;
    }

    public static PipeUpgradeItemStackHandler createItemFilterInventory(@Nullable Level level) {
        return new PipeUpgradeItemStackHandler() {
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
