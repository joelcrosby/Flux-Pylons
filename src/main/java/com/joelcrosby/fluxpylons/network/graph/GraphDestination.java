package com.joelcrosby.fluxpylons.network.graph;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Objects;

public record GraphDestination(BlockPos receiver, Direction incomingDirection, GraphNode connectedNode, GraphDestinationType destinationType) {

    public BlockEntity getConnectedBlockEntity() {
        return connectedNode.getLevel().getBlockEntity(receiver);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        var that = (GraphDestination) o;
        return Objects.equals(receiver, that.receiver) &&
                incomingDirection == that.incomingDirection &&
                Objects.equals(connectedNode.getPos(), that.connectedNode.getPos())
                && destinationType == that.destinationType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(receiver, incomingDirection, connectedNode.getPos(), destinationType);
    }
}
