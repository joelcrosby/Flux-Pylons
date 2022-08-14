package com.joelcrosby.fluxpylons.machine.common;

import com.joelcrosby.fluxpylons.recipe.common.BaseRecipe;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class MachineItemStackHandler extends ItemStackHandler {
    private final int inputSlots;
    private final int outputSlots;
    private final boolean hasEnergySlot;
    
    private final SlotRange inputSlotRange;
    private final SlotRange outputSlotRange;

    public MachineItemStackHandler(int inputSlots, int outputSlots, boolean hasEnergySlot) {
        super(inputSlots + outputSlots + (hasEnergySlot ? 1 : 0));
        
        this.inputSlots = inputSlots;
        this.outputSlots = outputSlots;
        this.hasEnergySlot = hasEnergySlot;
        this.inputSlotRange = calculateInputSlots();
        this.outputSlotRange = calculateOutputSlots();
    }

    private SlotRange calculateInputSlots() {
        return new SlotRange(0, this.inputSlots);
    }

    private SlotRange calculateOutputSlots() {
        var high = this.inputSlots + this.outputSlots - 1;

        return new SlotRange(this.inputSlots, high);
    }

    public int getInputSlots() {
        return this.inputSlots;
    }

    public int getOutputSlots() {
        return this.outputSlots;
    }
    
    public SlotRange getInputSlotRange() {
        return this.inputSlotRange;
    }
    
    public SlotRange getOutputSlotRange() {
        return this.outputSlotRange;
    }

    public int getOutputSlot(int i) {
        return this.outputSlotRange.values()[i];
    }
    
    public int getInputSlot(int i) {
        return this.inputSlotRange.values()[i];
    }

    public ItemStack getOutputItemStack(int i) {
        var slot = getOutputSlot(i);
        return this.stacks.get(slot);
    }

    public ItemStack getInputItemStack(int i) {
        var slot = getInputSlot(i);
        return this.stacks.get(slot);
    }

    public boolean canProcessInput(BaseRecipe recipe)
    {
        for (var i = 0; i < recipe.inputItems.size(); i++) {
            var output = recipe.inputItems.get(i);
            var amount = output.getAmount();
            var stack = getInputItemStack(i);

            if (stack.getCount() < amount) {
                return false;
            }
        }
        
        return true;
    }
    
    @Nonnull
    public ItemStack[] getOutputItemStacks()
    {
        var stacks = new ItemStack[outputSlots];
        
        for (var i = 0; i < outputSlots; i++) {
            stacks[i] = this.stacks.get(getOutputSlot(i));
        }
        
        return stacks;
    }

    public int getAvailableOutputSlot(int amount, ItemStack itemStack)
    {
        var i = 0;
        
        for (var stack : getOutputItemStacks()) {
            var canInsert = stack.isEmpty() || stack.sameItem(itemStack);
            var stackHasSpace = stack.getCount() <= (stack.getMaxStackSize() - amount);
            
            if (canInsert && stackHasSpace) {
                return getOutputSlot(i);
            }
            
            i++;
        }

        return -1;
    }
    
    @Nullable
    public ItemStack getAvailableOutputStack(int amount, ItemStack itemStack)
    {
        for (var stack : getOutputItemStacks()) {
            var canInsert = stack.isEmpty() || stack.sameItem(itemStack);
            var stackHasSpace = stack.getCount() <= (stack.getMaxStackSize() - amount);
            
            if (canInsert && stackHasSpace) {
                return stack.copy();
            }
        }

        return null;
    }

    public boolean hasSpaceInOutput(int amount)
    {
        for (var stack : getOutputItemStacks()) {
            if (stack.getCount() > (stack.getMaxStackSize() - amount)) {
                return false;
            }    
        }
        
        return true;
    }

    public boolean hasOutputSpaceForRecipe(BaseRecipe recipe)
    {
        for (var i = 0; i < recipe.outputItems.size(); i++) {
            var output = recipe.outputItems.get(i);
            var amount = output.getCount();
            var stack = getOutputItemStack(i);

            var canInsert = stack.isEmpty() || stack.sameItem(output);
            var stackHasSpace = stack.getCount() <= (stack.getMaxStackSize() - amount);

            if (!canInsert || !stackHasSpace) {
                return false;
            }
        }

        return true;
    }
}
