package com.joelcrosby.fluxpylons.pipe.network.graph;

import com.joelcrosby.fluxpylons.pipe.network.Network;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.stream.Collectors;

public class Graph {
    private final Network network;
    private final GraphNodeType nodeType;

    private Set<GraphNode> nodes = new HashSet<>();
    private final Map<GraphDestinationType, List<GraphDestination>> destinations = new HashMap<>();
    private final Map<Pair<GraphDestinationType, BlockPos>, List<GraphDestination>> cacheRelativeDestinations = new HashMap<>();

    public Graph(Network network, GraphNodeType nodeType) {
        this.network = network;
        this.nodeType = nodeType;
    }

    public GraphScannerResult scan(Level originLevel, BlockPos originPos) {
        var scanner = new GraphScanner(nodes, this.nodeType);
        var result = scanner.scanAt(originLevel, originPos);

        this.nodes = result.foundNodes();

        result.newNodes().forEach(p -> p.joinNetwork(network));
        result.removedNodes().forEach(GraphNode::leaveNetwork);

        destinations.clear();
        cacheRelativeDestinations.clear();

        for (var destination : result.destinations()) {
            destinations.computeIfAbsent(destination.destinationType(), type -> new ArrayList<>()).add(destination);
        }

        return result;
    }

    public Set<GraphNode> getNodes() {
        return nodes;
    }

    public List<GraphDestination> getDestinations(GraphDestinationType type) {
        return destinations.getOrDefault(type, Collections.emptyList());
    }

    public List<GraphDestination> getRelativeDestinations(GraphDestinationType type, BlockPos origin) {

        var cacheKey = Pair.of(type, origin);
        
        if (cacheRelativeDestinations.containsKey(cacheKey)) {
            return cacheRelativeDestinations.get(cacheKey);
        }
        
        Comparator<GraphDestination> compareDistance = Comparator.comparing(a -> distanceTo(a.connectedNode().getPos(), origin));
        
        var result = getDestinations(type)
                    .stream()
                    .filter(dest -> dest.getConnectedBlockEntity().getBlockPos() != origin)
                    .sorted(compareDistance)
                    .collect(Collectors.toList());
        
        cacheRelativeDestinations.put(Pair.of(type, origin), result);
        
        return result;
    }

    public static double distanceTo(BlockPos aPos, BlockPos bPos) {
        double dx = aPos.getX() - bPos.getX();             
        double dy = aPos.getY() - bPos.getY();             
        double dz = aPos.getZ() - bPos.getZ();
        
        return Math.sqrt((dx * dx) + (dy * dy) + (dz * dz));     
    }
}
