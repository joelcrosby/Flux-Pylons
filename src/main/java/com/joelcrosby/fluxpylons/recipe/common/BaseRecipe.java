package com.joelcrosby.fluxpylons.recipe.common;

import cofh.lib.fluid.FluidIngredient;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class BaseRecipe implements Recipe<Container> {
    public final ResourceLocation recipeId;
    public int energy;

    public final List<Ingredient> inputItems = new ArrayList<>();
    public final List<FluidIngredient> inputFluids = new ArrayList<>();
    public final List<ItemStack> outputItems = new ArrayList<>();
    public final List<FluidStack> outputFluids = new ArrayList<>();
    
    public BaseRecipe(ResourceLocation recipeId, RecipeData data) {
        this.recipeId = recipeId;

        this.energy = data.energy;
        
        this.inputItems.addAll(data.inputItems);
        this.inputFluids.addAll(data.inputFluids);
        this.outputItems.addAll(data.outputItems);
        this.outputFluids.addAll(data.outputFluids);
    }
    
    public abstract RecipeSerializer<?> getSerializer();
    
    public int getInputItemsCount(int index) {
        return inputItems.size();
    }

    public int getInputFluidAmount(int index) {
        return 100;
    }

    public int getOutputItemsCount(int index) {
        return outputItems.get(index).getCount();
    }

    public int getOutputFluidAmount(int index) {
        return outputFluids.get(index).getAmount();
    }

    @Override
    public boolean matches(Container inv, Level worldIn) {
        if (inv.isEmpty()) return false;
        
        for (var inputItem : inputItems) {
            var item = Arrays.stream(inputItem.getItems()).findFirst().orElse(null).getItem();
            
            if (item == null) {
                return false;
            }
            
            if (inv.countItem(item) == 0) {
                return false;
            }
        }

        return true;
    }

    @Override
    public ItemStack assemble(Container inv) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem() {
        return outputItems.stream().findFirst().orElse(ItemStack.EMPTY);
    }

    @Override
    public ResourceLocation getId() {
        return recipeId;
    }

    @Override
    public abstract RecipeType<?> getType();
}