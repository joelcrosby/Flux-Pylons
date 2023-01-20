package com.joelcrosby.fluxpylons;

import com.joelcrosby.fluxpylons.item.IngotItem;
import com.joelcrosby.fluxpylons.item.WrenchItem;
import com.joelcrosby.fluxpylons.item.upgrade.extract.ExtractItem;
import com.joelcrosby.fluxpylons.item.upgrade.extract.FluidExtractItem;
import com.joelcrosby.fluxpylons.item.upgrade.extract.FluidRetrieverItem;
import com.joelcrosby.fluxpylons.item.upgrade.extract.RetrieverItem;
import com.joelcrosby.fluxpylons.item.upgrade.filter.BasicFilterItem;
import com.joelcrosby.fluxpylons.item.upgrade.filter.FluidFilterItem;
import com.joelcrosby.fluxpylons.item.upgrade.filter.TagFilterItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class FluxPylonsItems
{
    public static final DeferredRegister<Item> ITEM_REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, FluxPylons.ID);

    public static final RegistryObject<WrenchItem> WRENCH = ITEM_REGISTRY.register("wrench", WrenchItem::new);
    public static final RegistryObject<BasicFilterItem> UPGRADE_FILTER = ITEM_REGISTRY.register("upgrade_filter", BasicFilterItem::new);
    public static final RegistryObject<FluidFilterItem> UPGRADE_FLUID_FILTER = ITEM_REGISTRY.register("upgrade_fluid_filter", FluidFilterItem::new);
    public static final RegistryObject<TagFilterItem> UPGRADE_TAG_FILTER = ITEM_REGISTRY.register("upgrade_tag_filter", TagFilterItem::new);
    public static final RegistryObject<ExtractItem> UPGRADE_EXTRACT = ITEM_REGISTRY.register("upgrade_extract", ExtractItem::new);
    public static final RegistryObject<FluidExtractItem> UPGRADE_FLUID_EXTRACT = ITEM_REGISTRY.register("upgrade_fluid_extract", FluidExtractItem::new);
    public static final RegistryObject<RetrieverItem> UPGRADE_RETRIEVER = ITEM_REGISTRY.register("upgrade_retriever", RetrieverItem::new);
    public static final RegistryObject<FluidRetrieverItem> UPGRADE_FLUID_RETRIEVER = ITEM_REGISTRY.register("upgrade_fluid_retriever", FluidRetrieverItem::new);
    public static final RegistryObject<IngotItem> INGOT_CONDUCTIVE_IRON = ITEM_REGISTRY.register("ingot_conductive_alloy", IngotItem::new);
}
