package com.joelcrosby.fluxpylons.item.upgrade.filter;

import com.joelcrosby.fluxpylons.FluxPylons;
import com.joelcrosby.fluxpylons.item.upgrade.UpgradeItem;
import com.joelcrosby.fluxpylons.pipe.network.graph.GraphNode;
import com.joelcrosby.fluxpylons.pipe.network.graph.GraphNodeType;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.List;

public class FilterItem extends UpgradeItem {
    @Override
    public void update(ItemStack itemStack, GraphNode node, Direction dir, GraphNodeType nodeType) {
        
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
                        new FilterContainerMenu(windowId, playerInventory, player, stack), containerName), 
                (buffer -> buffer.writeItem(stack))
        );
        
        return new InteractionResultHolder<>(InteractionResult.PASS, stack);
    }

    public static void setIsDenyList(ItemStack stack, boolean isDenyList) {
        stack.getOrCreateTag().putBoolean("is-deny-list", isDenyList);
    }
    
    public static Boolean getIsDenyList(ItemStack stack) {
        var compound = stack.getOrCreateTag();
        return compound.getBoolean("is-deny-list");
    }

    public static void setMatchNbt(ItemStack stack, boolean matchNbt) {
        stack.getOrCreateTag().putBoolean("match-nbt", matchNbt);
    }

    public static Boolean getMatchNbt(ItemStack stack) {
        var compound = stack.getOrCreateTag();
        return compound.getBoolean("match-nbt");
    }
    
    public static FilterItemStackHandler getInventory(ItemStack stack) {
        var compound = stack.getOrCreateTag();
        var handler = new FilterItemStackHandler(FilterContainer.SLOTS, stack);
        
        handler.deserializeNBT(compound.getCompound("inventory"));
        
        if (compound.contains("inventory")) {
            return handler;
        }
        
        return setInventory(stack, new FilterItemStackHandler(FilterContainer.SLOTS, stack));
    }

    public static FilterItemStackHandler setInventory(ItemStack stack, FilterItemStackHandler handler) {
        stack.getOrCreateTag().put("inventory", handler.serializeNBT());
        return handler;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
        
        var mc = Minecraft.getInstance();

        if (world == null || mc.player == null) return;

        var sneakPressed = Screen.hasShiftDown();

        if (sneakPressed) {
            var inventory = getInventory(stack);
            
            for (var i = 0; i < inventory.getSlots(); i++) {
                var stackInSlot = inventory.getStackInSlot(i);
                
                if (stackInSlot.isEmpty()) continue;
                
                tooltip.add(new TranslatableComponent(stackInSlot.getItem().getDescriptionId()).withStyle(ChatFormatting.GOLD));
            }
        } else {
            tooltip.add(new TranslatableComponent("info." + FluxPylons.ID + ".shift").setStyle(Style.EMPTY.applyFormat(ChatFormatting.GRAY)));
        }
    }
}
