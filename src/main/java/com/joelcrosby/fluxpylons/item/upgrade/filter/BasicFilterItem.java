package com.joelcrosby.fluxpylons.item.upgrade.filter;

import com.joelcrosby.fluxpylons.FluxPylons;
import com.joelcrosby.fluxpylons.FluxPylonsContainerMenus;
import com.joelcrosby.fluxpylons.item.upgrade.filter.common.BaseFilterItem;
import com.joelcrosby.fluxpylons.item.upgrade.filter.common.ItemFilterContainerMenu;
import com.joelcrosby.fluxpylons.item.upgrade.filter.common.ItemFilterStackHandler;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.network.NetworkHooks;

public class BasicFilterItem extends BaseFilterItem {
    @Override
    public ItemStackHandler getItemStackHandler(ItemStack stack) {
        return new ItemFilterStackHandler(FluxPylonsContainerMenus.BaseFilterContainerSlots, stack);
    }
    
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        var stack = player.getItemInHand(interactionHand);

        if (level.isClientSide()) return new InteractionResultHolder<>(InteractionResult.PASS, stack);

        var containerName = new TranslatableComponent("container." + FluxPylons.ID + "." + this.getRegistryName().getPath());

        NetworkHooks.openGui((ServerPlayer) player,
                new SimpleMenuProvider((windowId, playerInventory, playerEntity) ->
                        new ItemFilterContainerMenu(windowId, player, stack), containerName),
                (buffer -> buffer.writeItem(stack))
        );

        return new InteractionResultHolder<>(InteractionResult.PASS, stack);
    }
}
