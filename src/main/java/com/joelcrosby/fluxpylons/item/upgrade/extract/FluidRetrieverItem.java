package com.joelcrosby.fluxpylons.item.upgrade.extract;

import com.joelcrosby.fluxpylons.FluxPylons;
import com.joelcrosby.fluxpylons.FluxPylonsContainerMenus;
import com.joelcrosby.fluxpylons.Utility;
import com.joelcrosby.fluxpylons.item.upgrade.filter.common.BaseFilterItem;
import com.joelcrosby.fluxpylons.item.upgrade.filter.common.FluidFilterContainerMenu;
import com.joelcrosby.fluxpylons.item.upgrade.filter.common.ItemFilterStackHandler;
import com.joelcrosby.fluxpylons.pipe.network.graph.GraphDestinationType;
import com.joelcrosby.fluxpylons.pipe.network.graph.GraphNode;
import com.joelcrosby.fluxpylons.pipe.network.graph.GraphNodeType;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.network.NetworkHooks;


public class FluidRetrieverItem extends BaseFilterItem {

    @Override
    public ItemStackHandler getItemStackHandler(ItemStack stack) {
        return new ItemFilterStackHandler(FluxPylonsContainerMenus.BaseFilterContainerSlots, stack);
    }

    @Override
    protected boolean defaultsToDenyList() {
        return true;
    }
    
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        var stack = player.getItemInHand(interactionHand);

        if (level.isClientSide()) return new InteractionResultHolder<>(InteractionResult.PASS, stack);

        openGui(player, stack);

        return new InteractionResultHolder<>(InteractionResult.PASS, stack);
    }

    public void openGui(Player player, ItemStack stack) {
        var containerName = new TranslatableComponent("container." + FluxPylons.ID + "." + this.getRegistryName().getPath());

        NetworkHooks.openGui((ServerPlayer) player,
                new SimpleMenuProvider((windowId, playerInventory, playerEntity) ->
                        new FluidFilterContainerMenu(windowId, player, stack), containerName),
                (buffer -> buffer.writeItem(stack))
        );
    }

    @Override
    public void update(ItemStack itemStack, GraphNode node, Direction dir, GraphNodeType nodeType) {
        var level = node.getLevel();
        var source = level.getBlockEntity(node.getPos().relative(dir));

        if (source == null) return;

        var fluidHandler = source
                .getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, dir.getOpposite())
                .orElse(null);

        if (fluidHandler == null) return;

        var isDenyList = BaseFilterItem.getIsDenyList(itemStack);
        var inventory = BaseFilterItem.getInventory(itemStack);

        var rate = nodeType.getFluidTransferRate();

        var destinations = node.getNetwork()
                .getRelativeDestinations(GraphDestinationType.FLUIDS, source.getBlockPos());

        Outer:
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
            
            for (var i = 0; i < destinationHandler.getTanks(); i++) {
                var fluidStack = destinationHandler.getFluidInTank(i);
                if (fluidStack.isEmpty()) {
                    continue;
                }

                var matchesFilter = Utility.matchesFilterInventory(inventory, fluidStack);
                if (isDenyList == matchesFilter) {
                    continue;
                }

                var simulatedExtract = destinationHandler.drain(rate, IFluidHandler.FluidAction.SIMULATE);
                if (simulatedExtract.isEmpty()) {
                    continue;
                }

                var amountToExtract = fluidHandler.fill(simulatedExtract, IFluidHandler.FluidAction.SIMULATE);

                if (amountToExtract == 0) continue;

                var extracted = destinationHandler.drain(amountToExtract, IFluidHandler.FluidAction.EXECUTE);
                fluidHandler.fill(extracted, IFluidHandler.FluidAction.EXECUTE);

                break Outer;
            }
        }
    }
}
