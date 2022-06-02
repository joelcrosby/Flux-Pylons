package com.joelcrosby.fluxpylons.item;

import com.joelcrosby.fluxpylons.network.graph.GraphNode;
import net.minecraft.core.Direction;


public abstract class Upgrade {
    public abstract void update(GraphNode node, Direction dir);
}
