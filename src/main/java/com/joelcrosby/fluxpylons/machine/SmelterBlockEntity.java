package com.joelcrosby.fluxpylons.machine;

import com.joelcrosby.fluxpylons.FluxPylonsBlockEntities;
import com.joelcrosby.fluxpylons.machine.common.MachineBlockEntity;
import com.joelcrosby.fluxpylons.machine.common.MachineCapabilityHandler;
import com.joelcrosby.fluxpylons.machine.common.MachineFluidHandler;
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
import org.jetbrains.annotations.Nullable;

public class SmelterBlockEntity extends MachineBlockEntity {

    private final MachineCapabilityHandler capabilityHandler = new MachineCapabilityHandler() {
        private final MachineItemStackHandler inventory = new MachineItemStackHandler(6, 2, true);

        @Nullable
        @Override
        public MachineItemStackHandler items() {
            return inventory;
        }

        @Nullable
        @Override
        public MachineFluidHandler fluids() {
            return null;
        }
    };
    
    public SmelterBlockEntity(BlockPos pos, BlockState state) {
        super(FluxPylonsBlockEntities.SMELTER, pos, state);
    }

    @Nullable
    public AbstractContainerMenu createMenu(int window, Inventory inventory, Player player) {
        return new SmelterContainerMenu(window, player, worldPosition);
    }

    @Override
    public MachineCapabilityHandler getCapabilityHandler() {
        return capabilityHandler;
    }

    @Override
    public BaseRecipe getRecipe(Level level, Container container) {
        return SmelterRecipe.getRecipe(level, container);
    }
}
