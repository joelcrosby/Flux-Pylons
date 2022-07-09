package com.joelcrosby.fluxpylons.network.packets;

import com.joelcrosby.fluxpylons.item.upgrade.filter.common.BaseFilterItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketOpenScreen {
    private final int slotNumber;

    public PacketOpenScreen(int slotNumber) {
        this.slotNumber = slotNumber;
    }

    public static void encode(PacketOpenScreen msg, FriendlyByteBuf buffer) {
        buffer.writeInt(msg.slotNumber);
    }

    public static PacketOpenScreen decode(FriendlyByteBuf buffer) {
        return new PacketOpenScreen(buffer.readInt());
    }

    public static class Handler {
        public static void handle(PacketOpenScreen msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                var sender = ctx.get().getSender();
                if (sender == null) return;

                var container = sender.containerMenu;
                if (container == null) return;

                var slot = container.slots.get(msg.slotNumber);
                var stack = slot.getItem();
                var stackItem = stack.getItem();

                if (stackItem instanceof BaseFilterItem filterItem) {
                    filterItem.openGui(sender, stack);
                }
            });

            ctx.get().setPacketHandled(true);
        }
    }
}
