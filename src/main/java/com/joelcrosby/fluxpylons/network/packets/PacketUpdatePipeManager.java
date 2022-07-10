package com.joelcrosby.fluxpylons.network.packets;

import com.joelcrosby.fluxpylons.pipe.PipeIoMode;
import com.joelcrosby.fluxpylons.pipe.PipeUpgradeContainerMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketUpdatePipeManager {
    private final PipeIoMode ioMode;

    public PacketUpdatePipeManager(PipeIoMode ioMode) {
        this.ioMode = ioMode;
    }

    public static void encode(PacketUpdatePipeManager msg, FriendlyByteBuf buffer) {
        buffer.writeEnum(msg.ioMode);
    }

    public static PacketUpdatePipeManager decode(FriendlyByteBuf buffer) {
        return new PacketUpdatePipeManager(buffer.readEnum(PipeIoMode.class));
    }

    public static class Handler {
        public static void handle(PacketUpdatePipeManager msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                var player = ctx.get().getSender();
                if (player == null)
                    return;

                var container = player.containerMenu;
                if (container == null)
                    return;

                if (container instanceof PipeUpgradeContainerMenu containerMenu) {
                    containerMenu.upgradeManager.setPipeIoMode(msg.ioMode);
                }
            });

            ctx.get().setPacketHandled(true);
        }
    }
}
