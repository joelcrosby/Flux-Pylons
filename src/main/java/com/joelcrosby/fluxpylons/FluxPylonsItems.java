package com.joelcrosby.fluxpylons;

import com.joelcrosby.fluxpylons.item.WrenchItem;
import com.joelcrosby.fluxpylons.item.upgrade.extract.ExtractItem;
import com.joelcrosby.fluxpylons.item.upgrade.extract.FluidExtractItem;
import com.joelcrosby.fluxpylons.item.upgrade.extract.FluidRetrieverItem;
import com.joelcrosby.fluxpylons.item.upgrade.extract.RetrieverItem;
import com.joelcrosby.fluxpylons.item.upgrade.filter.FluidFilterItem;
import com.joelcrosby.fluxpylons.item.upgrade.filter.TagFilterItem;
import com.joelcrosby.fluxpylons.item.upgrade.filter.common.BaseFilterItem;
import net.minecraftforge.registries.ObjectHolder;

public class FluxPylonsItems
{
    @ObjectHolder(FluxPylons.ID + ":wrench")
    public static final WrenchItem WRENCH = null;
    
    @ObjectHolder(FluxPylons.ID + ":upgrade_filter")
    public static final BaseFilterItem UPGRADE_FILTER = null;
    
    @ObjectHolder(FluxPylons.ID + ":upgrade_fluid_filter")
    public static final FluidFilterItem UPGRADE_FLUID_FILTER = null;
    
    @ObjectHolder(FluxPylons.ID + ":upgrade_tag_filter")
    public static final TagFilterItem UPGRADE_TAG_FILTER = null;
    
    @ObjectHolder(FluxPylons.ID + ":upgrade_extract")
    public static final ExtractItem UPGRADE_EXTRACT = null;
    
    @ObjectHolder(FluxPylons.ID + ":upgrade_fluid_extract")
    public static final FluidExtractItem UPGRADE_FLUID_EXTRACT = null;
    
    @ObjectHolder(FluxPylons.ID + ":upgrade_retriever")
    public static final RetrieverItem UPGRADE_RETRIEVER = null;

    @ObjectHolder(FluxPylons.ID + ":upgrade_fluid_retriever")
    public static final FluidRetrieverItem UPGRADE_FLUID_RETRIEVER = null;
}
