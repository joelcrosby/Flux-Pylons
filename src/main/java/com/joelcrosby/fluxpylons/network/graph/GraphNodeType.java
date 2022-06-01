package com.joelcrosby.fluxpylons.network.graph;

import com.joelcrosby.fluxpylons.pipe.PipeType;

public enum GraphNodeType {
    BASIC,
    ADVANCED;

    public int getCapacity() {
        return getTransferRate() * 8;
    }

    public int getTransferRate() {
        return switch (this) {
            case BASIC -> 512;
            case ADVANCED -> 1024 * 4;
        };
    }

    public GraphNodeType getEntityType(PipeType pipeType) {
        return switch (pipeType) {
            case BASIC -> GraphNodeType.BASIC;
            case ADVANCED -> GraphNodeType.ADVANCED;
        };
    }

    public PipeType getEntityType() {
        return switch (this) {
            case BASIC -> PipeType.BASIC;
            case ADVANCED -> PipeType.ADVANCED;
        };
    }
}
