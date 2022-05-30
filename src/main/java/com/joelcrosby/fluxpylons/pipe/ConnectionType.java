package com.joelcrosby.fluxpylons.pipe;

import net.minecraft.util.StringRepresentable;

import java.util.Locale;

public enum ConnectionType implements StringRepresentable {
    END(true),
    CONNECTED(true),
    DISCONNECTED(false),
    BLOCKED(false);

    private final String name;
    private final boolean isConnected;

    ConnectionType(boolean isConnected) {
        this.name = this.name().toLowerCase(Locale.ROOT);
        this.isConnected = isConnected;
    }

    public boolean isConnected() {
        return this.isConnected;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }
}
