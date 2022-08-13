package com.joelcrosby.fluxpylons.machine.common;

import com.joelcrosby.fluxpylons.FluxPylons;
import com.joelcrosby.fluxpylons.energy.FluxEnergyStorage;
import com.joelcrosby.fluxpylons.recipe.common.BaseRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;

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

    @Nullable
    public abstract MachineItemStackHandler getItemStackHandler();
    
    @Nullable
    public abstract IFluidHandler getFluidHandler();

    @Nullable
    public abstract BaseRecipe getRecipe(Level level, Container container) ;
    
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
        var fluidHandler = getFluidHandler();
        
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if (itemHandler != null) {
                return LazyOptional.of(() -> itemHandler).cast();
            }
        }

        if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            if (fluidHandler != null) {
                return LazyOptional.of(() -> fluidHandler).cast();
            }
        }

        return LazyOptional.empty();
    }
    
    public void tick(Level level, BlockPos pos, BlockState state) {
        sendClientUpdate();

        BlockState blockstate;

        var inventory = getItemStackHandler();
        var fluidInventory = getFluidHandler();
        var container = new SimpleContainer(inventory.getSlots());

        for (var i = 0; i < inventory.getInputSlots(); i++) {
            var inputStack = inventory.getStackInSlot(i);
            container.addItem(inputStack);
        }

        var energy = 20;
        var recipe = getRecipe(level, container);

        if (recipe == null) {
            consumedEnergy = 0;
            
            if (state.getValue(BlockStateProperties.LIT) == Boolean.TRUE) {
                blockstate = state.setValue(BlockStateProperties.LIT, Boolean.FALSE);
                level.setBlock(pos, blockstate, 3);
            }
            
            return;
        }

        maxEnergy = recipe.energy;

        if (!inventory.canProcessInput(recipe)) {
            consumedEnergy = 0;
            return;
        }

        if (inventory.hasOutputSpaceForRecipe(recipe) && canConsumeEnergy()) {
            if (machineState == MachineState.COMPLETE) {
                
                // consume recipe inputs
                
                for (var i = 0; i < recipe.inputItems.size(); i++) {
                    var ingredient = recipe.inputItems.get(i);
                    var amount = ingredient.getAmount();

                    inventory.extractItem(i, amount, false);
                }

                for (var i = 0; i < recipe.inputFluids.size(); i++) {
                    var ingredient = recipe.inputFluids.get(i);
                    var amount = Arrays.stream(ingredient.getFluids()).findFirst().orElse(FluidStack.EMPTY).getAmount();

                    fluidInventory.drain(amount, IFluidHandler.FluidAction.EXECUTE);
                }
                
                // insert recipe output into machine inventory
                
                for (var i = 0; i < recipe.outputItems.size(); i++) {
                    var outputCount = recipe.getOutputItemsCount(i);
                    var stack = recipe.outputItems.get(i);
                    var newOutputStack = stack.copy();
                    var outputSlot = inventory.getAvailableOutputSlot(outputCount, stack);
                    
                    if (outputSlot == -1) {
                        return;
                    }

                    var output = inventory.getAvailableOutputStack(outputCount, stack);
                    
                    // Manipulating the Output slot
                    if (output.getItem() != newOutputStack.getItem() || output.getItem() == Items.AIR) {
                        if (output.getItem() == Items.AIR) { // Fix air > 1 jamming slots
                            output.setCount(1);
                        }

                        newOutputStack.setCount(recipe.getOutputItemsCount(i));
                        inventory.insertItem(outputSlot, newOutputStack.copy(), false);
                    } else {
                        output.setCount(recipe.getOutputItemsCount(i));
                        inventory.insertItem(outputSlot, newOutputStack.copy(), false);
                    }
                }

                machineState = MachineState.IDLE;
                consumedEnergy = 0;

                consumeEnergy(energy);
                setChanged();
            } else if (machineState == MachineState.PROCESSING) { // In progress
                consumeEnergy(energy);

                if (consumedEnergy >= recipe.energy) {
                    machineState = MachineState.COMPLETE;
                }
            } else { // Check if we should start processing
                if (inventory.hasOutputSpaceForRecipe(recipe)) {
                    machineState = MachineState.PROCESSING;
                    blockstate = state.setValue(BlockStateProperties.LIT, Boolean.TRUE);
                } else {
                    machineState = MachineState.IDLE;
                    blockstate = state.setValue(BlockStateProperties.LIT, Boolean.FALSE);
                }

                level.setBlock(pos, blockstate, 3);
                this.setChanged();
            }
        } else { // This is if we reach the maximum in the slots; or no power
            if (!canConsumeEnergy()) { // if no power
                machineState = MachineState.IDLE;
            } else { // zero in other cases
                machineState = MachineState.IDLE;
                consumedEnergy = 0;
            }

            blockstate = state.setValue(BlockStateProperties.LIT, Boolean.FALSE);
            level.setBlock(pos, blockstate, 3);

            this.setChanged();
        }
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, MachineBlockEntity entity) {
        entity.tick(level, pos, state);
        
        if (entity.getEnergyItemCapability() != null) {
            var itemEnergyStorage = entity.getEnergyItemCapability();
            
            if (entity.energyStorage.getEnergyStored() < entity.energyStorage.getMaxEnergyStored()) {
                entity.energyStorage.receiveEnergy(itemEnergyStorage.extractEnergy(200, false), false);
            }
        }
    }

    public void sendClientUpdate() {
        if (level == null) return;
        level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 1);
    }

    public LazyOptional<IEnergyStorage> getEnergy() {
        return lazyStorage;
    }
    
    public void consumeEnergy(int amount) {
        var toExtract = amount;
        
        var itemCap = getEnergyItemCapability();
        if (itemCap != null) {
            toExtract = toExtract - itemCap.extractEnergy(toExtract, false);
        } 
        
        var internalCap = lazyStorage.orElse(null);
        if (internalCap != null) {
            toExtract = toExtract - ((FluxEnergyStorage)internalCap).extractInternal(toExtract, false);
        }

        consumedEnergy = consumedEnergy + amount - toExtract;
    }
    
    @Nullable
    public IEnergyStorage getEnergyItemCapability() {
        var handler = getItemStackHandler();
        var energySlot = handler.getSlots() - 1;
        var energyStack = handler.getStackInSlot(energySlot);
        
        return energyStack.getCapability(CapabilityEnergy.ENERGY).orElse(null);
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

        var fluidInventory = compound.getCompound("fluidInventory");
        var fluidHandler = getFluidHandler();
        if (fluidHandler != null) {
            ((FluidTank)fluidHandler).readFromNBT(fluidInventory);
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

        var fluidHandler = getFluidHandler();
        if (fluidHandler != null) {
            var fluidTag = new CompoundTag();
            ((FluidTank)fluidHandler).writeToNBT(fluidTag);
            compound.put("fluidInventory", fluidTag);
        }
        
        compound.putInt("maxEnergy", maxEnergy);
        compound.putInt("consumedEnergy", consumedEnergy);
        compound.putInt("state", machineState.ordinal());
    }
}
