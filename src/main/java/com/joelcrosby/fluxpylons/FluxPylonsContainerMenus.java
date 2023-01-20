package com.joelcrosby.fluxpylons;

import com.joelcrosby.fluxpylons.crate.CrateContainerMenu;
import com.joelcrosby.fluxpylons.item.upgrade.filter.TagFilterContainerMenu;
import com.joelcrosby.fluxpylons.item.upgrade.filter.common.FluidFilterContainerMenu;
import com.joelcrosby.fluxpylons.item.upgrade.filter.common.ItemFilterContainerMenu;
import com.joelcrosby.fluxpylons.machine.SmelterContainerMenu;
import com.joelcrosby.fluxpylons.machine.WasherContainerMenu;
import com.joelcrosby.fluxpylons.pipe.PipeUpgradeContainerMenu;
import com.joelcrosby.fluxpylons.pipe.PipeUpgradeItemStackHandler;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod(FluxPylons.ID)
public class FluxPylonsContainerMenus {
    public static final DeferredRegister<MenuType<?>> CONTAINER_REGISTRY = DeferredRegister.create(ForgeRegistries.MENU_TYPES, FluxPylons.ID);
    
    public static final RegistryObject<MenuType<CrateContainerMenu>> CRATE_CONTAINER_MENU = CONTAINER_REGISTRY.register("crate", () -> IForgeMenuType.create((id, inv, data) -> new CrateContainerMenu(id, inv.player, data.readBlockPos())));
    public static final RegistryObject<MenuType<PipeUpgradeContainerMenu>> PIPE_UPGRADE_CONTAINER_MENU = CONTAINER_REGISTRY.register("upgrade", () -> IForgeMenuType.create((id, inv, data) -> new PipeUpgradeContainerMenu(id, inv.player, new PipeUpgradeItemStackHandler(), data)));
    public static final RegistryObject<MenuType<ItemFilterContainerMenu>> UPGRADE_FILTER_CONTAINER_MENU = CONTAINER_REGISTRY.register("filter", () -> IForgeMenuType.create((id, inv, data) -> new ItemFilterContainerMenu(id, inv.player, data.readItem())));
    public static final RegistryObject<MenuType<FluidFilterContainerMenu>> UPGRADE_FLUID_FILTER_CONTAINER_MENU = CONTAINER_REGISTRY.register("fluid_filter", () -> IForgeMenuType.create((id, inv, data) -> new FluidFilterContainerMenu(id, inv.player, data.readItem())));
    public static final RegistryObject<MenuType<TagFilterContainerMenu>> UPGRADE_TAG_FILTER_CONTAINER_MENU = CONTAINER_REGISTRY.register("tag_filter", () -> IForgeMenuType.create((id, inv, data) -> new TagFilterContainerMenu(id, inv.player, data.readItem())));
    public static final RegistryObject<MenuType<SmelterContainerMenu>> SMELTER_CONTAINER_MENU = CONTAINER_REGISTRY.register("smelter", () -> IForgeMenuType.create((id, inv, data) -> new SmelterContainerMenu(id, inv.player, data.readBlockPos())));
    public static final RegistryObject<MenuType<WasherContainerMenu>> WASHER_CONTAINER_MENU = CONTAINER_REGISTRY.register("washer", () -> IForgeMenuType.create((id, inv, data) -> new WasherContainerMenu(id, inv.player, data.readBlockPos())));
        
    public static final int BaseFilterContainerSlots = 14;
}
