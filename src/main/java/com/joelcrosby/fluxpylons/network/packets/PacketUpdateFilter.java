package com.joelcrosby.fluxpylons.network.packets;

import com.joelcrosby.fluxpylons.item.upgrade.filter.common.BaseFilterContainerMenu;
import com.joelcrosby.fluxpylons.item.upgrade.filter.common.BaseFilterItem;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketUpdateFilter {
    private final boolean isDenyList;
    private final boolean matchNbt;
    private final Direction interactionSide;

    public PacketUpdateFilter(boolean isDenyList, boolean compareNBT, Direction interactionSide) {
        this.isDenyList = isDenyList;
        this.matchNbt = compareNBT;
        this.interactionSide = interactionSide;
    }

    public static void encode(PacketUpdateFilter msg, FriendlyByteBuf buffer) {
        buffer.writeBoolean(msg.isDenyList);
        buffer.writeBoolean(msg.matchNbt);
        
        if (msg.interactionSide != null) {
            buffer.writeEnum(msg.interactionSide);
        }
    }

    public static PacketUpdateFilter decode(FriendlyByteBuf buffer) {
        return new PacketUpdateFilter(buffer.readBoolean(), buffer.readBoolean(), buffer.isReadable() ? buffer.readEnum(Direction.class) : null);
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

                if (container instanceof BaseFilterContainerMenu filterContainerMenu) {
                    var filterItem = filterContainerMenu.filterItem;
                    if (filterItem == null) return;
                    BaseFilterItem.setIsDenyList(filterItem, msg.isDenyList);
                    BaseFilterItem.setMatchNbt(filterItem, msg.matchNbt);
                    BaseFilterItem.setInteractionSide(filterItem, msg.interactionSide);
                }
            });

            ctx.get().setPacketHandled(true);
        }
    }
}
