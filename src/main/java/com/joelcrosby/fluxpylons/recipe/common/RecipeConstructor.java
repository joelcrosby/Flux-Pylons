package com.joelcrosby.fluxpylons.recipe.common;

import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface RecipeConstructor<T extends BaseRecipe> {
    @Nullable
    T create(RecipeData recipeData);
}
