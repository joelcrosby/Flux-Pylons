package com.joelcrosby.fluxpylons.machine;

import com.joelcrosby.fluxpylons.FluxPylonsBlockEntities;
import com.joelcrosby.fluxpylons.machine.common.MachineBlockEntity;
import com.joelcrosby.fluxpylons.machine.common.MachineState;
import com.joelcrosby.fluxpylons.recipe.SmelterRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicReference;

public class SmelterBlockEntity extends MachineBlockEntity {

    private final ItemStackHandler inventory = new ItemStackHandler(9);
    private final AtomicReference<ItemStack> inputItemStack = new AtomicReference<ItemStack>(new ItemStack(Items.AIR,0));
    
    public SmelterBlockEntity(BlockPos pos, BlockState state) {
        super(FluxPylonsBlockEntities.SMELTER, pos, state);
    }

    @Nullable
    public AbstractContainerMenu createMenu(int window, Inventory inventory, Player player) {
        return new SmelterContainerMenu(window, player, worldPosition);
    }
    
    public void tick() {
        sendClientUpdate();
        
        var input = inventory.getStackInSlot(0).copy();
        var container = new SimpleContainer(6);
        
        for (var i = 0; i < 6; i++) {
            var inputStack = inventory.getStackInSlot(i);
            container.addItem(inputStack);
        }
        
        var output = inventory.getStackInSlot(7).copy();
        var recipe = SmelterRecipe.getRecipe(level, container);
        
        if (recipe == null) return;
        
        var energy = 20;
        
        inputItemStack.set(input.copy());

        if (input.isEmpty()) {
            return;
        }
        
        if (output.getCount() + recipe.getOutputItemsCount(0) < 64 && canConsumeEnergy()) {
            if (state == MachineState.COMPLETE) { // The processing is about to be complete
                // Extract the inputted item
                
                for (var i = 0; i < recipe.inputItems.size(); i++) {
                    inventory.extractItem(i, 1, false);
                }

                // Get output stack from the recipe
                var newOutputStack = recipe.outputItems.get(0).copy();


                // Manipulating the Output slot
                if (output.getItem() != newOutputStack.getItem() || output.getItem() == Items.AIR) {
                    if (output.getItem() == Items.AIR) { // Fix air > 1 jamming slots
                        output.setCount(1);
                    }
                    
                    newOutputStack.setCount(recipe.getOutputItemsCount(0));
                    inventory.insertItem(6, newOutputStack.copy(), false); // CRASH the game if this is not empty!
                } else { // Assuming the recipe output item is already in the output slot
                    output.setCount(recipe.getOutputItemsCount(0)); // Simply change the stack to equal the output amount
                    inventory.insertItem(6, output.copy(), false); // Place the new output stack on top of the old one
                }
                
                state = MachineState.IDLE;
                
                consumeEnergy(energy);
                setChanged();
            } else if (state == MachineState.PROCESSING) { // In progress
                consumeEnergy(energy);
                
                if (consumedEnergy >= recipe.energy) {
                    state = MachineState.COMPLETE;
                }
            } else { // Check if we should start processing
                if (output.isEmpty() || output.getItem() == recipe.outputItems.get(0).getItem()) {
                    state = MachineState.PROCESSING;
                } else {
                    state = MachineState.IDLE;
                }
            }
        } else { // This is if we reach the maximum in the slots; or no power
            if (!canConsumeEnergy()){ // if no power
                state = MachineState.IDLE;
            } else { // zero in other cases
                state = MachineState.IDLE;
            }
        }
    }

    @Override
    public ItemStackHandler getItemStackHandler() {
        return inventory;
    }
}
