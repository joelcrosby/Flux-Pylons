package com.joelcrosby.fluxpylons.recipe.common;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;

public class FluxPylonsRecipeType<T extends Recipe<?>> implements RecipeType<T> {
    private ResourceLocation name;

    public FluxPylonsRecipeType(ResourceLocation name){
        this.name = name;
    }

    @Override
    public String toString(){
        return name.toString();
    }

}
