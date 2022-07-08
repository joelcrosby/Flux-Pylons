package com.joelcrosby.fluxpylons.pylon.network;

import com.joelcrosby.fluxpylons.FluxPylons;
import com.joelcrosby.fluxpylons.pylon.network.graph.PylonGraphNode;
import com.joelcrosby.fluxpylons.pylon.network.graph.PylonGraphNodeType;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PylonNetworkManager extends SavedData {
    private static final String NAME = FluxPylons.ID + "_pylon_networks";
    private static final Logger LOGGER = LogManager.getLogger(PylonNetworkManager.class);

    public static final int CONNECTION_RANGE = 16;
    
    private final Level level;
    private final HashMap<String, PylonNetwork> networks = new HashMap<>();
    private final HashMap<BlockPos, PylonGraphNode> nodes = new HashMap<>();

    public PylonNetworkManager(Level level) {
        this.level = level;
    }

    public static PylonNetworkManager get(Level level) {
        return get((ServerLevel) level);
    }

    public static PylonNetworkManager get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent((tag) -> {
            var networkManager = new PylonNetworkManager(level);
            networkManager.load(tag);
            return networkManager;
        }, () -> new PylonNetworkManager(level), NAME);
    }

    public void addNetwork(PylonNetwork network) {
        if (networks.containsKey(network.getId())) {
            throw new RuntimeException("Duplicate network " + network.getId());
        }

        networks.put(network.getId(), network);

        LOGGER.debug("Network {} created", network.getId());

        setDirty();
    }

    public void removeNetwork(String id) {
        if (!networks.containsKey(id)) {
            throw new RuntimeException("Network " + id + " not found");
        }

        networks.remove(id);

        LOGGER.debug("Network {} removed", id);

        setDirty();
    }

    private void formNetworkAt(Level level, BlockPos pos, PylonGraphNodeType nodeType) {
        var networkId = UUID.randomUUID().toString().substring(0, 8);
        var network = new PylonNetwork(networkId, pos, level, nodeType);

        addNetwork(network);
        network.scanGraph(level, pos);

        LOGGER.debug("Formed network at {}", pos);
    }

    public void mergeNetworksIntoOne(Set<PylonGraphNode> candidates, Level level, BlockPos pos) {
        if (candidates.isEmpty()) {
            throw new RuntimeException("Cannot merge networks: no candidates");
        }

        var networkCandidates = new HashSet<PylonNetwork>();

        for (var candidate : candidates) {
            if (candidate.getNetwork() != null) {
                networkCandidates.add(candidate.getNetwork());
            }
        }

        var networks = networkCandidates.iterator();
        var mainNetwork = networks.next();
        var mergedNetworks = new HashSet<PylonNetwork>();

        while (networks.hasNext()) {
            // Remove all the other networks.
            var otherNetwork = networks.next();

            mergedNetworks.add(otherNetwork);

            removeNetwork(otherNetwork.getId());
        }

        mainNetwork.scanGraph(level, pos);

        mergedNetworks.forEach(n -> {
            n.onMergedWith(mainNetwork);

            LOGGER.debug("Network {} merged into main network {}", n.getId(), mainNetwork.getId());
        });
    }

    public void addNode(PylonGraphNode node) {
        if (nodes.containsKey(node.getPos())) {
            throw new RuntimeException("Node at " + node.getPos() + " already exists");
        }

        nodes.put(node.getPos(), node);

        LOGGER.debug("Node added at {}", node.getPos());

        setDirty();

        var adjacentNodes = findAdjacentNodes(node);

        if (adjacentNodes.isEmpty()) {
            formNetworkAt(node.getLevel(), node.getPos(), node.getNodeType());
        } else {
            mergeNetworksIntoOne(adjacentNodes, node.getLevel(), node.getPos());
        }
    }

    public void removeNode(BlockPos pos) {
        var node = getNode(pos);
        
        if (node == null) {
            throw new RuntimeException("Node at " + pos + " was not found");
        }

        if (node.getNetwork() == null) {
            LOGGER.warn("Removed node at {} has no associated network", node.getPos());
        }

        nodes.remove(node.getPos());

        LOGGER.debug("Node removed at {}", node.getPos());

        setDirty();

        if (node.getNetwork() != null) {
            splitNetworks(node, true);
        }
    }

    public void splitNetworks(PylonGraphNode originNode, Boolean ensureNodeRemoved) {
        // Sanity checks
        var adjacentNodes = findAdjacentNodes(originNode);
        
        for (var adjacent : adjacentNodes) {
            if (adjacent.getNetwork() == null) {
                throw new RuntimeException("Adjacent node has no network");
            }

            if (adjacent.getNetwork() != originNode.getNetwork()) {
                LOGGER.debug("The origin node network is different than the adjacent node network");
                return;
            }
        }

        
        // We can assume all adjacent nodes (with the same network type) share the same network with the removed node.
        // That means it doesn't matter which node network we use for splitting, we'll take the first found one.
        var otherNodeInNetwork = adjacentNodes.stream().findFirst().orElse(null);

        if (otherNodeInNetwork != null) {
            otherNodeInNetwork.getNetwork().setOriginPos(otherNodeInNetwork.getPos());
            setDirty();

            var result = otherNodeInNetwork.getNetwork().scanGraph(
                    otherNodeInNetwork.getLevel(),
                    otherNodeInNetwork.getPos()
            );

            // For sanity checking
            var foundRemovedNode = false;

            for (var removed : result.removedNodes()) {
                // It's obvious that our removed node is removed.
                // We don't want to create a new split network for this one.
                if (removed.getPos().equals(originNode.getPos())) {
                    foundRemovedNode = true;
                    continue;
                }

                // The formNetworkAt call below can let these removed nodes join a network again.
                // We only have to form a new network when necessary, hence the null check.
                if (removed.getNetwork() == null) {
                    formNetworkAt(removed.getLevel(), removed.getPos(), removed.getNodeType());
                }
            }

            if (!ensureNodeRemoved) {
                formNetworkAt(originNode.getLevel(), originNode.getPos(), originNode.getNodeType());
            } else if (!foundRemovedNode) {
                throw new RuntimeException("Didn't find removed node when splitting network");
            }

            
        } else {
            LOGGER.debug("Removing empty network {}", originNode.getNetwork().getId());

            removeNetwork(originNode.getNetwork().getId());
        }
    }

    private Set<PylonGraphNode> findAdjacentNodes(PylonGraphNode node) {
        var startPos = node.getPos();
        
        var sx = startPos.getX();
        var sy = startPos.getY();
        var sz = startPos.getZ();
        
        return this.nodes.values()
                .stream()
                .filter(n -> {
                    
                    var targetPos = n.getPos();
                    var x = targetPos.getX();
                    var y = targetPos.getY();
                    var z = targetPos.getZ();
                    
                    return Stream.of(x == sx, y == sy, z == sz).filter(m -> m).count() == 2;
                })
                .collect(Collectors.toSet());
    }

    @Nullable
    public PylonGraphNode getNode(BlockPos pos) {
        return nodes.get(pos);
    }

    public Collection<PylonNetwork> getNetworks() {
        return networks.values();
    }
    
    public void load(CompoundTag tag) {
        var nodeTags = tag.getList("nodes", Tag.TAG_COMPOUND);
        
        for (var nodeTag : nodeTags) {
            var nodeTagCompound = (CompoundTag) nodeTag;
            var node = PylonGraphNode.fromNbt(level, nodeTagCompound);

            this.nodes.put(node.getPos(), node);
        }

        var networkTags = tag.getList("networks", Tag.TAG_COMPOUND);
        
        for (var networkTag : networkTags) {
            var netTagCompound = (CompoundTag) networkTag;
            var network = PylonNetwork.fromNBT(level, netTagCompound);

            this.networks.put(network.getId(), network);

            LOGGER.debug("Loaded existing network {}", network.getId());
        }

        LOGGER.debug("Read {} nodes", nodeTags.size());
        LOGGER.debug("Read {} networks", networks.size());
    }

    @Override
    @NotNull
    public CompoundTag save(CompoundTag tag) {
        var nodeTags = new ListTag();
        var networkTags = new ListTag();

        for (var node : this.nodes.values()) {
            var nodeTag = new CompoundTag();
            nodeTag.putString("id", node.getId().toString());
            nodeTags.add(node.writeToNbt(nodeTag));
        }

        for (var network : this.networks.values()) {
            var networkTag = new CompoundTag();
            networkTags.add(network.serializeNBT(networkTag));
        }

        tag.put("nodes", nodeTags);
        tag.put("networks", networkTags);

        return tag;
    }
}
