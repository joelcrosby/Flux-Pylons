package com.joelcrosby.fluxpylons;

import com.joelcrosby.fluxpylons.setup.Client;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(FluxPylons.ID)
public class FluxPylons
{
    public static final String ID = "fluxpylons";

    public FluxPylons()
    {
        MinecraftForge.EVENT_BUS.register(this);

        final var bus = FMLJavaModLoadingContext.get().getModEventBus();

        FluxPylonsRecipes.RECIPE_SERIALIZERS.register(bus);
        FluxPylonsRecipes.FluxPylonsRecipeTypes.RECIPE_TYPES_REGISTRY.register(bus);
        
        FluxPylonsBlocks.BLOCKS_REGISTRY.register(bus);
        FluxPylonsBlockEntities.BLOCK_ENTITIES_REGISTRY.register(bus);
        FluxPylonsItems.ITEM_REGISTRY.register(bus);
        FluxPylonsContainerMenus.CONTAINER_REGISTRY.register(bus);
        
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> bus.addListener(Client::setup));
    }
}
