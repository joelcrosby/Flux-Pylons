package com.joelcrosby.fluxpylons.item.upgrade.extract;

import com.joelcrosby.fluxpylons.FluxPylons;
import com.joelcrosby.fluxpylons.FluxPylonsContainerMenus;
import com.joelcrosby.fluxpylons.Utility;
import com.joelcrosby.fluxpylons.item.upgrade.filter.common.BaseFilterItem;
import com.joelcrosby.fluxpylons.item.upgrade.filter.common.ItemFilterContainerMenu;
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
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.network.NetworkHooks;


public class RetrieverItem extends BaseFilterItem {

    @Override
    public ItemStackHandler getItemStackHandler(ItemStack stack) {
        return new ItemFilterStackHandler(FluxPylonsContainerMenus.BaseFilterContainerSlots, stack);
    }

    @Override
    protected boolean defaultsToDenyList() {
        return true;
    }

    @Override
    protected boolean supportsInteractionSide() {
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
                        new ItemFilterContainerMenu(windowId, player, stack), containerName),
                (buffer -> buffer.writeItem(stack))
        );
    }

    @Override
    public void update(ItemStack itemStack, GraphNode node, Direction dir, GraphNodeType nodeType) {
        var level = node.getLevel();
        var source = level.getBlockEntity(node.getPos().relative(dir));

        if (source == null) return;
        
        var isDenyList = BaseFilterItem.getIsDenyList(itemStack);
        var matchNbt = BaseFilterItem.getMatchNbt(itemStack);
        var inventory = BaseFilterItem.getInventory(itemStack);
        var interactionDir = BaseFilterItem.getInteractionSide(itemStack);

        var handlerDir = interactionDir == null ? dir.getOpposite() : interactionDir;
        
        var itemHandler = source
                .getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, handlerDir)
                .orElse(null);

        if (itemHandler == null) return;
        
        var rate = nodeType.getItemTransferRate();

        var destinations = node.getNetwork()
                .getRelativeDestinations(GraphDestinationType.ITEMS, source.getBlockPos());

        var iterations = node.getNodeType() == GraphNodeType.ADVANCED ? 4 : 1;
        
        for (var j = 0; j < iterations; j++) {
            
            Outer:
            for (var destination : destinations) {
                if (!destination.canExtract()) continue;

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

                for (var i = 0; i < destinationHandler.getSlots(); i++) {
                    var slot = destinationHandler.getStackInSlot(i);
                    if (slot.isEmpty()) {
                        continue;
                    }

                    var matchesFilter = Utility.matchesFilterInventory(inventory, slot, matchNbt);
                    if (isDenyList == matchesFilter) {
                        continue;
                    }

                    var simulatedExtract = destinationHandler.extractItem(i, rate, true);
                    if (simulatedExtract.isEmpty()) {
                        continue;
                    }

                    var remainder = ItemHandlerHelper.insertItem(itemHandler, simulatedExtract, true).getCount();
                    var amountToExtract = simulatedExtract.getCount() - remainder;

                    if (amountToExtract == 0) continue;

                    var extracted = destinationHandler.extractItem(i, amountToExtract, false);
                    ItemHandlerHelper.insertItem(itemHandler, extracted, false);

                    break Outer;
                }
            }
        }
    }
}
