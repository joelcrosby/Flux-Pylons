package com.joelcrosby.fluxpylons.setup;

import com.joelcrosby.fluxpylons.FluxPylons;
import com.joelcrosby.fluxpylons.FluxPylonsItems;
import com.joelcrosby.fluxpylons.pipe.IPipeConnectable;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class Common {
    
    public static final CreativeModeTab TAB = new CreativeModeTab(FluxPylons.ID) {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(FluxPylonsItems.WRENCH.get());
        }
    };
    
    public static final Capability<IPipeConnectable> pipeConnectableCapability = CapabilityManager.get(new CapabilityToken<>() {});
}
