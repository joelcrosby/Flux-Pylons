package com.joelcrosby.fluxpylons.compat.jei;

import com.joelcrosby.fluxpylons.FluxPylons;
import com.joelcrosby.fluxpylons.FluxPylonsBlocks;
import com.joelcrosby.fluxpylons.FluxPylonsItems;
import com.joelcrosby.fluxpylons.FluxPylonsRecipes;
import com.joelcrosby.fluxpylons.compat.jei.category.SmeltingCategory;
import com.joelcrosby.fluxpylons.compat.jei.category.WashingCategory;
import com.joelcrosby.fluxpylons.compat.jei.container.SmelterContainerHandler;
import com.joelcrosby.fluxpylons.compat.jei.container.WasherContainerHandler;
import com.joelcrosby.fluxpylons.machine.SmelterGui;
import com.joelcrosby.fluxpylons.machine.WasherGui;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@JeiPlugin
public class FluxPylonsJeiPlugin implements IModPlugin {
    public static final ResourceLocation SMELTING_UID = new ResourceLocation(FluxPylons.ID, "plugin/smelting");
    public static final ResourceLocation WASHING_UID = new ResourceLocation(FluxPylons.ID, "plugin/washing");
    
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
            FluxPylonsItems.UPGRADE_EXTRACT.get(),
            FluxPylonsItems.UPGRADE_FLUID_EXTRACT.get(),
            FluxPylonsItems.UPGRADE_FILTER.get(),
            FluxPylonsItems.UPGRADE_FLUID_FILTER.get(),
            FluxPylonsItems.UPGRADE_TAG_FILTER.get(),
            FluxPylonsItems.UPGRADE_RETRIEVER.get(),
            FluxPylonsItems.UPGRADE_FLUID_RETRIEVER.get()
        );
        
        for (var item : itemNames) {
            var key = new ResourceLocation(ForgeRegistries.ITEMS.getKey(item).toString() + "_clear_nbt");
            var manager = recipeManager.byKey(key);
            
            manager.ifPresent(recipe -> hiddenRecipes.add((CraftingRecipe) recipe));
        }

        recipeRegistry.hideRecipes(RecipeTypes.CRAFTING, hiddenRecipes);
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        var guiHelper = registration.getJeiHelpers().getGuiHelper();
        
        registration.addRecipeCategories(new SmeltingCategory(guiHelper));
        registration.addRecipeCategories(new WashingCategory(guiHelper));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(SmeltingCategory.RECIPE_TYPE, getRecipesOfType(FluxPylonsRecipes.FluxPylonsRecipeTypes.SMELTING.get()));
        registration.addRecipes(WashingCategory.RECIPE_TYPE, getRecipesOfType(FluxPylonsRecipes.FluxPylonsRecipeTypes.WASHING.get()));
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addGuiContainerHandler(SmelterGui.class, new SmelterContainerHandler());
        registration.addGuiContainerHandler(WasherGui.class, new WasherContainerHandler());
    }
    
    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(FluxPylonsBlocks.SMELTER.get()).copy(), SmeltingCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(FluxPylonsBlocks.WASHER.get()).copy(), WashingCategory.RECIPE_TYPE);
    }
    
    private static List<Recipe<?>> getRecipesOfType(RecipeType<?> recipeType) {
        return Minecraft.getInstance().level.getRecipeManager().getRecipes().stream()
                .filter(recipe -> recipe.getType() == recipeType)
                .collect(Collectors.toList());
    }
}
