package com.joelcrosby.fluxpylons.item.upgrade.extract;

import com.joelcrosby.fluxpylons.item.upgrade.UpgradeItem;
import com.joelcrosby.fluxpylons.network.graph.GraphDestinationType;
import com.joelcrosby.fluxpylons.network.graph.GraphNode;
import com.joelcrosby.fluxpylons.network.graph.GraphNodeType;
import net.minecraft.core.Direction;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

public class UpgradeExtractItem extends UpgradeItem {
    
    @Override
    public void update(GraphNode node, Direction dir, GraphNodeType nodeType) {
        var level = node.getLevel();
        var source = level.getBlockEntity(node.getPos().relative(dir));

        if (source == null) return;

        var itemHandler = source
                .getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, dir.getOpposite())
                .orElse(null);

        if (itemHandler == null) return;

        Slots:
        for (var i = 0; i < itemHandler.getSlots(); i++) {
            var slot = itemHandler.getStackInSlot(i);
            if (slot.isEmpty()) {
                continue;
            }

            var rate = nodeType.getItemTransferRate();
            
            var simulatedExtract = itemHandler.extractItem(i, rate, true);
            if (simulatedExtract.isEmpty()) {
                continue;
            }

            var destinations = node.getNetwork()
                    .getRelativeDestinations(GraphDestinationType.ITEMS, source.getBlockPos());

            for (var destination : destinations) {
                var destinationEntity = destination.getConnectedBlockEntity();
                if (destinationEntity == null) continue;

                if (destination.getConnectedBlockEntity().getBlockPos() == source.getBlockPos()) {
                    throw new RuntimeException("destination cannot be the same as source");
                }

                var incomingDirection = destination.incomingDirection().getOpposite();
                var destinationHandler = destinationEntity
                        .getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, incomingDirection)
                        .orElse(null);

                if (destinationHandler == null) continue;

                if (ItemHandlerHelper.insertItem(destinationHandler, simulatedExtract, true).isEmpty()) {
                    var extracted = itemHandler.extractItem(i, rate, false);
                    ItemHandlerHelper.insertItem(destinationHandler, extracted, false);

                    break Slots;
                }
            }
        }
    }
}
