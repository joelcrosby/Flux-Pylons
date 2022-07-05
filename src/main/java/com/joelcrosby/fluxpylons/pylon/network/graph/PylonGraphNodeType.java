package com.joelcrosby.fluxpylons.pylon.network.graph;

public enum PylonGraphNodeType {
    BASIC,
    ADVANCED;

    public int getCapacity() {
        return getEnergyTransferRate() * 8;
    }

    public int getEnergyTransferRate() {
        return switch (this) {
            case BASIC -> 8192;
            case ADVANCED -> 8192 * 4;
        };
    }
}
