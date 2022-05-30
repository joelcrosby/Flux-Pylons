package com.joelcrosby.fluxpylons.network.graph;

import com.joelcrosby.fluxpylons.FluxPylons;
import com.joelcrosby.fluxpylons.network.Network;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

public class GraphNode {
    protected final Level level;
    protected final BlockPos pos;
    protected Network network;
    protected GraphNodeType nodeType;

    public static final ResourceLocation ID = new ResourceLocation(FluxPylons.ID, "energy");
    
    private final Logger logger = LogManager.getLogger(getClass());
    
    public GraphNode(Level level, BlockPos pos, GraphNodeType nodeType) {
        this.level = level;
        this.pos = pos;
        this.nodeType = nodeType;
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

    public static GraphNode fromNbt(Level level, CompoundTag tag) {
        var pos =  BlockPos.of(tag.getLong("pos"));
        var nodeTypeValue = tag.getInt("type");
        var nodeType = GraphNodeType.values()[nodeTypeValue];

        return new GraphNode(level, pos, nodeType);
    }
    
    public CompoundTag writeToNbt(CompoundTag tag) {
        tag.putLong("pos", pos.asLong());
        tag.putInt("type", nodeType.ordinal());
        
        return tag;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GraphNode node = (GraphNode) o;
        return level.equals(node.level) &&
                pos.equals(node.pos);
    }

    @Override
    public int hashCode() {
        return Objects.hash(level, pos);
    }
}
