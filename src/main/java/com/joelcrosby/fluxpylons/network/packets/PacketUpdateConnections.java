package com.joelcrosby.fluxpylons.network.packets;

import com.joelcrosby.fluxpylons.pylon.PylonBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public class PacketUpdateConnections {

    private final BlockPos entityPos;
    private final Set<BlockPos> connections;

    public PacketUpdateConnections(BlockPos entityPos, Set<BlockPos> connections) {
        this.entityPos = entityPos;
        this.connections = connections;
    }

    public static void encode(PacketUpdateConnections msg, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(msg.entityPos);
        buffer.writeCollection(msg.connections, FriendlyByteBuf::writeBlockPos);
    }

    public static PacketUpdateConnections decode(FriendlyByteBuf buffer) {
        var entityPos = buffer.readBlockPos();
        var collection = buffer.readCollection(i -> new HashSet<>(), FriendlyByteBuf::readBlockPos);
        return new PacketUpdateConnections(entityPos, collection);
    }

    public static class Handler {
        public static void handle(PacketUpdateConnections msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                var entity = Minecraft.getInstance().level.getBlockEntity(msg.entityPos);

                if (entity instanceof PylonBlockEntity pylon) {
                    pylon.updateConnections(msg.connections);
                }
            });

            ctx.get().setPacketHandled(true);
        }
    }
}
