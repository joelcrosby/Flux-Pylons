package com.joelcrosby.fluxpylons.pipe.network.graph;

import com.joelcrosby.fluxpylons.pipe.PipeType;

public enum GraphNodeType {
    BASIC,
    ADVANCED;

    public int getCapacity() {
        return getEnergyTransferRate() * 8;
    }

    public int getEnergyTransferRate() {
        return switch (this) {
            case BASIC -> 512;
            case ADVANCED -> 1024 * 4;
        };
    }

    public int getFluidTransferRate() {
        return switch (this) {
            case BASIC -> 10;
            case ADVANCED -> 20;
        };
    }
    
    public int getItemTransferRate() {
        return switch (this) {
            case BASIC -> 8;
            case ADVANCED -> 16;
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
