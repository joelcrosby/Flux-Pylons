package com.joelcrosby.fluxpylons.compat.jei;

import com.joelcrosby.fluxpylons.FluxPylons;
import com.joelcrosby.fluxpylons.FluxPylonsItems;
import com.joelcrosby.fluxpylons.FluxPylonsRecipes;
import com.joelcrosby.fluxpylons.compat.jei.category.SmeltingCategory;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@JeiPlugin
public class FluxPylonsJeiPlugin implements IModPlugin {
    public static final ResourceLocation SMELTING_UID = new ResourceLocation(FluxPylons.ID, "plugin/smelting");
    
    @Nonnull
    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(FluxPylons.ID, "jei_plugin");
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        var recipeRegistry = jeiRuntime.getRecipeManager();
        var recipeManager = Minecraft.getInstance().level.getRecipeManager();
        var hiddenRecipes = new ArrayList<CraftingRecipe>();
        
        var itemNames = List.of(
            FluxPylonsItems.UPGRADE_EXTRACT,
            FluxPylonsItems.UPGRADE_FLUID_EXTRACT,
            FluxPylonsItems.UPGRADE_FILTER,
            FluxPylonsItems.UPGRADE_FLUID_FILTER,
            FluxPylonsItems.UPGRADE_TAG_FILTER,
            FluxPylonsItems.UPGRADE_RETRIEVER,
            FluxPylonsItems.UPGRADE_FLUID_RETRIEVER
        );
        
        for (var item : itemNames) {
            var key = new ResourceLocation(item.getRegistryName().toString() + "_clear_nbt");
            var manager = recipeManager.byKey(key);
            
            manager.ifPresent(recipe -> hiddenRecipes.add((CraftingRecipe) recipe));
        }

        recipeRegistry.hideRecipes(RecipeTypes.CRAFTING, hiddenRecipes);
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        var guiHelper = registration.getJeiHelpers().getGuiHelper();
        
        registration.addRecipeCategories(new SmeltingCategory(guiHelper));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(SmeltingCategory.RECIPE_TYPE, getRecipesOfType(FluxPylonsRecipes.FluxPylonsRecipeTypes.SMELTING));
    }

    private static List<Recipe<?>> getRecipesOfType(RecipeType<?> recipeType) {
        return Minecraft.getInstance().level.getRecipeManager().getRecipes().stream()
                .filter(recipe -> recipe.getType() == recipeType)
                .collect(Collectors.toList());
    }
}
