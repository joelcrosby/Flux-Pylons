package com.joelcrosby.fluxpylons.network.packets;

import com.joelcrosby.fluxpylons.item.upgrade.filter.FluidFilterContainerMenu;
import com.joelcrosby.fluxpylons.item.upgrade.filter.FluidFilterItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketUpdateFluidFilter {
    private final boolean allowDeny;

    public PacketUpdateFluidFilter(boolean allowDeny) {
        this.allowDeny = allowDeny;
    }

    public static void encode(PacketUpdateFluidFilter msg, FriendlyByteBuf buffer) {
        buffer.writeBoolean(msg.allowDeny);
    }

    public static PacketUpdateFluidFilter decode(FriendlyByteBuf buffer) {
        return new PacketUpdateFluidFilter(buffer.readBoolean());
    }

    public static class Handler {
        public static void handle(PacketUpdateFluidFilter msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                var player = ctx.get().getSender();
                if (player == null)
                    return;

                var container = player.containerMenu;
                if (container == null)
                    return;

                if (container instanceof FluidFilterContainerMenu filterContainerMenu) {
                    var filterItem = filterContainerMenu.filterItem;
                    if (filterItem == null) return;
                    FluidFilterItem.setIsDenyFilter(filterItem, msg.allowDeny);
                }
            });

            ctx.get().setPacketHandled(true);
        }
    }
}
