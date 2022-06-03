package com.joelcrosby.fluxpylons.item.upgrade.extract;

import com.joelcrosby.fluxpylons.item.upgrade.UpgradeItem;
import com.joelcrosby.fluxpylons.network.graph.GraphDestinationType;
import com.joelcrosby.fluxpylons.network.graph.GraphNode;
import net.minecraft.core.Direction;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class UpgradeFluidExtractItem extends UpgradeItem {
    private static final int stackSize = 200;
    
    @Override
    public void update(GraphNode node, Direction dir) {
        var level = node.getLevel();
        var source = level.getBlockEntity(node.getPos().relative(dir));

        if (source == null) return;

        var fluidHandler = source
                .getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, dir)
                .orElse(null);

        if (fluidHandler == null) return;

        Tanks:
        for (var i = 0; i < fluidHandler.getTanks(); i++) {
            var availableFluid = fluidHandler.getFluidInTank(i);
            if (availableFluid.isEmpty()) {
                continue;
            }

            var simulatedExtract = fluidHandler.drain(stackSize, IFluidHandler.FluidAction.SIMULATE);
            if (simulatedExtract.isEmpty()) {
                continue;
            }

            var destinations = node.getNetwork().getRelativeDestinations(GraphDestinationType.FLUIDS, source.getBlockPos());
            
            for (var destination : destinations) {
                var destinationEntity = destination.getConnectedBlockEntity();
                if (destinationEntity == null) return;

                if (destination.getConnectedBlockEntity().getBlockPos() == source.getBlockPos()) {
                    throw new RuntimeException("destination cannot be the same as source");
                }

                var incomingDirection = destination.incomingDirection();
                var destinationHandler = destinationEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, incomingDirection).orElse(null);


                if (destinationHandler.fill(simulatedExtract, IFluidHandler.FluidAction.SIMULATE) != 0) {
                    var extracted = fluidHandler.drain(stackSize, IFluidHandler.FluidAction.EXECUTE);
                    destinationHandler.fill(extracted, IFluidHandler.FluidAction.EXECUTE);

                    break Tanks;
                }
            }
        }
    }
}
