package com.joelcrosby.fluxpylons.pylon.network;

import com.joelcrosby.fluxpylons.energy.FluxEnergyStorage;
import com.joelcrosby.fluxpylons.pylon.network.graph.*;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;

import java.util.List;

public class PylonNetwork {
    private final PylonGraph graph;

    private BlockPos originPos;
    private boolean didDoInitialScan;
    
    private final FluxEnergyStorage storage;
    private final LazyOptional<IEnergyStorage> lazyStorage;
    
    private final Level level;
    
    private final String id;
    private final PylonGraphNodeType nodeType;

    public PylonNetwork(String id, BlockPos originPos, Level level, PylonGraphNodeType nodeType) {
        this.id = id;
        this.level = level;
        this.nodeType = nodeType;
        this.graph = new PylonGraph(this, this.nodeType);

        this.storage = new FluxEnergyStorage(nodeType.getCapacity(), nodeType.getEnergyTransferRate(), nodeType.getEnergyTransferRate());
        this.lazyStorage = LazyOptional.of(() -> this.storage);
        
        this.setOriginPos(originPos);
    }

    public String getId() {
        return id;
    }
    
    public Level getLevel() {
        return level;
    }
    
    public void setOriginPos(BlockPos originPos) {
        this.originPos = originPos;
    }

    public CompoundTag serializeNBT(CompoundTag tag) {
        tag.putString("id", id);
        tag.putLong("origin", originPos.asLong());
        tag.putInt("energy", storage.getEnergyStored());
        tag.putInt("type", nodeType.ordinal());
        
        return tag;
    }

    public static PylonNetwork fromNBT(Level level, CompoundTag nbt) {
        var networkBlockPos = BlockPos.of(nbt.getLong("origin"));
        var networkId = nbt.getString("id");
        var nodeTypeValue = nbt.getInt("type");
        var nodeType = PylonGraphNodeType.values()[nodeTypeValue];
        var network = new PylonNetwork(networkId, networkBlockPos, level, nodeType);

        network.storage.setEnergyStored(nbt.getInt("energy"));
        
        return network;
    }
    
    public LazyOptional<IEnergyStorage> GetEnergyStorage() {
        return lazyStorage;
    }

    public PylonGraphNode getNode(BlockPos pos) {
        return graph.getNodes().stream().filter(p -> p.getPos().equals(pos)).findFirst().orElse(null);
    }

    public List<PylonGraphDestination> getDestinations(PylonGraphDestinationType type) {
        return graph.getDestinations(type);
    }

    public List<PylonGraphDestination> getRelativeDestinations(PylonGraphDestinationType type, BlockPos pos) {
        return graph.getRelativeDestinations(type, pos);
    }
    
    public PylonGraphScannerResult scanGraph(Level level, BlockPos pos) {
        var result =  graph.scan(level, pos);

        var firstNode = result.foundNodes().stream().findFirst();
        
        if (firstNode.isEmpty()) {
            return result;
        }

        this.storage.setCapacity(result.foundNodes().size() * this.nodeType.getCapacity());
        
        return result;
    }

    public void onMergedWith(PylonNetwork mainNetwork) {
        var mainEnergy = mainNetwork.storage.getEnergyStored();
        var mergedEnergy = storage.getEnergyStored();
        
        mainNetwork.storage.setEnergyStored(mainEnergy + mergedEnergy);
    }

    public void update(Level level) {
        if (!didDoInitialScan) {
            didDoInitialScan = true;

            scanGraph(level, originPos);
        }
        
        updateEnergy();
    }

    private void updateEnergy() {
        if (this.storage.getEnergyStored() <= 0) {
            return;
        }

        var destinations = graph.getDestinations(PylonGraphDestinationType.ENERGY);

        for (var destination : destinations) {
            var blockEntity = destination.getConnectedBlockEntity();
            if (blockEntity == null) {
                continue;
            }

            var side = destination.incomingDirection().getOpposite();
            var energyHandler = blockEntity.getCapability(ForgeCapabilities.ENERGY, side).orElse(null);

            if (energyHandler == null) {
                continue;
            }
            
            if (!energyHandler.canReceive()) {
                continue;
            }

            var toOffer = Math.min(this.nodeType.getEnergyTransferRate(), this.storage.getEnergyStored());
            if (toOffer <= 0) {
                break;
            }

            toOffer = this.storage.extractEnergy(toOffer, false);
            if (toOffer <= 0) {
                break;
            }

            var accepted = energyHandler.receiveEnergy(toOffer, false);

            var remainder = toOffer - accepted;
            if (remainder > 0) {
                this.storage.receiveEnergy(remainder, false);
            }
        }
    }
}
