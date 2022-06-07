package com.joelcrosby.fluxpylons.pipe;

import com.joelcrosby.fluxpylons.FluxPylons;
import com.joelcrosby.fluxpylons.item.upgrade.UpgradeItem;
import com.joelcrosby.fluxpylons.item.upgrade.filter.FilterItem;
import com.joelcrosby.fluxpylons.pipe.network.graph.GraphNode;
import com.joelcrosby.fluxpylons.util.FluidHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.network.NetworkHooks;

import java.util.List;


public class PipeUpgradeManager {
    private final GraphNode node;
    private final Direction dir;

    private int ticks;
    private static final int tickInterval = 10;

    public final PipeUpgradeContainer pipeUpgradeContainer;
    
    public PipeUpgradeManager(GraphNode node, Direction dir) {
        this.node = node;
        this.dir = dir;
        this.pipeUpgradeContainer = new PipeUpgradeContainer(node);
    }
    
    public void update() {
        var upgrades = pipeUpgradeContainer.getUpgrades();
        
        for (var upgrade : upgrades.extractFluids()) {
            if (!upgrade.isEmpty() && upgrade.getItem() instanceof UpgradeItem upgradeItem) {
                upgradeItem.update(upgrade, node, dir, node.getNodeType());
            }
        }
        
        if (tickInterval != 0 && (ticks++) % tickInterval != 0) {
            return;
        }
        
        for (var upgrade : upgrades.extractItems()) {
            if (!upgrade.isEmpty() && upgrade.getItem() instanceof UpgradeItem upgradeItem) {
                upgradeItem.update(upgrade, node, dir, node.getNodeType());
            }
        }
    }
    
    public List<ItemStack> getFilterUpgrades() {
        return this.pipeUpgradeContainer.getUpgrades().filterItems();
    }

    public List<ItemStack> getFluidFilterUpgrades() {
        return this.pipeUpgradeContainer.getUpgrades().filterFluids();
    }
    
    public void OpenContainerMenu(ServerPlayer player) {
        var containerName = new TranslatableComponent("container." + FluxPylons.ID + "." + node.getNodeType().getEntityType().getId());
        
        NetworkHooks.openGui(player,
                new SimpleMenuProvider((windowId, playerInventory, playerEntity) ->
                        new PipeUpgradeContainerMenu(windowId, player, pipeUpgradeContainer.getItems(), node.getPos(), dir), containerName),
                buffer -> {
                    buffer.writeBlockPos(node.getPos());
                    buffer.writeByte(dir.ordinal());
        });
    }
    
    public void dropContents(Level level, BlockPos pos) {
        Containers.dropContents(level, pos, pipeUpgradeContainer);
    }
    
    public Tag serializeNBT() {
        var tag = new CompoundTag();
        tag.put("upgradeItems", pipeUpgradeContainer.getItems().serializeNBT());
        
        return tag;
    }

    public void deserializeNBT(CompoundTag tag) {
        if (tag == null) {
            return;
        }
        
        pipeUpgradeContainer.getItems().deserializeNBT(tag.getCompound("upgradeItems"));
    }
    
    public boolean IsValidDestination(ItemStack itemStack) {
        var isFiltered = !getFilterUpgrades().isEmpty();
        if (!isFiltered) {
            return true;
        }
        
        return getFilterUpgrades().stream().anyMatch(filter -> IsValidDestination(itemStack, filter));
    }

    private boolean IsValidDestination(ItemStack itemStack, ItemStack filter) {
        var isDenyList = filter.getOrCreateTag().getBoolean("is-deny-list");
        var matchNbt = filter.getOrCreateTag().getBoolean("match-nbt");

        var anyMatch = this.pipeUpgradeContainer.getUpgrades()
                .filterItems()
                .stream()
                .anyMatch(filterItem ->  {

                    var inventory = FilterItem.getInventory(filterItem);
                    var isMatch = false;
                    
                    for (var i = 0; i < inventory.getSlots(); i++) {
                        var slotStack = inventory.getStackInSlot(i);

                        if (slotStack.isEmpty()) continue;

                        if (matchNbt) {
                            isMatch = ItemHandlerHelper.canItemStacksStack(itemStack, slotStack);
                        } else {
                            isMatch = itemStack.sameItem(slotStack);
                        }
                        
                        if (isMatch) break;
                    }

                    return isMatch;
                });
        
        return isDenyList != anyMatch;
    }

    public boolean IsValidDestination(FluidStack fluidStack) {
        var isFiltered = !getFluidFilterUpgrades().isEmpty();
        if (!isFiltered) {
            return true;
        }

        return getFluidFilterUpgrades().stream().anyMatch(filter -> IsValidDestination(fluidStack, filter));
    }
    
    private boolean IsValidDestination(FluidStack fluidStack, ItemStack filter) {
        var isDenyList = filter.getOrCreateTag().getBoolean("is-deny-list");

        var anyMatch = this.pipeUpgradeContainer.getUpgrades()
                .filterItems()
                .stream()
                .anyMatch(filterItem ->  {

                    var inventory = FilterItem.getInventory(filterItem);
                    var isMatch = false;

                    for (var i = 0; i < inventory.getSlots(); i++) {
                        var slotStack = inventory.getStackInSlot(i);

                        if (slotStack.isEmpty()) continue;

                        var slotFluidStack = FluidHelper.getFromStack(slotStack, true).getValue();
                        
                        if (slotFluidStack == null) continue;
                        
                        isMatch = slotFluidStack.isFluidEqual(fluidStack);

                        if (isMatch) break;
                    }

                    return isMatch;
                });

        return isDenyList != anyMatch;
    }
}
