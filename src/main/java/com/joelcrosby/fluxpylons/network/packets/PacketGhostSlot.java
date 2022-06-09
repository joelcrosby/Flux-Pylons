package com.joelcrosby.fluxpylons.network.packets;

import com.joelcrosby.fluxpylons.item.upgrade.filter.common.FilterSlotHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketGhostSlot {
    private final int slotNumber;
    private final ItemStack stack;
    private final int count;

    public PacketGhostSlot(int slotNumber, ItemStack stack, int count) {
        this.slotNumber = slotNumber;
        this.stack = stack;
        this.count = count;
    }

    public static void encode(PacketGhostSlot msg, FriendlyByteBuf buffer) {
        buffer.writeInt(msg.slotNumber);
        buffer.writeItem(msg.stack);
        buffer.writeInt(msg.count);
    }

    public static PacketGhostSlot decode(FriendlyByteBuf buffer) {
        return new PacketGhostSlot(buffer.readInt(), buffer.readItem(), buffer.readInt());
    }

    public static class Handler {
        public static void handle(PacketGhostSlot msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                ServerPlayer sender = ctx.get().getSender();
                if (sender == null) return;

                var container = sender.containerMenu;
                
                if (container == null) return;

                var slot = container.slots.get(msg.slotNumber);
                var stack = msg.stack;

                stack.setCount(msg.count);

                if (slot instanceof FilterSlotHandler) {
                    slot.set(stack);
                }
            });

            ctx.get().setPacketHandled(true);
        }
    }
}
