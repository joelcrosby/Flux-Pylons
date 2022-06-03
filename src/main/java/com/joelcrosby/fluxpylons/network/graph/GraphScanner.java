package com.joelcrosby.fluxpylons.network.graph;

import com.joelcrosby.fluxpylons.network.NetworkEnergyStorage;
import com.joelcrosby.fluxpylons.network.NetworkManager;
import com.joelcrosby.fluxpylons.pipe.PipeBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;

import java.util.*;

public class GraphScanner {
    private final Set<GraphNode> foundNodes = new HashSet<>();
    private final Set<GraphNode> newNodes = new HashSet<>();
    private final Set<GraphNode> removedNodes = new HashSet<>();
    private final Set<GraphDestination> destinations = new HashSet<>();
    private final Set<GraphNode> currentNodes;
    private final GraphNodeType nodeType;

    private final List<GraphScannerRequest> allRequests = new ArrayList<>();
    private final Queue<GraphScannerRequest> requests = new ArrayDeque<>();

    public GraphScanner(Set<GraphNode> currentNodes, GraphNodeType nodeType) {
        this.currentNodes = currentNodes;
        this.nodeType = nodeType;
        this.removedNodes.addAll(currentNodes);
    }

    private void addRequest(GraphScannerRequest request) {
        requests.add(request);
        allRequests.add(request);
    }
    
    public GraphScannerResult scanAt(Level level, BlockPos pos) {
        addRequest(new GraphScannerRequest(level, pos, null, null));

        GraphScannerRequest request;
        
        while ((request = requests.poll()) != null) {
            singleScanAt(request);
        }

        return new GraphScannerResult(
                foundNodes,
                newNodes,
                removedNodes,
            destinations,
            allRequests
        );
    }

    private void singleScanAt(GraphScannerRequest request) {
        var node = NetworkManager.get(request.getLevel()).getNode(request.getPos());

        if (node != null) {
            if (!this.nodeType.equals(node.nodeType)) {
                return;
            }
            
            if (!foundNodes.add(node)) {
                return;
            }
            
            if (!currentNodes.contains(node)) {
                newNodes.add(node);
            }

            removedNodes.remove(node);

            request.setSuccessful(true);

            var blockState = node.getLevel().getBlockState(node.getPos());
            
            for (var dir : Direction.values()) {
                var prop = PipeBlock.DIRECTIONS.get(dir);
                var connectionType = blockState.getValue(prop);
                
                if (!connectionType.isConnected())
                    continue;
                
                addRequest(new GraphScannerRequest(
                    request.getLevel(),
                    request.getPos().relative(dir),
                    dir,
                    request
                ));
            }
            
        } else if (request.getParent() != null) { // This can NOT be called on node positions! (causes problems with block entities getting invalidated/validates when it shouldn't)
            // We can NOT have the TE capability checks always run regardless of whether there was a node or not.
            // Otherwise, we have this loop: node gets placed -> network gets scanned -> TEs get checked -> it might check the TE we just placed
            // -> the newly created TE can be created in immediate mode -> TE#validate is called again -> TE#remove is called again!

            var pos = request.getPos();
            var dir = request.getDirection();
            var facingDirection = dir.getOpposite();
            
            var parentNode = NetworkManager.get(request.getLevel()).getNode(request.getParent().getPos());
            var blockEntity = request.getLevel().getBlockEntity(pos);

            if (blockEntity == null) {
                return;
            }
            
            blockEntity.getCapability(CapabilityEnergy.ENERGY, facingDirection)
                .ifPresent(handler -> {
                    if (!(handler instanceof NetworkEnergyStorage)) {
                        destinations.add(new GraphDestination(pos, dir, parentNode, GraphDestinationType.ENERGY));
                    }
                });
            
            blockEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facingDirection)
                .ifPresent(handler -> destinations.add(new GraphDestination(pos, dir, parentNode, GraphDestinationType.ITEMS)));
            
            blockEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facingDirection)
                .ifPresent(handler -> destinations.add(new GraphDestination(pos, dir, parentNode, GraphDestinationType.FLUIDS)));
        }
    }
}
