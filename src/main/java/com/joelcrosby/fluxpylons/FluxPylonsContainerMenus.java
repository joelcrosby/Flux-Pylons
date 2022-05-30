package com.joelcrosby.fluxpylons;

import com.joelcrosby.fluxpylons.crate.CrateContainerMenu;
import com.joelcrosby.fluxpylons.pipe.PipeContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.registries.ObjectHolder;

public class FluxPylonsContainerMenus {
    @ObjectHolder(FluxPylons.ID + ":crate")
    public static final MenuType<CrateContainerMenu> CRATE_CONTAINER_MENU = null;
    @ObjectHolder(FluxPylons.ID + ":pipe")
    public static final MenuType<PipeContainerMenu> PIPE_CONTAINER_MENU = null;
}
