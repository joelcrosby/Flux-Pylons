package com.joelcrosby.fluxpylons.network.packets;

import com.google.common.collect.Lists;
import com.joelcrosby.fluxpylons.item.upgrade.filter.TagFilterContainerMenu;
import com.joelcrosby.fluxpylons.item.upgrade.filter.TagFilterItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
import java.util.function.Supplier;

public class PacketUpdateTagFilter {
    private final boolean isDenyList;
    private final List<String> tags;

    public PacketUpdateTagFilter(boolean isDenyList, List<String> tags) {
        this.isDenyList = isDenyList;
        this.tags = tags;
    }

    public static void encode(PacketUpdateTagFilter msg, FriendlyByteBuf buffer) {
        buffer.writeBoolean(msg.isDenyList);
        buffer.writeCollection(msg.tags, FriendlyByteBuf::writeUtf);
    }

    public static PacketUpdateTagFilter decode(FriendlyByteBuf buffer) {
        return new PacketUpdateTagFilter(buffer.readBoolean(), buffer.readCollection(Lists::newArrayListWithCapacity, FriendlyByteBuf::readUtf));
    }

    public static class Handler {
        public static void handle(PacketUpdateTagFilter msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                var player = ctx.get().getSender();
                if (player == null)
                    return;

                var container = player.containerMenu;
                if (container == null)
                    return;

                if (container instanceof TagFilterContainerMenu filterContainerMenu) {
                    var filterItem = filterContainerMenu.filterItem;
                    if (filterItem == null) return;
                    TagFilterItem.setIsDenyList(filterItem, msg.isDenyList);
                    TagFilterItem.setTags(filterItem, msg.tags);
                }
            });

            ctx.get().setPacketHandled(true);
        }
    }
}
