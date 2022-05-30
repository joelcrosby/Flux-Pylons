package com.joelcrosby.fluxpylons.network.graph;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public class GraphScannerRequest {
    private final Level level;
    private final BlockPos pos;
    private boolean successful;
    
    @Nullable
    private final Direction direction;
    
    @Nullable
    private final GraphScannerRequest parent;
    
    public GraphScannerRequest(Level level, BlockPos pos, @Nullable Direction direction, @Nullable GraphScannerRequest parent) {
        this.level = level;
        this.pos = pos;
        this.direction = direction;
        this.parent = parent;
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
    public GraphScannerRequest getParent() {
        return parent;
    }

    @SuppressWarnings("unused")
    public boolean isSuccessful() {
        return successful;
    }

    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }
}
