package com.joelcrosby.fluxpylons.pylon;

import com.joelcrosby.fluxpylons.FluxPylonsBlockEntities;
import com.joelcrosby.fluxpylons.pylon.network.PylonNetworkManager;
import com.joelcrosby.fluxpylons.pylon.network.graph.PylonGraphNode;
import com.joelcrosby.fluxpylons.pylon.network.graph.PylonGraphNodeType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import static com.joelcrosby.fluxpylons.pylon.network.PylonNetworkManager.CONNECTION_RANGE;

public class PylonBlockEntity extends BlockEntity {
    private final PylonGraphNodeType nodeType;
    private boolean unloaded;
    
    protected final Set<BlockPos> connections = new CopyOnWriteArraySet<>();
    
    public PylonBlockEntity(BlockPos pos, BlockState state, PylonGraphNodeType nodeType) {
        super(FluxPylonsBlockEntities.PYLON, pos, state);
        
        this.nodeType = nodeType;
    }

    public Set<BlockPos> getConnections() {
        return this.connections;
    }

    public void updateConnections(Collection<BlockPos> connections) {
        this.connections.clear();
        this.connections.addAll(connections);
        
        this.markDirtyClient();
    }

    public void markDirtyClient() {
        this.setChanged();
        
        if (this.getLevel() != null) {
            var state = this.getLevel().getBlockState(this.getBlockPos());
            this.getLevel().sendBlockUpdated(this.getBlockPos(), state, state, 3);
        }
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
            var manager = PylonNetworkManager.get(level);

            var node = manager.getNode(worldPosition);

            if (node != null)
                manager.removeNode(worldPosition);
        }
    }

    @Override
    public void clearRemoved() {
        super.clearRemoved();
        
        if (level == null) {
            return;
        }
        
        if (level.isClientSide) {
            return;
        }
        
        var manager = PylonNetworkManager.get(level);

        if (manager.getNode(worldPosition) == null) {
            var dir = getBlockState()
                    .getOptionalValue(BlockStateProperties.FACING)
                    .orElse(Direction.DOWN);
            
            manager.addNode(new PylonGraphNode(level, worldPosition, dir, nodeType));
        }
    }

    @Override
    public final CompoundTag getUpdateTag() {
        var tag = new CompoundTag();
        saveAdditional(tag);
        return tag;
    }

    @Override
    public final void handleUpdateTag(CompoundTag tag) {
        this.load(tag);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
    
    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        this.load(pkt.getTag());
    }
    
    @Override
    public void saveAdditional(CompoundTag compound) {
        super.saveAdditional(compound);
        
        var connectionsTag = new ListTag();
        for (var pos : this.connections) {
            connectionsTag.add(NbtUtils.writeBlockPos(pos));
        }

        compound.put("connections", connectionsTag);
    }

    @Override
    public void load(CompoundTag compound) {
        var connectionsCompound = compound.getList("connections", Tag.TAG_COMPOUND);
        
        this.connections.clear();
        
        for (var i = 0; i < connectionsCompound.size(); i++) {
            var pos = NbtUtils.readBlockPos(connectionsCompound.getCompound(i));
            this.connections.add(pos);
        }

        super.load(compound);
    }
    
    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (side != getBlockState().getValue(BlockStateProperties.FACING)) {
            return LazyOptional.empty();
        }
        
        if (cap == CapabilityEnergy.ENERGY) {
            if (!level.isClientSide) {
                var node = PylonNetworkManager.get(level).getNode(this.worldPosition);

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

    @Nonnull
    @Override
    public AABB getRenderBoundingBox() {
        return new AABB(
                getBlockPos().above(CONNECTION_RANGE).north(CONNECTION_RANGE).east(CONNECTION_RANGE),
                getBlockPos().below(CONNECTION_RANGE).south(CONNECTION_RANGE).west(CONNECTION_RANGE)
        );
    }
}
