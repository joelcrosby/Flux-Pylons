package com.joelcrosby.fluxpylons;

import com.joelcrosby.fluxpylons.setup.Client;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(FluxPylons.ID)
public class FluxPylons
{
    public static final String ID = "fluxpylons";

    public FluxPylons()
    {
        var bus = FMLJavaModLoadingContext.get().getModEventBus();
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> bus.addListener(Client::setup));
    }
}
