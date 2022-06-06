package com.joelcrosby.fluxpylons.pipe;

import com.joelcrosby.fluxpylons.item.upgrade.extract.UpgradeExtractItem;
import com.joelcrosby.fluxpylons.item.upgrade.extract.UpgradeFluidExtractItem;
import com.joelcrosby.fluxpylons.item.upgrade.filter.UpgradeFilterItem;
import com.joelcrosby.fluxpylons.item.upgrade.filter.UpgradeFluidFilterItem;
import com.joelcrosby.fluxpylons.pipe.network.NetworkManager;
import com.joelcrosby.fluxpylons.pipe.network.graph.GraphNode;
import com.joelcrosby.fluxpylons.util.FluidHelper;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class PipeUpgradeContainer implements Container {
    private PipeUpgrades upgrades;
    private final Level level;
    
    private final PipeUpgradeItemStackHandler items = new PipeUpgradeItemStackHandler() {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            updateUpgrades();
        }
        
        private void updateUpgrades() {
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
        var extractItems = new ArrayList<ItemStack>();
        var extractFluids = new ArrayList<ItemStack>();
        var filterItems = new ArrayList<ItemStack>();
        var filterFluids = new ArrayList<ItemStack>();

        for (var i = 0; i < items.getSlots(); i++) {
            var stack = items.getStackInSlot(i);

            if (stack.isEmpty()) continue;

            var item = stack.getItem();

            if (item instanceof UpgradeExtractItem)
                extractItems.add(stack);
            if (item instanceof UpgradeFluidExtractItem)
                extractFluids.add(stack);
            if (item instanceof UpgradeFilterItem)
                filterItems.add(stack);
            if (item instanceof UpgradeFluidFilterItem)
                filterFluids.add(stack);
        }

        var filterRegistryNames = getFilterItemRegistryNames(filterItems);
        var fluids = getFilterFluidRegistryNames(filterFluids);
        
        return new PipeUpgrades(extractItems, extractFluids, filterItems, filterRegistryNames, filterFluids, fluids);
    }
    
    private HashSet<String> getFilterItemRegistryNames(List<ItemStack> filters) {
        return filters.stream()
                .map(stack -> {
                    var names = new HashSet<String>();
                    var inventory = UpgradeFilterItem.getInventory(stack);

                    for (var i = 0; i < inventory.getSlots(); i++) {
                        var slotStack = inventory.getStackInSlot(i);

                        if (slotStack.isEmpty()) continue;

                        names.add(slotStack.getItem().getDescriptionId());
                    }

                    return names;
                })
                .reduce(new HashSet<>(), (prev, curr) -> { prev.addAll(curr); return prev; });
    }

    private HashSet<String> getFilterFluidRegistryNames(List<ItemStack> filters) {
        return filters.stream()
                .map(stack -> {
                    var names = new HashSet<String>();
                    var inventory = UpgradeFluidFilterItem.getInventory(stack);

                    for (var i = 0; i < inventory.getSlots(); i++) {
                        var slotStack = inventory.getStackInSlot(i);

                        if (slotStack.isEmpty()) continue;

                        names.add(FluidHelper.getFromStack(slotStack, true).getValue().getFluid().getRegistryName().toString());
                    }

                    return names;
                })
                .reduce(new HashSet<>(), (prev, curr) -> { prev.addAll(curr); return prev; });
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
    public void setChanged() {
        this.upgrades = cacheUpgrades();

        if (level != null && !level.isClientSide) {
            NetworkManager.get(level).setDirty();
        }
    }

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
