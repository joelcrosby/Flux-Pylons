package com.joelcrosby.fluxpylons.pipe.network.graph;

import java.util.List;
import java.util.Set;

public record GraphScannerResult(
        Set<GraphNode> foundNodes,
        Set<GraphNode> newNodes,
        Set<GraphNode> removedNodes,
        Set<GraphDestination> destinations,
        List<GraphScannerRequest> requests
) {
}
