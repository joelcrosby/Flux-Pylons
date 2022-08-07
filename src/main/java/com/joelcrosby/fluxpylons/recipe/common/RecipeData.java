package com.joelcrosby.fluxpylons.recipe.common;

import cofh.lib.fluid.FluidIngredient;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;

public class RecipeData {
    public int energy;
    
    public ArrayList<Ingredient> inputItems;
    public ArrayList<FluidIngredient> inputFluids;
    public ArrayList<ItemStack> outputItems;
    public ArrayList<FluidStack> outputFluids;

    public RecipeData(int energy,
                      ArrayList<Ingredient> inputItems,
                      ArrayList<FluidIngredient> inputFluids,
                      ArrayList<ItemStack> outputItems,
                      ArrayList<FluidStack> outputFluids) {

        this.energy = energy;
        this.inputItems = inputItems;
        this.inputFluids = inputFluids;
        this.outputItems = outputItems;
        this.outputFluids = outputFluids;
    }
}
