package com.joelcrosby.fluxpylons;

import com.joelcrosby.fluxpylons.item.WrenchItem;
import com.joelcrosby.fluxpylons.item.upgrade.extract.UpgradeExtractItem;
import com.joelcrosby.fluxpylons.item.upgrade.extract.UpgradeFluidExtractItem;
import com.joelcrosby.fluxpylons.item.upgrade.filter.UpgradeFilterItem;
import net.minecraftforge.registries.ObjectHolder;

public class FluxPylonsItems
{
    @ObjectHolder(FluxPylons.ID + ":wrench")
    public static final WrenchItem WRENCH = null;
    @ObjectHolder(FluxPylons.ID + ":upgrade_filter")
    public static final UpgradeFilterItem UPGRADE_FILTER = null;
    @ObjectHolder(FluxPylons.ID + ":upgrade_extract")
    public static final UpgradeExtractItem UPGRADE_EXTRACT = null;
    @ObjectHolder(FluxPylons.ID + ":upgrade_fluid_extract")
    public static final UpgradeFluidExtractItem UPGRADE_FLUID_EXTRACT = null;
    
}
