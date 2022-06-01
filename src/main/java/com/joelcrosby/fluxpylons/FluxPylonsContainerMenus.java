package com.joelcrosby.fluxpylons;

import com.joelcrosby.fluxpylons.crate.CrateContainerMenu;
import com.joelcrosby.fluxpylons.item.UpgradeContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.registries.ObjectHolder;

public class FluxPylonsContainerMenus {
    @ObjectHolder(FluxPylons.ID + ":crate")
    public static final MenuType<CrateContainerMenu> CRATE_CONTAINER_MENU = null;
    @ObjectHolder(FluxPylons.ID + ":upgrade")
    public static final MenuType<UpgradeContainerMenu> UPGRADE_CONTAINER_MENU = null;
}
