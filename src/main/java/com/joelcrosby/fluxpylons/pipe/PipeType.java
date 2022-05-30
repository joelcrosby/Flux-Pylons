package com.joelcrosby.fluxpylons.pipe;

import com.joelcrosby.fluxpylons.FluxPylonsBlockEntities;
import com.joelcrosby.fluxpylons.network.graph.GraphNodeType;
import net.minecraft.world.level.block.entity.BlockEntityType;

public enum PipeType {
    BASIC,
    ADVANCED;
    
    public BlockEntityType<PipeBlockEntity> getEntityType() {
        return switch (this) {
            case BASIC -> FluxPylonsBlockEntities.BASIC_PIPE;
            case ADVANCED -> FluxPylonsBlockEntities.ADV_PIPE;
        };
    }

    public GraphNodeType getNodeType() {
        return switch (this) {
            case BASIC -> GraphNodeType.BASIC;
            case ADVANCED -> GraphNodeType.ADVANCED;
        };
    }
}
