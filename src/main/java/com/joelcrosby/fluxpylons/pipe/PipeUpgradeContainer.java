package com.joelcrosby.fluxpylons.pipe;

import com.joelcrosby.fluxpylons.item.upgrade.extract.UpgradeExtractItem;
import com.joelcrosby.fluxpylons.item.upgrade.extract.UpgradeFluidExtractItem;
import com.joelcrosby.fluxpylons.item.upgrade.filter.UpgradeFilterItem;
import com.joelcrosby.fluxpylons.pipe.network.NetworkManager;
import com.joelcrosby.fluxpylons.pipe.network.graph.GraphNode;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.ArrayList;

public class PipeUpgradeContainer implements Container {
    private PipeUpgrades upgrades;
    private final Level level;
    private final PipeUpgradeItemStackHandler items = new PipeUpgradeItemStackHandler() {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);

            upgrades = cacheUpgrades();

            if (level != null && !level.isClientSide) {
                NetworkManager.get(level).setDirty();
            }
        }
    };
    
    public PipeUpgradeContainer(GraphNode node) {
        this.level = node.getLevel();
    }
    
    public PipeUpgradeItemStackHandler getItems() {
        return items;
    }
    
    public PipeUpgrades getUpgrades() {
        if (this.upgrades != null) {
            return this.upgrades;
        }
        
        this.upgrades = cacheUpgrades();
        return this.upgrades;
    }
    
    private PipeUpgrades cacheUpgrades() {
        var itemUpgrades = new ArrayList<UpgradeExtractItem>();
        var fluidUpgrades = new ArrayList<UpgradeFluidExtractItem>();
        var filterUpgrades = new ArrayList<UpgradeFilterItem>();

        for (var i = 0; i < items.getSlots(); i++) {
            var inSlot = items.getStackInSlot(i);

            if (inSlot.isEmpty()) continue;

            var item = inSlot.getItem();

            if (item instanceof UpgradeExtractItem ue)
                itemUpgrades.add(ue);
            if (item instanceof UpgradeFluidExtractItem uf)
                fluidUpgrades.add(uf);
            if (item instanceof UpgradeFilterItem ufi)
                filterUpgrades.add(ufi);
        }

        return new PipeUpgrades(itemUpgrades, fluidUpgrades, filterUpgrades);
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
