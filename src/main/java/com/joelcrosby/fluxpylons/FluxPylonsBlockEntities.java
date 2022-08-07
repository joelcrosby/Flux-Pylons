package com.joelcrosby.fluxpylons;

import com.joelcrosby.fluxpylons.crate.CrateBlockEntity;
import com.joelcrosby.fluxpylons.machine.ChamberBlockEntity;
import com.joelcrosby.fluxpylons.machine.SmelterBlockEntity;
import com.joelcrosby.fluxpylons.pipe.PipeBlockEntity;
import com.joelcrosby.fluxpylons.pylon.PylonBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.ObjectHolder;

public class FluxPylonsBlockEntities
{
    @ObjectHolder(FluxPylons.ID + ":pipe")
    public static final BlockEntityType<PipeBlockEntity> BASIC_PIPE = null;
    @ObjectHolder(FluxPylons.ID + ":adv_pipe")
    public static final BlockEntityType<PipeBlockEntity> ADV_PIPE = null;
    @ObjectHolder(FluxPylons.ID + ":crate")
    public static final BlockEntityType<CrateBlockEntity> CRATE = null;
    @ObjectHolder(FluxPylons.ID + ":pylon")
    public static final BlockEntityType<PylonBlockEntity> PYLON = null;

    @ObjectHolder(FluxPylons.ID + ":chamber")
    public static final BlockEntityType<ChamberBlockEntity> CHAMBER = null;
    @ObjectHolder(FluxPylons.ID + ":smelter")
    public static final BlockEntityType<SmelterBlockEntity> SMELTER = null;
}
