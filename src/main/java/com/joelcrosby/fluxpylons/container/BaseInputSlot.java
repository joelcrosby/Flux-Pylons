package com.joelcrosby.fluxpylons.container;

import com.joelcrosby.fluxpylons.recipe.common.BaseRecipe;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class BaseInputSlot extends SlotItemHandler {
    public BaseInputSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
    }

    public <C extends Container, T extends Recipe<C>> boolean checkRecipe(BaseRecipe recipe, ItemStack stack) {
        if (recipe == null) return false;

        for (var ingredient : recipe.inputItems) {
            for (var testStack : ingredient.getItems()) {
                if (stack.getItem() == testStack.getItem()) {
                    return true;
                }
            }
        }
        
        return false;
    }
}
