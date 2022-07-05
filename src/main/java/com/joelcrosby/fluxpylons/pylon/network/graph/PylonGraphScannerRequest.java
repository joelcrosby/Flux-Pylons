package com.joelcrosby.fluxpylons.pylon.network.graph;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public class PylonGraphScannerRequest {
    private final Level level;
    private final BlockPos pos;
    private boolean successful;

    @Nullable
    private final PylonGraphNode node;
    @Nullable
    private final Direction direction;
    @Nullable
    private final Direction pylonDirection;
    @Nullable
    private final PylonGraphScannerRequest parent;

    public PylonGraphScannerRequest(Level level,
                                    BlockPos pos,
                                    @Nullable Direction direction,
                                    @Nullable PylonGraphScannerRequest parent,
                                    @Nullable Direction pylonDirection,
                                    @Nullable PylonGraphNode node) {
        this.level = level;
        this.pos = pos;
        this.direction = direction;
        this.parent = parent;
        this.pylonDirection = pylonDirection;
        this.node = node;
    }

    public Level getLevel() {
        return level;
    }

    public BlockPos getPos() {
        return pos;
    }

    @Nullable
    public Direction getDirection() {
        return direction;
    }

    @Nullable
    public Direction getPylonDirection() {
        return pylonDirection;
    }

    @Nullable
    public PylonGraphScannerRequest getParent() {
        return parent;
    }

    @Nullable
    public PylonGraphNode getNode() {
        return node;
    }
    
    @SuppressWarnings("unused")
    public boolean isSuccessful() {
        return successful;
    }

    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }
}
