package com.joelcrosby.fluxpylons.item.upgrade.filter;

import com.joelcrosby.fluxpylons.FluxPylons;
import com.joelcrosby.fluxpylons.item.upgrade.UpgradeItem;
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
import net.minecraftforge.network.NetworkHooks;

public class UpgradeFilterItem extends UpgradeItem {
    @Override
    public void update(GraphNode node, Direction dir, GraphNodeType nodeType) {
        
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return false;
    }
    
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        var stack = player.getItemInHand(interactionHand);
        
        if (level.isClientSide()) return new InteractionResultHolder<>(InteractionResult.PASS, stack);

        var containerName = new TranslatableComponent("container." + FluxPylons.ID + "." + this.getRegistryName().getPath());
        
        NetworkHooks.openGui((ServerPlayer) player,
                new SimpleMenuProvider((windowId, playerInventory, playerEntity) ->
                        new UpgradeFilterContainerMenu(windowId, playerInventory, player, stack), containerName), 
                (buffer -> buffer.writeItem(stack))
        );
        
        return new InteractionResultHolder<>(InteractionResult.PASS, stack);
    }

    public static UpgradeFilterItemStackHandler getInventory(ItemStack stack) {
        var compound = stack.getOrCreateTag();
        var handler = new UpgradeFilterItemStackHandler(UpgradeFilterContainer.SLOTS, stack);
        
        handler.deserializeNBT(compound.getCompound("inventory"));
        
        if (compound.contains("inventory")) {
            return handler;
        }
        
        return setInventory(stack, new UpgradeFilterItemStackHandler(UpgradeFilterContainer.SLOTS, stack));
    }

    public static UpgradeFilterItemStackHandler setInventory(ItemStack stack, UpgradeFilterItemStackHandler handler) {
        stack.getOrCreateTag().put("inventory", handler.serializeNBT());
        return handler;
    }
}
