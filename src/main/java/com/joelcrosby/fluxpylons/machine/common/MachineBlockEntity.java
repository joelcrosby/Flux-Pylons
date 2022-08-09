package com.joelcrosby.fluxpylons.machine.common;

import com.joelcrosby.fluxpylons.FluxPylons;
import com.joelcrosby.fluxpylons.energy.FluxEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class MachineBlockEntity extends BlockEntity implements MenuProvider {

    private final FluxEnergyStorage energyStorage;
    private final LazyOptional<IEnergyStorage> lazyStorage;
    
    private final BlockEntityType<?> type;

    protected MachineState machineState = MachineState.IDLE;
    
    protected int consumedEnergy = 0;
    protected int maxEnergy = 0;
    
    public MachineBlockEntity(BlockEntityType<?> type,  BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.type = type;
        
        this.energyStorage = new FluxEnergyStorage(100000, 1000, 0);
        this.lazyStorage = LazyOptional.of(() -> energyStorage);
    }

    @Override
    public Component getDisplayName() {
        var name = BlockEntityType.getKey(type).getPath();
        return new TranslatableComponent("container." + FluxPylons.ID + "." + name);
    }


    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (cap == CapabilityEnergy.ENERGY) {
            return lazyStorage.cast();
        }

        var itemHandler = getItemStackHandler();
        
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if (itemHandler != null) {
                return LazyOptional.of(() -> itemHandler).cast();
            }
        }

        return LazyOptional.empty();
    }
    
    public abstract void tick(Level level, BlockPos pos, BlockState state);

    public static void serverTick(Level level, BlockPos pos, BlockState state, MachineBlockEntity entity) {
        entity.tick(level, pos, state);
    }

    public void sendClientUpdate() {
        if (level == null) return;
        level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 1);
    }
    
    public abstract ItemStackHandler getItemStackHandler();

    public LazyOptional<IEnergyStorage> getEnergy() {
        return lazyStorage;
    }
    
    public void consumeEnergy(int amount) {
        lazyStorage.ifPresent(e -> {
            consumedEnergy = consumedEnergy + ((FluxEnergyStorage)e).extractInternal(amount, false);
        });
    }
    
    public boolean canConsumeEnergy() {
        return lazyStorage.map(e -> e.getEnergyStored() > 0).orElse(false);
    }
    
    public int getProgress() {
        if (maxEnergy <= 0) return 0;
        if (consumedEnergy > maxEnergy) return 100;
        return (int) Math.floor(((float)consumedEnergy / (float)maxEnergy) * 100);
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
    
    @Nonnull
    @Override
    public CompoundTag getUpdateTag() {
        var compoundTag = new CompoundTag();
        this.saveAdditional(compoundTag);
        return compoundTag;
    }
    
    @Override
    public void load(CompoundTag compound) {
        this.energyStorage.setEnergyStored(compound.getInt("energy"));

        var inventory = compound.getCompound("inventory");
        var handler = getItemStackHandler();

        if (handler != null) {
            handler.deserializeNBT(inventory);
        }

        maxEnergy = compound.getInt("maxEnergy");
        consumedEnergy = compound.getInt("consumedEnergy");
        machineState = MachineState.values()[compound.getInt("state")];
        
        super.load(compound);
    }

    @Override
    protected void saveAdditional(CompoundTag compound) {
        super.saveAdditional(compound);
        compound.putInt("energy", energyStorage.getEnergyStored());

        var handler = getItemStackHandler();
        if (handler != null) {
            var inventory = ((INBTSerializable<CompoundTag>)handler).serializeNBT();
            compound.put("inventory", inventory);
        }
        
        compound.putInt("maxEnergy", maxEnergy);
        compound.putInt("consumedEnergy", consumedEnergy);
        compound.putInt("state", machineState.ordinal());
    }
}
