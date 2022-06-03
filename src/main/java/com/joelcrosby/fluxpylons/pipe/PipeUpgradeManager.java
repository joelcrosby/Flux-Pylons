package com.joelcrosby.fluxpylons.pipe;

import com.joelcrosby.fluxpylons.FluxPylons;
import com.joelcrosby.fluxpylons.FluxPylonsContainerMenus;
import com.joelcrosby.fluxpylons.network.graph.GraphNode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;


public class PipeUpgradeManager implements MenuProvider {
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
        if (tickInterval != 0 && (ticks++) % tickInterval != 0) {
            return;
        }
        
        for (var upgrade : pipeUpgradeContainer.getUpgrades()) {
            upgrade.update(node, dir);
        }
    }
    
    public void OpenContainerMenu(ServerPlayer player) {
        NetworkHooks.openGui(player, this, buffer -> {
            buffer.writeBlockPos(node.getPos());
            buffer.writeByte(dir.ordinal());
        });
    }

    @Override
    public AbstractContainerMenu createMenu(int window, Inventory inventory, Player player) {
        return new PipeUpgradeContainerMenu(FluxPylonsContainerMenus.PIPE_UPGRADE_CONTAINER_MENU, window, player, node.getPos(), pipeUpgradeContainer.getItems());
    }

    @Override
    public Component getDisplayName() {
        return new TranslatableComponent("container." + FluxPylons.ID + "." + node.getNodeType().getEntityType().getId());
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
