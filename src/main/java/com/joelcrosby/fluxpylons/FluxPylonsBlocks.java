package com.joelcrosby.fluxpylons;

import com.joelcrosby.fluxpylons.crate.CrateBlock;
import com.joelcrosby.fluxpylons.pipe.PipeBlock;
import com.joelcrosby.fluxpylons.pylon.PylonBlock;
import net.minecraftforge.registries.ObjectHolder;

public class FluxPylonsBlocks
{
    @ObjectHolder(FluxPylons.ID + ":pipe")
    public static final PipeBlock BASIC_PIPE = null;
    @ObjectHolder(FluxPylons.ID + ":adv_pipe")
    public static final PipeBlock ADV_PIPE = null;
    @ObjectHolder(FluxPylons.ID + ":crate")
    public static final CrateBlock CRATE = null;
    @ObjectHolder(FluxPylons.ID + ":pylon")
    public static final PylonBlock PYLON = null;
}
