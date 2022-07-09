package com.joelcrosby.fluxpylons.item.upgrade.filter.common;

import com.joelcrosby.fluxpylons.FluxPylons;
import com.joelcrosby.fluxpylons.Utility;
import com.joelcrosby.fluxpylons.item.upgrade.UpgradeItem;
import com.joelcrosby.fluxpylons.item.upgrade.filter.TagFilterItem;
import com.joelcrosby.fluxpylons.pipe.network.graph.GraphNode;
import com.joelcrosby.fluxpylons.pipe.network.graph.GraphNodeType;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Direction;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseFilterItem extends UpgradeItem {
    
    protected abstract ItemStackHandler getItemStackHandler(ItemStack stack);

    public abstract void openGui(Player player, ItemStack stack);
    
    @Override
    public void update(ItemStack itemStack, GraphNode node, Direction dir, GraphNodeType nodeType) {
        
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return false;
    }
    
    public static void setIsDenyList(ItemStack stack, boolean isDenyList) {
        stack.getOrCreateTag().putBoolean("is-deny-list", isDenyList);
    }
    
    public static void setTags(ItemStack stack, List<String> tags) {
        stack.getOrCreateTag().put("tags", Utility.stringListToTag(tags));
    }

    public static List<String> getTags(ItemStack card) {
        var tags = new ArrayList<String>();
        var compound = card.getOrCreateTag();
        
        if (compound.contains("tags")) {
            var listNBT = compound.getList("tags", Tag.TAG_COMPOUND);
            tags = new ArrayList<>(Utility.TagToStringList(listNBT));
        } else {
            compound.put("tags", Utility.stringListToTag(tags));
        }
        
        return tags;
    }
    
    public static boolean getIsDenyList(ItemStack stack) {
        var compound = stack.getOrCreateTag();
        
        if (compound.contains("is-deny-list")) {
            return compound.getBoolean("is-deny-list");
        }
        
        if (!(stack.getItem() instanceof BaseFilterItem baseFilterItem)) {
            return false;
        }
        
        return baseFilterItem.defaultsToDenyList();
    }

    public static void setMatchNbt(ItemStack stack, boolean matchNbt) {
        stack.getOrCreateTag().putBoolean("match-nbt", matchNbt);
    }

    public static boolean getMatchNbt(ItemStack stack) {
        var compound = stack.getOrCreateTag();
        return compound.getBoolean("match-nbt");
    }

    public static ItemStackHandler setInventory(ItemStack stack, ItemStackHandler handler) {
        stack.getOrCreateTag().put("inventory", handler.serializeNBT());
        return handler;
    }

    protected boolean supportsNbtMatch() {
        return true;
    }
    
    protected boolean defaultsToDenyList() {
        return false;
    }

    public static ItemStackHandler getInventory(ItemStack stack) {
        var compound = stack.getOrCreateTag();
        var item = (BaseFilterItem) stack.getItem();
        var handler = item.getItemStackHandler(stack);

        handler.deserializeNBT(compound.getCompound("inventory"));

        if (compound.contains("inventory")) {
            return handler;
        }
        
        return setInventory(stack, item.getItemStackHandler(stack));
    }
    
    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
        var mc = Minecraft.getInstance();

        if (world == null || mc.player == null) return;

        var sneakPressed = Screen.hasShiftDown();

        if (sneakPressed) {
            var inventory = getInventory(stack);
            var isDenyList = getIsDenyList(stack);
            var matchNbt = getMatchNbt(stack);

            var prefix = "item.fluxpylons.filter.tooltip.";
            
            var isDenyComponent = new TranslatableComponent(prefix + (isDenyList ? "deny" : "allow")).setStyle(Style.EMPTY.applyFormat(isDenyList ? ChatFormatting.RED : ChatFormatting.DARK_GREEN));
            var matchNbtComponent = new TranslatableComponent(prefix + (matchNbt ? "match-nbt" : "ignore-nbt")).setStyle(Style.EMPTY.applyFormat(matchNbt ? ChatFormatting.DARK_GREEN : ChatFormatting.RED));
            
            var divider = new TextComponent(" | ").setStyle(Style.EMPTY.applyFormat(ChatFormatting.DARK_GRAY));
            
            tooltip.add(isDenyComponent.append(supportsNbtMatch() ? divider.append(matchNbtComponent) : TextComponent.EMPTY));
            
            if (stack.getItem() instanceof TagFilterItem) {
                var tags = getTags(stack);

                for (var tag : tags) {
                    tooltip.add(new TextComponent(""));
                    tooltip.add(new TranslatableComponent(tag).withStyle(ChatFormatting.DARK_AQUA));
                }
            } else {
                for (var i = 0; i < inventory.getSlots(); i++) {
                    var stackInSlot = inventory.getStackInSlot(i);

                    if (stackInSlot.isEmpty()) continue;

                    if (i == 0) {
                        tooltip.add(new TextComponent(""));
                    }
                    
                    tooltip.add(new TranslatableComponent(stackInSlot.getItem().getDescriptionId()).withStyle(ChatFormatting.GOLD));
                }
            }
            
        } else {
            tooltip.add(new TranslatableComponent("info." + FluxPylons.ID + ".hold").withStyle(ChatFormatting.GRAY)
                   .append(new TextComponent(" "))
                   .append(new TranslatableComponent("info." + FluxPylons.ID + ".shift").withStyle(ChatFormatting.AQUA)
                   .append(new TextComponent(" "))
                   .append(new TranslatableComponent("info." + FluxPylons.ID + ".more_info").withStyle(ChatFormatting.GRAY)
           )));
        }
    }
}
