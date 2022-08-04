package com.joelcrosby.fluxpylons.pylon.network.graph;

import com.joelcrosby.fluxpylons.FluxPylons;
import com.joelcrosby.fluxpylons.pylon.network.PylonNetwork;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

public class PylonGraphNode {
    protected final Level level;
    protected final BlockPos pos;
    protected final Direction direction;
    protected final PylonGraphNodeType nodeType;
    
    protected PylonNetwork network;

    public static final ResourceLocation ID = new ResourceLocation(FluxPylons.ID, "pylon-node");
    
    private final Logger logger = LogManager.getLogger(getClass());

    public PylonGraphNode(Level level, BlockPos pos, Direction direction, PylonGraphNodeType nodeType) {
        this.level = level;
        this.pos = pos;
        this.direction = direction;

        this.nodeType = nodeType;
    }

    public Level getLevel() {
        return level;
    }
    
    public BlockPos getPos() {
        return pos;
    }

    public Direction getDirection() {
        return direction;
    }
    
    public PylonNetwork getNetwork() {
        return network;
    }
    
    public ResourceLocation getId() {
        return ID;
    }

    public PylonGraphNodeType getNodeType() {
        return this.nodeType;
    }
    
    public void joinNetwork(PylonNetwork network) {
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
    
    public static PylonGraphNode fromNbt(Level level, CompoundTag tag) {
        var pos =  BlockPos.of(tag.getLong("pos"));
        var nodeTypeValue = tag.getInt("type");
        var nodeType = PylonGraphNodeType.values()[nodeTypeValue];
        var dirValue = tag.getInt("direction");
        var dir = Direction.values()[dirValue];
        
        return new PylonGraphNode(level, pos, dir, nodeType);
    }
    
    public CompoundTag writeToNbt(CompoundTag tag) {
        tag.putLong("pos", pos.asLong());
        tag.putInt("type", nodeType.ordinal());
        tag.putInt("direction", direction.ordinal());
        
        return tag;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        var node = (PylonGraphNode) o;
        return level.equals(node.level) &&
                pos.equals(node.pos);
    }

    @Override
    public int hashCode() {
        return Objects.hash(level, pos);
    }
}
