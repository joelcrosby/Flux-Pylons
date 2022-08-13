package com.joelcrosby.fluxpylons.machine;

import com.joelcrosby.fluxpylons.FluxPylonsBlockEntities;
import com.joelcrosby.fluxpylons.machine.common.MachineBlockEntity;
import com.joelcrosby.fluxpylons.machine.common.MachineItemStackHandler;
import com.joelcrosby.fluxpylons.recipe.WasherRecipe;
import com.joelcrosby.fluxpylons.recipe.common.BaseRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.Nullable;

public class WasherBlockEntity extends MachineBlockEntity {
    private final MachineItemStackHandler inventory = new MachineItemStackHandler(1, 2, true);
    
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
    public MachineItemStackHandler getItemStackHandler() {
        return inventory;
    }

    @Override
    public IFluidHandler getFluidHandler() {
        return fluidInventory;
    }

    @Override
    public BaseRecipe getRecipe(Level level, Container container) {
        return WasherRecipe.getRecipe(level, container);
    }

    public FluidStack getFluidStack() {
        return fluidInventory.getFluidInTank(0);
    }

    public int getFluidTankCapacity() {
        return fluidInventory.getTankCapacity(0);
    }
}
