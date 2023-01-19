package com.joelcrosby.fluxpylons;

import com.joelcrosby.fluxpylons.pipe.network.NetworkManager;
import com.joelcrosby.fluxpylons.pylon.network.PylonNetworkManager;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public final class Events {
    
    @SubscribeEvent
    public static void onLevelTick(TickEvent.LevelTickEvent e) {
        if (!e.level.isClientSide && e.phase == TickEvent.Phase.END) {
            NetworkManager.get(e.level).getNetworks().forEach(n -> n.update(e.level));
            PylonNetworkManager.get(e.level).getNetworks().forEach(n -> n.update(e.level));
        }
    }
}
