package com.joelcrosby.fluxpylons.pipe;

import com.joelcrosby.fluxpylons.network.NetworkManager;
import com.joelcrosby.fluxpylons.network.graph.GraphNode;
import com.joelcrosby.fluxpylons.setup.Common;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;

import javax.annotation.Nonnull;

public class PipeBlockEntity extends BlockEntity implements IPipeConnectable {
    private boolean unloaded;
    private final PipeType pipeType;

    
    private final LazyOptional<PipeBlockEntity> lazyThis = LazyOptional.of(() -> this);
    
    public PipeBlockEntity(BlockPos pos, BlockState state, PipeType pipeType) {
        super(pipeType.getEntityType(), pos, state);
        
        this.pipeType = pipeType;
    }

    public PipeType getPipeType() { 
        return this.pipeType;
    }

    public PipeUpgradeManager getUpgradeManager(Direction dir) {
        var node = NetworkManager.get(level).getNode(worldPosition);
        
        if (node == null) {
            throw new RuntimeException("PipeBlockEntity has node attached node");
        } 
        
        return node.getUpgradeManager(dir);
    }
    
    public ConnectionType getConnectionType(BlockPos pipePos, Direction direction) {
        var state = this.level.getBlockState(pipePos.relative(direction));
        
        if (!(state.getBlock() instanceof PipeBlock)) {
            return ConnectionType.DISCONNECTED;
        }
        
        if (state.getValue(PipeBlock.DIRECTIONS.get(direction.getOpposite())) == ConnectionType.BLOCKED)
            return ConnectionType.BLOCKED;
        
        return ConnectionType.CONNECTED;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (cap == Common.pipeConnectableCapability)
            return this.lazyThis.cast();
        if (cap == CapabilityEnergy.ENERGY) {
            if (!level.isClientSide) {
                var node = NetworkManager.get(level).getNode(this.worldPosition);
                
                if (node != null) {
                    var network = node.getNetwork();
                    if (network != null) {
                        return network.GetEnergyStorage().cast();
                    }
                }
            }
        }
        
        return LazyOptional.empty();
    }

    @Override
    public void onChunkUnloaded() {
        super.onChunkUnloaded();
        unloaded = true;
    }
    
    @Override
    public void setRemoved() {
        super.setRemoved();

        if (!level.isClientSide && !unloaded) {
            var manager = NetworkManager.get(level);

            var pipe = manager.getNode(worldPosition);
            
            if (pipe != null)
                manager.removeNode(worldPosition);
        }
    }
    
    @Override
    public void clearRemoved() {
        super.clearRemoved();

        if (level == null || level.isClientSide) {
            return;
        }
        
        var manager = NetworkManager.get(level);

        if (manager.getNode(worldPosition) == null) {
            manager.addNode(new GraphNode(level, worldPosition, pipeType.getNodeType()));
        }
    }
    
    @Override
    public final CompoundTag getUpdateTag() {
        return writeUpdate(super.getUpdateTag());
    }

    @Override
    public final ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this, BlockEntity::getUpdateTag);
    }
    
    @Override
    public final void handleUpdateTag(CompoundTag tag) {
        super.load(tag);
    }

    public CompoundTag writeUpdate(CompoundTag tag) {
        return tag;
    }
}
