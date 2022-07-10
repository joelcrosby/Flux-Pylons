package com.joelcrosby.fluxpylons.pipe;

import com.joelcrosby.fluxpylons.FluxPylons;
import com.joelcrosby.fluxpylons.Utility;
import com.joelcrosby.fluxpylons.item.upgrade.UpgradeItem;
import com.joelcrosby.fluxpylons.item.upgrade.filter.TagFilterItem;
import com.joelcrosby.fluxpylons.item.upgrade.filter.common.BaseFilterItem;
import com.joelcrosby.fluxpylons.pipe.network.NetworkManager;
import com.joelcrosby.fluxpylons.pipe.network.graph.GraphNode;
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
import net.minecraftforge.network.NetworkHooks;

import java.util.List;


public class PipeUpgradeManager {
    private final GraphNode node;
    private final Direction dir;

    private int ticks;
    private static final int tickInterval = 10;

    public final PipeUpgradeContainer pipeUpgradeContainer;
    
    private PipeIoMode pipeIoMode = PipeIoMode.INSERT_EXTRACT;
    
    public PipeUpgradeManager(GraphNode node, Direction dir) {
        this.node = node;
        this.dir = dir;
        this.pipeUpgradeContainer = new PipeUpgradeContainer(node);
    }
    
    public void update() {
        var upgrades = pipeUpgradeContainer.getUpgrades();
        
        updateUpgradeItems(upgrades.extractFluids());
        updateUpgradeItems(upgrades.retrieverFluids());
        
        if (tickInterval != 0 && (ticks++) % tickInterval != 0) {
            return;
        }
        
        updateUpgradeItems(upgrades.extractItems());
        updateUpgradeItems(upgrades.retrieverItems());
    }

    private void updateUpgradeItems(List<ItemStack> upgrades) {
        for (var upgrade : upgrades) {
            if (!upgrade.isEmpty() && upgrade.getItem() instanceof UpgradeItem upgradeItem) {
                upgradeItem.update(upgrade, node, dir, node.getNodeType());
            }
        }
    }

    public void setPipeIoMode(PipeIoMode ioMode) {
        this.pipeIoMode = ioMode;
        NetworkManager.get(node.getLevel()).setDirty();
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
                        new PipeUpgradeContainerMenu(windowId, player, pipeUpgradeContainer.getItems(), node.getPos(), dir, this.pipeIoMode), containerName),
                buffer -> {
                    buffer.writeBlockPos(node.getPos());
                    buffer.writeByte(dir.ordinal());
                    buffer.writeByte(pipeIoMode.ordinal());
                }
        );
    }
    
    public void dropContents(Level level, BlockPos pos) {
        Containers.dropContents(level, pos, pipeUpgradeContainer);
    }
    
    public boolean insertUpgrade(ItemStack itemStack) {
        return pipeUpgradeContainer.insertItem(itemStack);
    }
    
    public Tag serializeNBT() {
        var tag = new CompoundTag();
        tag.put("upgradeItems", pipeUpgradeContainer.getItems().serializeNBT());
        tag.putInt("io-mode", pipeIoMode.ordinal());
        
        return tag;
    }

    public void deserializeNBT(CompoundTag tag) {
        if (tag == null) {
            return;
        }
        
        this.pipeIoMode = PipeIoMode.values()[tag.getInt("io-mode")];
        
        pipeUpgradeContainer.getItems().deserializeNBT(tag.getCompound("upgradeItems"));
    }
    
    public boolean IsValidDestination(ItemStack itemStack) {
        if (this.pipeIoMode == PipeIoMode.DISABLED || this.pipeIoMode == PipeIoMode.EXTRACT) {
            return false;
        }
        
        var isFiltered = !getFilterUpgrades().isEmpty();
        if (!isFiltered) {
            return true;
        }
        
        return getFilterUpgrades().stream().anyMatch(filter -> IsValidDestination(itemStack, filter));
    }

    private boolean IsValidDestination(ItemStack itemStack, ItemStack filter) {
        var isDenyList = BaseFilterItem.getIsDenyList(filter);
        var matchNbt = BaseFilterItem.getMatchNbt(filter);

        var anyMatch = this.pipeUpgradeContainer.getUpgrades()
                .filterItems()
                .stream()
                .anyMatch(filterItem ->  {
                    if (filterItem.getItem() instanceof TagFilterItem) {
                        var tags = TagFilterItem.getTags(filterItem);
                        return itemStack.getTags().map(t -> t.location().toString()).anyMatch(tags::contains);
                    } else {
                        var inventory = BaseFilterItem.getInventory(filterItem);
                        return Utility.matchesFilterInventory(inventory, itemStack, matchNbt);
                    }
                });
        
        return isDenyList != anyMatch;
    }

    public boolean IsValidDestination(FluidStack fluidStack) {
        if (this.pipeIoMode == PipeIoMode.DISABLED || this.pipeIoMode == PipeIoMode.EXTRACT) {
            return false;
        }
        
        var isFiltered = !getFluidFilterUpgrades().isEmpty();
        if (!isFiltered) {
            return true;
        }

        return getFluidFilterUpgrades().stream().anyMatch(filter -> IsValidDestination(fluidStack, filter));
    }
    
    private boolean IsValidDestination(FluidStack fluidStack, ItemStack filter) {
        var isDenyList = BaseFilterItem.getIsDenyList(filter);

        var anyMatch = this.pipeUpgradeContainer.getUpgrades()
                .filterFluids()
                .stream()
                .anyMatch(filterItem ->  {
                    var inventory = BaseFilterItem.getInventory(filterItem);
                    return Utility.matchesFilterInventory(inventory, fluidStack);
                });

        return isDenyList != anyMatch;
    }

    public boolean canExtract() {
        return this.pipeIoMode == PipeIoMode.EXTRACT || this.pipeIoMode == PipeIoMode.INSERT_EXTRACT;
    }

    public boolean canInsert() {
        return this.pipeIoMode == PipeIoMode.INSERT_EXTRACT || this.pipeIoMode == PipeIoMode.INSERT;
    }
}
