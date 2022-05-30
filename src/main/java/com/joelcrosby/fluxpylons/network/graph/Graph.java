package com.joelcrosby.fluxpylons.network.graph;

import com.joelcrosby.fluxpylons.network.Network;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Graph {
    private final Network network;
    private final GraphNodeType nodeType;

    private Set<GraphNode> nodes = new HashSet<>();
    private final List<GraphDestination> destinations = new LinkedList<>();

    public Graph(Network network, GraphNodeType nodeType) {
        this.network = network;
        this.nodeType = nodeType;
    }

    public GraphScannerResult scan(Level originLevel, BlockPos originPos) {
        GraphScanner scanner = new GraphScanner(nodes, this.nodeType);

        GraphScannerResult result = scanner.scanAt(originLevel, originPos);

        this.nodes = result.foundNodes();

        result.newNodes().forEach(p -> p.joinNetwork(network));
        result.removedNodes().forEach(GraphNode::leaveNetwork);

        destinations.clear();
        destinations.addAll(result.destinations());

        return result;
    }

    public Set<GraphNode> getNodes() {
        return nodes;
    }

    public List<GraphDestination> getDestinations() {
        return destinations;
    }
}
