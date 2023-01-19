package com.joelcrosby.fluxpylons.pipe;

import com.joelcrosby.fluxpylons.pipe.network.NetworkManager;
import com.joelcrosby.fluxpylons.pipe.network.graph.GraphNode;
import com.joelcrosby.fluxpylons.setup.Common;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;

public class PipeBlockEntity extends BlockEntity implements IPipeConnectable {
    private boolean unloaded;
    private final PipeType pipeType;
    public BlockState cover;
    
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
        if (cap == ForgeCapabilities.ENERGY) {
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
        return this.saveWithoutMetadata();
    }
    
    @Override
    public final void handleUpdateTag(CompoundTag tag) {
        this.load(tag);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        this.load(pkt.getTag());
    }
    
    @Override
    public void saveAdditional(CompoundTag compound) {
        super.saveAdditional(compound);
        if (this.cover != null)
            compound.put("cover", NbtUtils.writeBlockState(this.cover));
    }

    @Override
    public void load(CompoundTag compound) {
        if (compound == null) {
            this.cover = null;
            return;
        } else {
            this.cover = compound.contains("cover") ? NbtUtils.readBlockState(compound.getCompound("cover")) : null;
        }
        
        super.load(compound);
    }
    
    @OnlyIn(Dist.CLIENT)
    public AABB getRenderBoundingBox() {
        // our render bounding box should always be the full block in case we're covered
        return new AABB(this.worldPosition);
    }


    public void removeCover(Player player, InteractionHand hand) {
        if (this.level.isClientSide)
            return;
        var drops = Block.getDrops(this.cover, (ServerLevel) this.level, this.worldPosition, null, player, player.getItemInHand(hand));
        for (var drop : drops)
            Containers.dropItemStack(this.level, this.worldPosition.getX(), this.worldPosition.getY(), this.worldPosition.getZ(), drop);
        this.cover = null;
    }
}
