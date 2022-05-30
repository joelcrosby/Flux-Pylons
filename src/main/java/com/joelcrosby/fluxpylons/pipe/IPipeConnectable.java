package com.joelcrosby.fluxpylons.pipe;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public interface IPipeConnectable {
    ConnectionType getConnectionType(BlockPos pipePos, Direction direction);
}
