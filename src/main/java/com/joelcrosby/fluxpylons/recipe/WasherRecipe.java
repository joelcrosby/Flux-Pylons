package com.joelcrosby.fluxpylons.recipe;

import com.joelcrosby.fluxpylons.FluxPylonsRecipes;
import com.joelcrosby.fluxpylons.recipe.common.BaseRecipe;
import com.joelcrosby.fluxpylons.recipe.common.BaseRecipeSerializer;
import com.joelcrosby.fluxpylons.recipe.common.RecipeData;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.HashMap;


public class WasherRecipe extends BaseRecipe {
    public static final BaseRecipeSerializer<WasherRecipe> SERIALIZER = new BaseRecipeSerializer<>(WasherRecipe::new);
    public static final RecipeType<WasherRecipe> RECIPE_TYPE = FluxPylonsRecipes.FluxPylonsRecipeTypes.WASHING.get();

    public WasherRecipe(RecipeData data) {
        super(data);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return new BaseRecipeSerializer<>(WasherRecipe::new);
    }

    @Override
    public RecipeType<?> getType() {
        return RECIPE_TYPE;
    }

    protected static final HashMap<Integer, WasherRecipe> recipeHashMap = new HashMap<>();
    
    public static WasherRecipe getRecipe(Level level, Container container) {
            for (var recipe : level.getRecipeManager().getRecipes()) {
                if (recipe instanceof WasherRecipe washerRecipe) {
                    if (washerRecipe.matches(container, level)) {
                        var hash = container.hashCode();
                        recipeHashMap.put(hash, washerRecipe);
                    }
                }
            }

        var hash = container.hashCode();
        return recipeHashMap.get(hash);
    }
}
