package com.joelcrosby.fluxpylons.pylon.network.graph;

import com.joelcrosby.fluxpylons.pylon.network.PylonNetwork;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.stream.Collectors;

public class PylonGraph {
    private final PylonNetwork network;
    private final PylonGraphNodeType nodeType;

    private Set<PylonGraphNode> nodes = new HashSet<>();
    private final Map<PylonGraphDestinationType, List<PylonGraphDestination>> destinations = new HashMap<>();
    private final Map<Pair<PylonGraphDestinationType, BlockPos>, List<PylonGraphDestination>> cacheRelativeDestinations = new HashMap<>();

    public PylonGraph(PylonNetwork network, PylonGraphNodeType nodeType) {
        this.network = network;
        this.nodeType = nodeType;
    }

    public PylonGraphScannerResult scan(Level originLevel, BlockPos originPos) {
        var scanner = new PylonGraphScanner(nodes, this.nodeType);
        var result = scanner.scanAt(originLevel, originPos);

        this.nodes = result.foundNodes();

        result.newNodes().forEach(p -> p.joinNetwork(network));
        result.removedNodes().forEach(PylonGraphNode::leaveNetwork);

        destinations.clear();
        cacheRelativeDestinations.clear();

        for (var destination : result.destinations()) {
            destinations.computeIfAbsent(destination.destinationType(), type -> new ArrayList<>()).add(destination);
        }

        return result;
    }

    public Set<PylonGraphNode> getNodes() {
        return nodes;
    }

    public List<PylonGraphDestination> getDestinations(PylonGraphDestinationType type) {
        return destinations.getOrDefault(type, Collections.emptyList());
    }

    public List<PylonGraphDestination> getRelativeDestinations(PylonGraphDestinationType type, BlockPos origin) {

        var cacheKey = Pair.of(type, origin);
        
        if (cacheRelativeDestinations.containsKey(cacheKey)) {
            return cacheRelativeDestinations.get(cacheKey);
        }
        
        Comparator<PylonGraphDestination> compareDistance = Comparator.comparing(a -> distanceTo(a.connectedNode().getPos(), origin));
        
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
