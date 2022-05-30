package com.joelcrosby.fluxpylons;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.WorldlyContainerHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import org.apache.commons.lang3.tuple.Pair;

import java.util.function.Function;

public class Utility {
    public static <T extends BlockEntity> T getBlockEntity(Class<T> type, BlockGetter world, BlockPos pos) {
        var tile = world.getBlockEntity(pos);
        return type.isInstance(tile) ? (T) tile : null;
    }

    public static ItemStack transferStackInSlot(AbstractContainerMenu container, IMergeItemStack merge, Player player, int slotIndex, Function<ItemStack, Pair<Integer, Integer>> predicate) {
        var inventoryStart = (int) container.slots.stream().filter(slot -> slot.container != player.getInventory()).count();
        var inventoryEnd = inventoryStart + 26;
        var hotbarStart = inventoryEnd + 1;
        var hotbarEnd = hotbarStart + 8;

        var slot = container.slots.get(slotIndex);
        if (slot != null && slot.hasItem()) {
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
                return ItemStack.EMPTY;
            }
            if (newStack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
            if (newStack.getCount() == currentStack.getCount())
                return ItemStack.EMPTY;
            slot.onTake(player, newStack);
            return currentStack;
        }
        return ItemStack.EMPTY;
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

    public interface IMergeItemStack {
        boolean mergeItemStack(ItemStack stack, int startIndex, int endIndex, boolean reverseDirection);
    }
}
