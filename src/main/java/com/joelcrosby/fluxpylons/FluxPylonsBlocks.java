package com.joelcrosby.fluxpylons;

import com.joelcrosby.fluxpylons.crate.CrateBlock;
import com.joelcrosby.fluxpylons.pipe.PipeBlock;
import com.joelcrosby.fluxpylons.pipe.PipeBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.ObjectHolder;

public class FluxPylonsBlocks
{
    @ObjectHolder(FluxPylons.ID + ":pipe")
    public static final PipeBlock BASIC_PIPE = null;
    @ObjectHolder(FluxPylons.ID + ":adv_pipe")
    public static final PipeBlock ADV_PIPE = null;
    @ObjectHolder(FluxPylons.ID + ":crate")
    public static final CrateBlock CRATE = null;
}
