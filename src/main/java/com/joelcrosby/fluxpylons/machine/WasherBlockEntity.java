package com.joelcrosby.fluxpylons.machine;

import com.joelcrosby.fluxpylons.FluxPylonsBlockEntities;
import com.joelcrosby.fluxpylons.machine.common.MachineBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

public class WasherBlockEntity extends MachineBlockEntity {
    private final ItemStackHandler inventory = new ItemStackHandler(4);
    
    private final IFluidHandler fluidInventory = new FluidTank(FluidAttributes.BUCKET_VOLUME * 10) {
        @Override
        public boolean isFluidValid(FluidStack stack) {
            var name = stack.getFluid().getRegistryName().getPath();
            if (!name.equals("water")) return false;
            return super.isFluidValid(stack);
        }
    };
    
    public WasherBlockEntity(BlockPos pos, BlockState state) {
        super(FluxPylonsBlockEntities.WASHER, pos, state);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int window, Inventory inventory, Player player) {
        return new WasherContainerMenu(window, player, worldPosition);
    }


    @Override
    public void tick(Level level, BlockPos pos, BlockState state) {
        sendClientUpdate();
    }

    @Override
    public ItemStackHandler getItemStackHandler() {
        return inventory;
    }

    @Override
    public IFluidHandler getFluidHandler() {
        return fluidInventory;
    }
    
    public FluidStack getFluidStack() {
        return fluidInventory.getFluidInTank(0);
    }

    public int getFluidTankCapacity() {
        return fluidInventory.getTankCapacity(0);
    }
}
