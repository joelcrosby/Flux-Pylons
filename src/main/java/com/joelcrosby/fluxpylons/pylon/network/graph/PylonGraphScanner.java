package com.joelcrosby.fluxpylons.pylon.network.graph;

import com.google.common.collect.Sets;
import com.joelcrosby.fluxpylons.Utility;
import com.joelcrosby.fluxpylons.pylon.PylonBlock;
import com.joelcrosby.fluxpylons.pylon.PylonBlockEntity;
import com.joelcrosby.fluxpylons.pylon.network.PylonNetworkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;

import java.util.*;

public class PylonGraphScanner {
    private final Set<PylonGraphNode> foundNodes = new HashSet<>();
    private final Set<PylonGraphNode> newNodes = new HashSet<>();
    private final Set<PylonGraphNode> removedNodes = new HashSet<>();
    private final Set<PylonGraphDestination> destinations = new HashSet<>();
    private final Set<PylonGraphNode> currentNodes;
    private final Hashtable<BlockPos, HashSet<BlockPos>> connections;
    private final PylonGraphNodeType nodeType;

    private final List<PylonGraphScannerRequest> allRequests = new ArrayList<>();
    private final Queue<PylonGraphScannerRequest> requests = new ArrayDeque<>();

    public PylonGraphScanner(Set<PylonGraphNode> currentNodes, PylonGraphNodeType nodeType) {
        this.currentNodes = currentNodes;
        this.nodeType = nodeType;
        this.removedNodes.addAll(currentNodes);
        this.connections = new Hashtable<>();
    }
    
    public PylonGraphScannerResult scanAt(Level level, BlockPos pos) {
        addRequest(new PylonGraphScannerRequest(level, pos, null, null, null, null));

        PylonGraphScannerRequest request;
        
        while ((request = requests.poll()) != null) {
            singleScanAt(request);
        }

        updateNodeConnections(foundNodes);
        
        return new PylonGraphScannerResult(
            foundNodes,
            newNodes,
            removedNodes,
            destinations,
            allRequests,
            connections
        );
    }

    private void addRequest(PylonGraphScannerRequest request) {
        requests.add(request);
        allRequests.add(request);
    }
    
    private void updateNodeConnections(Collection<PylonGraphNode> nodes) {
        for (var n : nodes ) {
            var entity = Utility.getExistingBlockEntity(PylonBlockEntity.class, n.level, n.getPos());
            
            if (entity instanceof PylonBlockEntity) {
                entity.updateConnections(connections.getOrDefault(n.getPos(), new HashSet<>()));
            }
        }
    }

    private void singleScanAt(PylonGraphScannerRequest request) {
        var node = PylonNetworkManager.get(request.getLevel()).getNode(request.getPos());
        
        if (node != null) {
            if (!this.nodeType.equals(node.nodeType)) {
                return;
            }

            if (!foundNodes.add(node)) {
                return;
            }
            
            if (request.getParent() != null) {
                var key = request.getParent().getPos();
                var value = node.getPos();

                if(connections.containsKey(key))
                {
                    connections.get(key).add(value);
                }
                else
                {
                    connections.put(key, Sets.newHashSet(value));
                }
            }
            
            if (!currentNodes.contains(node)) {
                newNodes.add(node);
            }

            removedNodes.remove(node);

            request.setSuccessful(true);
            
            var level = node.getLevel();
            var facing = node.getDirection();
            
            for (var dir : Direction.values()) {
                
                if (dir == facing) continue;
                
                for (var i = 0; i < PylonNetworkManager.CONNECTION_RANGE; i++) {
                    var targetPos = node.getPos().relative(dir, i + 1);
                    var targetBlockState = level.getBlockState(targetPos);
                    var targetBlock = targetBlockState.getBlock();
                    
                    if (targetBlock instanceof PylonBlock) {
                        var targetFacing = targetBlockState.getValue(BlockStateProperties.FACING);
                        
                        if (targetFacing == facing && targetFacing == dir) {
                            continue;
                        }
                        
                        addRequest(new PylonGraphScannerRequest(
                            request.getLevel(),
                            targetPos,
                            dir,
                            request,
                            facing,
                            node
                        ));

                        break;
                    }
                }
            }
        } else if (request.getParent() != null) { // This can NOT be called on node positions! (causes problems with block entities getting invalidated/validates when it shouldn't)
            // We can NOT have the TE capability checks always run regardless of whether there was a node or not.
            // Otherwise, we have this loop: node gets placed -> network gets scanned -> TEs get checked -> it might check the TE we just placed
            // -> the newly created TE can be created in immediate mode -> TE#validate is called again -> TE#remove is called again!

            var pos = request.getPos();
            var dir = request.getPylonDirection();
            var facingDirection = dir.getOpposite();
            
            var parentNode = PylonNetworkManager.get(request.getLevel()).getNode(request.getParent().getPos());
            var blockEntity = request.getLevel().getBlockEntity(pos);

            if (blockEntity == null) {
                return;
            }
            
            blockEntity.getCapability(CapabilityEnergy.ENERGY, facingDirection)
                .ifPresent(handler -> destinations.add(new PylonGraphDestination(pos, dir, parentNode, PylonGraphDestinationType.ENERGY)));
            
            blockEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facingDirection)
                .ifPresent(handler -> destinations.add(new PylonGraphDestination(pos, dir, parentNode, PylonGraphDestinationType.ITEMS)));
            
            blockEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facingDirection)
                .ifPresent(handler -> destinations.add(new PylonGraphDestination(pos, dir, parentNode, PylonGraphDestinationType.FLUIDS)));
        }
    }
}
