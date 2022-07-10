package com.joelcrosby.fluxpylons;

import com.joelcrosby.fluxpylons.pipe.PipeBlockEntity;
import com.joelcrosby.fluxpylons.util.FluidHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.WorldlyContainerHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class Utility {
    public static <T extends BlockEntity> T getBlockEntity(Class<T> type, BlockGetter world, BlockPos pos) {
        var tile = world.getBlockEntity(pos);
        return type.isInstance(tile) ? (T) tile : null;
    }

    public static <T extends BlockEntity> T getExistingBlockEntity(Class<T> type, BlockGetter world, BlockPos pos) {
        var tile = world.getExistingBlockEntity(pos);
        return type.isInstance(tile) ? (T) tile : null;
    }
    
    public static ItemStack transferStackInSlot(AbstractContainerMenu container, IMergeItemStack merge, Player player, int slotIndex, Function<ItemStack, Pair<Integer, Integer>> predicate) {
        var inventoryStream = container.slots.stream().filter(slot -> slot.container != player.getInventory());
        var inventoryStart = (int) inventoryStream.count();
        var inventoryEnd = inventoryStart + 26;
        var hotbarStart = inventoryEnd + 1;
        var hotbarEnd = hotbarStart + 8;

        var slot = container.slots.get(slotIndex);

        if (slot == null || !slot.hasItem()) {
            return ItemStack.EMPTY;
        }
        
        var newStack = slot.getItem();
        var currentStack = newStack.copy();

        if (slotIndex >= inventoryStart) {
            // shift into this container here
            // mergeItemStack with the slots that newStack should go into
            // return an empty stack if mergeItemStack fails
            // shift into this container here
            // mergeItemStack with the slots that newStack should go into
            // return an empty stack if mergeItemStack fails
            var slots = predicate.apply(newStack);
            
            if (slots != null) {
                if (merge.mergeItemStack(newStack, slots.getLeft(), slots.getRight(), false))
                    return ItemStack.EMPTY;
            }
            
            else if (slotIndex >= inventoryStart && slotIndex <= inventoryEnd) {
                if (merge.mergeItemStack(newStack, hotbarStart, hotbarEnd + 1, false))
                    return ItemStack.EMPTY;
            } else if (slotIndex >= inventoryEnd + 1 && slotIndex < hotbarEnd + 1 && merge.mergeItemStack(newStack, inventoryStart, inventoryEnd + 1, false)) {
                return ItemStack.EMPTY;
            }
        } else if (merge.mergeItemStack(newStack, inventoryStart, hotbarEnd + 1, false)) {
            slot.setChanged();
            return ItemStack.EMPTY;
        }
        
        if (newStack.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        
        if (newStack.getCount() == currentStack.getCount()) {
            return ItemStack.EMPTY;
        }
        
        slot.onTake(player, newStack);
        
        return currentStack;
    }

    public static IItemHandler getBlockItemHandler(LevelAccessor world, BlockPos pos, Direction direction) {
        var state = world.getBlockState(pos);
        var block = state.getBlock();
        if (!(block instanceof WorldlyContainerHolder holder))
            return null;
        var inventory = holder.getContainer(state, world, pos);
        if (inventory == null)
            return null;
        return new SidedInvWrapper(inventory, direction);
    }

    public static void sendBlockEntityToClients(PipeBlockEntity tile) {
        var level = (ServerLevel) tile.getLevel();
        var entities = level.getChunkSource().chunkMap.getPlayers(new ChunkPos(tile.getBlockPos()), false);
        var packet = ClientboundBlockEntityDataPacket.create(tile, BlockEntity::saveWithoutMetadata);
        for (var e : entities)
            e.connection.send(packet);
    }

    public static void addTooltip(String name, List<Component> tooltip) {
        if (Screen.hasShiftDown()) {
            var content = I18n.get("info." + FluxPylons.ID + "." + name).split("\n");
            for (var s : content)
                tooltip.add(new TextComponent(s).setStyle(Style.EMPTY.applyFormat(ChatFormatting.DARK_PURPLE)));
        } else {
            tooltip.add(new TranslatableComponent("info." + FluxPylons.ID + ".shift").setStyle(Style.EMPTY.applyFormat(ChatFormatting.GRAY)));
        }
    }

    public static void addTooltip(String name, List<Component> textComponents, List<Component> tooltip) {
        if (Screen.hasShiftDown()) {
            var content = I18n.get("info." + FluxPylons.ID + "." + name).split("\n");
            for (var s : content)
                tooltip.add(new TextComponent(s).setStyle(Style.EMPTY.applyFormat(ChatFormatting.DARK_PURPLE)));
        } else {
            tooltip.addAll(textComponents);
            tooltip.add(new TranslatableComponent("info." + FluxPylons.ID + ".hold").withStyle(ChatFormatting.GRAY)
                    .append(new TextComponent(" "))
                    .append(new TranslatableComponent("info." + FluxPylons.ID + ".shift").withStyle(ChatFormatting.AQUA)
                    .append(new TextComponent(" "))
                    .append(new TranslatableComponent("info." + FluxPylons.ID + ".more_info").withStyle(ChatFormatting.GRAY)
            )));
        }
    }

    public static ListTag stringListToTag(List<String> list) {
        var nbtList = new ListTag();
        
        for (String string : list) {
            var tag = new CompoundTag();
            tag.putString("list", string);
            nbtList.add(tag);
        }
        
        return nbtList;
    }

    public static List<String> TagToStringList(ListTag nbtList) {
        var list = new ArrayList<String>();
        
        for (int i = 0; i < nbtList.size(); i++) {
            CompoundTag tag = nbtList.getCompound(i);
            list.add(tag.getString("list"));
        }
        
        return list;
    }

    public static boolean inBounds(int x, int y, int w, int h, double ox, double oy) {
        return ox >= x && ox <= x + w && oy >= y && oy <= y + h;
    }
    
    public static <T> T getIndex(Set<T> set, int index) {
        var i = 0;
        
        for (T entry:set) {
            if (index == i) return entry;
            i++;
        }
        
        return null;
    }

    public static boolean matchesFilterInventory(ItemStackHandler inventory, ItemStack itemStack, boolean matchNbt) {
        var isMatch = false;

        for (var i = 0; i < inventory.getSlots(); i++) {
            var slotStack = inventory.getStackInSlot(i);

            if (slotStack.isEmpty()) continue;

            if (matchNbt) {
                isMatch = ItemHandlerHelper.canItemStacksStack(itemStack, slotStack);
            } else {
                isMatch = itemStack.sameItem(slotStack);
            }

            if (isMatch) break;
        }

        return isMatch;
    }

    public static boolean matchesFilterInventory(ItemStackHandler inventory, FluidStack fluidStack) {
        var isMatch = false;

        for (var i = 0; i < inventory.getSlots(); i++) {
            var slotStack = inventory.getStackInSlot(i);

            if (slotStack.isEmpty()) continue;

            var slotFluidStack = FluidHelper.getFromStack(slotStack, true).getValue();

            if (slotFluidStack == null) continue;

            isMatch = slotFluidStack.isFluidEqual(fluidStack);

            if (isMatch) break;
        }

        return isMatch;
    }

    public interface IMergeItemStack {
        boolean mergeItemStack(ItemStack stack, int startIndex, int endIndex, boolean reverseDirection);
    }
}
