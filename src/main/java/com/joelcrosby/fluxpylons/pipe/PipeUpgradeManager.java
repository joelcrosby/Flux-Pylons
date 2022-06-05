package com.joelcrosby.fluxpylons.pipe;

import com.joelcrosby.fluxpylons.FluxPylons;
import com.joelcrosby.fluxpylons.item.upgrade.UpgradeItem;
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
import net.minecraftforge.network.NetworkHooks;

import java.util.List;
import java.util.Set;


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
            var upgradeItem = (UpgradeItem) upgrade.getItem();
            upgradeItem.update(upgrade, node, dir, node.getNodeType());
        }
        
        if (tickInterval != 0 && (ticks++) % tickInterval != 0) {
            return;
        }
        
        for (var upgrade : upgrades.extractItems()) {
            var upgradeItem = (UpgradeItem) upgrade.getItem();
            upgradeItem.update(upgrade, node, dir, node.getNodeType());
        }
    }
    
    public List<ItemStack> getFilterUpgrades() {
        return this.pipeUpgradeContainer.getUpgrades().filters();
    }

    public List<ItemStack> getFluidFilterUpgrades() {
        return this.pipeUpgradeContainer.getUpgrades().fluidFilters();
    }
    
    public Set<String> getFilterItemNames() {
        return this.pipeUpgradeContainer.getUpgrades().filterItemRegistryNames();
    }    
    
    public Set<String> getFilterFluids() {
        return this.pipeUpgradeContainer.getUpgrades().filterFluidRegistryNames();
    }
    
    public void OpenContainerMenu(ServerPlayer player) {
        var containerName = new TranslatableComponent("container." + FluxPylons.ID + "." + node.getNodeType().getEntityType().getId());
        
        NetworkHooks.openGui(player,
                new SimpleMenuProvider((windowId, playerInventory, playerEntity) ->
                        new PipeUpgradeContainerMenu(windowId, player, pipeUpgradeContainer.getItems()), containerName),
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
}
