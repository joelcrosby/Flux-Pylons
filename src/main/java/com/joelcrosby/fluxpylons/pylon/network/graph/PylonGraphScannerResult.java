package com.joelcrosby.fluxpylons.pylon.network.graph;

import net.minecraft.core.BlockPos;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

public record PylonGraphScannerResult(
        Set<PylonGraphNode> foundNodes,
        Set<PylonGraphNode> newNodes,
        Set<PylonGraphNode> removedNodes,
        Set<PylonGraphDestination> destinations,
        List<PylonGraphScannerRequest> requests,
        Hashtable<BlockPos, HashSet<BlockPos>> connections
) {
}
