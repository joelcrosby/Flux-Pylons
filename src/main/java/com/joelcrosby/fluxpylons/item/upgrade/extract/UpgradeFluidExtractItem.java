package com.joelcrosby.fluxpylons.item.upgrade.extract;

import com.joelcrosby.fluxpylons.item.upgrade.UpgradeItem;
import com.joelcrosby.fluxpylons.pipe.network.graph.GraphDestinationType;
import com.joelcrosby.fluxpylons.pipe.network.graph.GraphNode;
import com.joelcrosby.fluxpylons.pipe.network.graph.GraphNodeType;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class UpgradeFluidExtractItem extends UpgradeItem {
    @Override
    public void update(ItemStack itemStack, GraphNode node, Direction dir, GraphNodeType nodeType) {
        var level = node.getLevel();
        var source = level.getBlockEntity(node.getPos().relative(dir));

        if (source == null) return;

        var fluidHandler = source
                .getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, dir.getOpposite())
                .orElse(null);

        if (fluidHandler == null) return;

        var rate = nodeType.getFluidTransferRate();
        
        Tanks:
        for (var i = 0; i < fluidHandler.getTanks(); i++) {
            var availableFluid = fluidHandler.getFluidInTank(i);
            if (availableFluid.isEmpty()) {
                continue;
            }

            var simulatedExtract = fluidHandler.drain(rate, IFluidHandler.FluidAction.SIMULATE);
            if (simulatedExtract.isEmpty()) {
                continue;
            }

            var destinations = node.getNetwork()
                    .getRelativeDestinations(GraphDestinationType.FLUIDS, source.getBlockPos());
            
            for (var destination : destinations) {
                var destinationEntity = destination.getConnectedBlockEntity();
                if (destinationEntity == null) continue;

                if (destination.getConnectedBlockEntity().getBlockPos() == source.getBlockPos()) {
                    throw new RuntimeException("destination cannot be the same as source");
                }

                var incomingDirection = destination.incomingDirection().getOpposite();
                var destinationHandler = destinationEntity
                        .getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, incomingDirection)
                        .orElse(null);

                if (destinationHandler == null) continue;

                if (destinationHandler.fill(simulatedExtract, IFluidHandler.FluidAction.SIMULATE) != 0) {
                    var extracted = fluidHandler.drain(rate, IFluidHandler.FluidAction.EXECUTE);
                    destinationHandler.fill(extracted, IFluidHandler.FluidAction.EXECUTE);

                    break Tanks;
                }
            }
        }
    }
}
