package com.joelcrosby.fluxpylons.network.packets;

import com.joelcrosby.fluxpylons.item.upgrade.filter.FilterContainerMenu;
import com.joelcrosby.fluxpylons.item.upgrade.filter.FilterItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketUpdateFilter {
    private final boolean allowDeny;
    private final boolean matchNbt;

    public PacketUpdateFilter(boolean allowDeny, boolean compareNBT) {
        this.allowDeny = allowDeny;
        this.matchNbt = compareNBT;
    }

    public static void encode(PacketUpdateFilter msg, FriendlyByteBuf buffer) {
        buffer.writeBoolean(msg.allowDeny);
        buffer.writeBoolean(msg.matchNbt);
    }

    public static PacketUpdateFilter decode(FriendlyByteBuf buffer) {
        return new PacketUpdateFilter(buffer.readBoolean(), buffer.readBoolean());
    }

    public static class Handler {
        public static void handle(PacketUpdateFilter msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                var player = ctx.get().getSender();
                if (player == null)
                    return;

                var container = player.containerMenu;
                if (container == null)
                    return;

                if (container instanceof FilterContainerMenu filterContainerMenu) {
                    var filterItem = filterContainerMenu.filterItem;
                    if (filterItem == null) return;
                    FilterItem.setIsDenyFilter(filterItem, msg.allowDeny);
                    FilterItem.setMatchNbt(filterItem, msg.matchNbt);
                }
            });

            ctx.get().setPacketHandled(true);
        }
    }
}
