package com.joelcrosby.fluxpylons.pylon;

import com.joelcrosby.fluxpylons.FluxPylonsBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class PylonBlockEntity extends BlockEntity {
    public PylonBlockEntity(BlockPos pos, BlockState state) {
        super(FluxPylonsBlockEntities.PYLON, pos, state);
    }
}
