package com.joelcrosby.fluxpylons.machine;

import com.joelcrosby.fluxpylons.FluxPylonsBlockEntities;
import com.joelcrosby.fluxpylons.machine.common.MachineBlockEntity;
import com.joelcrosby.fluxpylons.machine.common.MachineItemStackHandler;
import com.joelcrosby.fluxpylons.recipe.SmelterRecipe;
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

public class SmelterBlockEntity extends MachineBlockEntity {

    private final MachineItemStackHandler inventory = new MachineItemStackHandler(6, 2, true);
    
    public SmelterBlockEntity(BlockPos pos, BlockState state) {
        super(FluxPylonsBlockEntities.SMELTER, pos, state);
    }

    @Nullable
    public AbstractContainerMenu createMenu(int window, Inventory inventory, Player player) {
        return new SmelterContainerMenu(window, player, worldPosition);
    }

    @Override
    public MachineItemStackHandler getItemStackHandler() {
        return inventory;
    }

    @Override
    public IFluidHandler getFluidHandler() {
        return null;
    }

    @Override
    public BaseRecipe getRecipe(Level level, Container container) {
        return SmelterRecipe.getRecipe(level, container);
    }
}
