package com.joelcrosby.fluxpylons.network;

import net.minecraftforge.energy.EnergyStorage;

public class NetworkEnergyStorage extends EnergyStorage {

    public NetworkEnergyStorage(int capacity, int maxReceive, int maxExtract) {
        super(capacity, maxReceive, maxExtract);
    }

    public void setEnergyStored(int energy) {
        this.energy = energy;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public void setMaxReceive(int maxReceive) {
        this.maxReceive = maxReceive;
    }

    public void setMaxExtract(int maxExtract) {
        this.maxExtract = maxExtract;
    }
    
    @SuppressWarnings("unused")
    public int extractInternal(int maxExtract, boolean simulate) {
        var energyExtracted = Math.min(this.energy, maxExtract);
        if (!simulate)
            this.energy -= energyExtracted;
        return energyExtracted;
    }
}
