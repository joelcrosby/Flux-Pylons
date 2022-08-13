package com.joelcrosby.fluxpylons.machine;

import com.joelcrosby.fluxpylons.FluxPylonsBlockEntities;
import com.joelcrosby.fluxpylons.machine.common.MachineBlockEntity;
import com.joelcrosby.fluxpylons.machine.common.MachineItemStackHandler;
import com.joelcrosby.fluxpylons.recipe.common.BaseRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

public class ChamberBlockEntity extends MachineBlockEntity {
    public ChamberBlockEntity(BlockPos pos, BlockState state) {
        super(FluxPylonsBlockEntities.CHAMBER, pos, state);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int window, Inventory inventory, Player player) {
        return null;
    }


    @Override
    public void tick(Level level, BlockPos pos, BlockState state) {
        
    }

    @Override
    @Nullable
    public MachineItemStackHandler getItemStackHandler() {
        return null;
    }

    @Override
    @Nullable
    public IFluidHandler getFluidHandler() {
        return null;
    }

    @Override
    @Nullable
    public BaseRecipe getRecipe(Level level, Container container) {
        return null;
    }
}
