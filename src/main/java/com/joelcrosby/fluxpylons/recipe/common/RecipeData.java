package com.joelcrosby.fluxpylons.recipe.common;

import cofh.lib.fluid.FluidIngredient;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;

public class RecipeData {
    public final ResourceLocation recipeId;
    public final int energy;
    
    public ArrayList<ItemStackIngredient> inputItems;
    public ArrayList<FluidIngredient> inputFluids;
    public ArrayList<ItemStack> outputItems;
    public ArrayList<FluidStack> outputFluids;

    public RecipeData(ResourceLocation recipeId,
                      int energy,
                      ArrayList<ItemStackIngredient> inputItems,
                      ArrayList<FluidIngredient> inputFluids,
                      ArrayList<ItemStack> outputItems,
                      ArrayList<FluidStack> outputFluids) {
        this.recipeId = recipeId;

        this.energy = energy;
        this.inputItems = inputItems;
        this.inputFluids = inputFluids;
        this.outputItems = outputItems;
        this.outputFluids = outputFluids;
    }
}
