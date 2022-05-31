package com.joelcrosby.fluxpylons.pipe;

import net.minecraft.util.StringRepresentable;

import java.util.Locale;

public enum ConnectionType implements StringRepresentable {
    END(true, true),
    CONNECTED(true, false),
    DISCONNECTED(false, false),
    BLOCKED(false, false);

    private final String name;
    private final boolean isConnected;
    private final boolean isEnd;

    ConnectionType(boolean isConnected, boolean isEnd) {
        this.name = this.name().toLowerCase(Locale.ROOT);
        
        this.isConnected = isConnected;
        this.isEnd = isEnd;
    }

    public boolean isConnected() {
        return this.isConnected;
    }

    public boolean isEnd() {
        return this.isEnd;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }
}
