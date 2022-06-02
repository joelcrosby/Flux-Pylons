package com.joelcrosby.fluxpylons.network.graph;

import com.joelcrosby.fluxpylons.FluxPylons;
import com.joelcrosby.fluxpylons.item.UpgradeManager;
import com.joelcrosby.fluxpylons.network.Network;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class GraphNode {
    protected final Level level;
    protected final BlockPos pos;
    protected Network network;
    protected GraphNodeType nodeType;

    public static final ResourceLocation ID = new ResourceLocation(FluxPylons.ID, "energy");
    
    private final Logger logger = LogManager.getLogger(getClass());

    public final Map<Direction, UpgradeManager> upgrades = new HashMap<>();
    
    public GraphNode(Level level, BlockPos pos, GraphNodeType nodeType) {
        this.level = level;
        this.pos = pos;
        this.nodeType = nodeType;

        for (var dir : Direction.values()) {
            this.upgrades.put(dir, new UpgradeManager(this, dir));
        }
    }

    public Level getLevel() {
        return level;
    }
    
    public BlockPos getPos() {
        return pos;
    }
    
    public Network getNetwork() {
        return network;
    }
    
    public ResourceLocation getId() {
        return ID;
    }

    public GraphNodeType getNodeType() {
        return this.nodeType;
    }
    
    public UpgradeManager getUpgradeManager(Direction dir) {
        return this.upgrades.get(dir);
    }
    
    public void joinNetwork(Network network) {
        this.network = network;

        logger.debug(pos + " joined network " + network.getId());

        sendBlockUpdate();
    }

    public void leaveNetwork() {
        logger.debug(pos + " left network " + network.getId());

        this.network = null;

        sendBlockUpdate();
    }

    public void sendBlockUpdate() {
        BlockState state = level.getBlockState(pos);
        level.sendBlockUpdated(pos, state, state, 1 | 2);
    }

     public void update() {
        for (var manager : upgrades.values()) {
            manager.update();
        }
     }
    
    public static GraphNode fromNbt(Level level, CompoundTag tag) {
        var pos =  BlockPos.of(tag.getLong("pos"));
        var nodeTypeValue = tag.getInt("type");
        var nodeType = GraphNodeType.values()[nodeTypeValue];

        var result = new GraphNode(level, pos, nodeType);
        
        for (var dir : Direction.values()) {
            var upgradeManager = new UpgradeManager(result, dir);
            var dataTag = tag.get(dir.getSerializedName());
            
            upgradeManager.deserializeNBT((CompoundTag) dataTag);
            result.upgrades.put(dir, upgradeManager);
        }
        
        return result;
    }
    
    public CompoundTag writeToNbt(CompoundTag tag) {
        tag.putLong("pos", pos.asLong());
        tag.putInt("type", nodeType.ordinal());

        for (var pair : upgrades.entrySet()) {
            tag.put(pair.getKey().getSerializedName(), pair.getValue().serializeNBT());
        }
        
        return tag;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        var node = (GraphNode) o;
        return level.equals(node.level) &&
                pos.equals(node.pos);
    }

    @Override
    public int hashCode() {
        return Objects.hash(level, pos);
    }
}
