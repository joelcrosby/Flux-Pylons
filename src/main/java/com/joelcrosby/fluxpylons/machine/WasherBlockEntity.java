package com.joelcrosby.fluxpylons.machine;

import com.joelcrosby.fluxpylons.FluxPylonsBlockEntities;
import com.joelcrosby.fluxpylons.machine.common.MachineBlockEntity;
import com.joelcrosby.fluxpylons.machine.common.MachineCapabilityHandler;
import com.joelcrosby.fluxpylons.machine.common.MachineFluidHandler;
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
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

public class WasherBlockEntity extends MachineBlockEntity {
    
    private final MachineCapabilityHandler capabilityHandler = new MachineCapabilityHandler() {
        private final MachineItemStackHandler inventory = new MachineItemStackHandler(1, 2, true);
        private final MachineFluidHandler fluidInventory = new MachineFluidHandler(1, 0) {
            @Override
            public boolean isFluidValid(int tank, FluidStack stack) {
                var name = ForgeRegistries.FLUIDS.getKey(stack.getFluid()).getPath();
                return name.equals("water");
            }
        };
        
        @Nullable
        @Override
        public MachineItemStackHandler items() {
            return inventory;
        }

        @Nullable
        @Override
        public MachineFluidHandler fluids() {
            return fluidInventory;
        }
    };
    
    public WasherBlockEntity(BlockPos pos, BlockState state) {
        super(FluxPylonsBlockEntities.WASHER.get(), pos, state);
    }

    @Override
    public MachineCapabilityHandler getCapabilityHandler() {
        return capabilityHandler;
    }
    
    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int window, Inventory inventory, Player player) {
        return new WasherContainerMenu(window, player, worldPosition);
    }
    
    @Override
    public BaseRecipe getRecipe(Level level, Container container) {
        return WasherRecipe.getRecipe(level, container);
    }

    public FluidStack getFluidStack() {
        return capabilityHandler.fluids().getFluidInTank(0);
    }

    public int getFluidTankCapacity() {
        return capabilityHandler.fluids().getTankCapacity(0);
    }
}
