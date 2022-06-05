package com.joelcrosby.fluxpylons.pipe;

import com.joelcrosby.fluxpylons.FluxPylons;
import com.joelcrosby.fluxpylons.pipe.network.graph.GraphNode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;


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
        
        for (var upgrade : upgrades.fluids()) {
            upgrade.update(node, dir, node.getNodeType());
        }
        
        if (tickInterval != 0 && (ticks++) % tickInterval != 0) {
            return;
        }
        
        for (var upgrade : upgrades.items()) {
            upgrade.update(node, dir, node.getNodeType());
        }
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
