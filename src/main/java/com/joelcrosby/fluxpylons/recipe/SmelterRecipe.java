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


public class SmelterRecipe extends BaseRecipe {
    public static final BaseRecipeSerializer<SmelterRecipe> SERIALIZER = new BaseRecipeSerializer<>(SmelterRecipe::new);
    public static final RecipeType<SmelterRecipe> RECIPE_TYPE = FluxPylonsRecipes.FluxPylonsRecipeTypes.SMELTING.get();

    public SmelterRecipe(RecipeData data) {
        super(data);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return new BaseRecipeSerializer<>(SmelterRecipe::new);
    }

    @Override
    public RecipeType<?> getType() {
        return RECIPE_TYPE;
    }

    protected static final HashMap<Integer, SmelterRecipe> recipeHashMap = new HashMap<>();
    
    public static SmelterRecipe getRecipe(Level level, Container container) {
            for (var recipe : level.getRecipeManager().getRecipes()) {
                if (recipe instanceof SmelterRecipe smelterRecipe) {
                    if (smelterRecipe.matches(container, level)) {
                        var hash = container.hashCode();
                        recipeHashMap.put(hash, smelterRecipe);
                    }
                }
            }

        var hash = container.hashCode();
        return recipeHashMap.get(hash);
    }
}
