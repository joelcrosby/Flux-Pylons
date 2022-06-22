package com.joelcrosby.fluxpylons.setup;

import com.joelcrosby.fluxpylons.FluxPylons;
import com.joelcrosby.fluxpylons.FluxPylonsItems;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.CraftingRecipe;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

@JeiPlugin
public class Jei implements IModPlugin {

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
            FluxPylonsItems.UPGRADE_TAG_FILTER
        );
        
        for (var item : itemNames) {
            var key = new ResourceLocation(item.getRegistryName().toString() + "_clear_nbt");
            var manager = recipeManager.byKey(key);
            
            manager.ifPresent(recipe -> hiddenRecipes.add((CraftingRecipe) recipe));
        }

        recipeRegistry.hideRecipes(RecipeTypes.CRAFTING, hiddenRecipes);
    }
}
