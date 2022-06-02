package com.joelcrosby.fluxpylons.item;

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


public class UpgradeManager implements MenuProvider {
    private final GraphNode node;
    private final Direction dir;

    private int ticks;
    private static final int tickInterval = 20;

    public final UpgradeContainer upgradeContainer;
    
    public UpgradeManager(GraphNode node, Direction dir) {
        this.node = node;
        this.dir = dir;
        this.upgradeContainer = new UpgradeContainer(node);
    }
    
    public void update() {
        if (tickInterval != 0 && (ticks++) % tickInterval != 0) {
            return;
        }
        
        for (var upgrade : upgradeContainer.getUpgrades()) {
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
        return new UpgradeContainerMenu(FluxPylonsContainerMenus.UPGRADE_CONTAINER_MENU, window, player, node.getPos(), upgradeContainer.getItems());
    }

    @Override
    public Component getDisplayName() {
        return new TranslatableComponent("container." + FluxPylons.ID + "." + node.getNodeType().getEntityType().getId());
    }
    
    public void dropContents(Level level, BlockPos pos) {
        Containers.dropContents(level, pos, upgradeContainer);
    }
    
    public Tag serializeNBT() {
        var tag = new CompoundTag();
        tag.put("upgradeItems", upgradeContainer.getItems().serializeNBT());
        
        return tag;
    }

    public void deserializeNBT(CompoundTag tag) {
        if (tag == null) {
            return;
        }
        
        upgradeContainer.getItems().deserializeNBT(tag.getCompound("upgradeItems"));
    }
}
