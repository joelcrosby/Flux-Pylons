package com.joelcrosby.fluxpylons.compat.jei.category;

import com.joelcrosby.fluxpylons.FluxPylons;
import com.joelcrosby.fluxpylons.FluxPylonsBlocks;
import com.joelcrosby.fluxpylons.compat.jei.FluxPylonsJeiPlugin;
import com.joelcrosby.fluxpylons.machine.SmelterGui;
import com.joelcrosby.fluxpylons.recipe.SmelterRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class SmeltingCategory implements IRecipeCategory<SmelterRecipe> {
    private final IDrawable background;
    private final IDrawable icon;
    
    @SuppressWarnings("FieldCanBeLocal")
    private final IDrawable slotDrawable;
    
    @SuppressWarnings("FieldCanBeLocal")
    private final IDrawable arrow;
    
    @SuppressWarnings("rawtypes")
    public static final RecipeType RECIPE_TYPE = new RecipeType<>(FluxPylonsJeiPlugin.SMELTING_UID, SmelterRecipe.class);

    public SmeltingCategory(IGuiHelper guiHelper) {
        background = guiHelper.drawableBuilder(SmelterGui.TEXTURE, 27, 22, 144, 40).build();
        icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(FluxPylonsBlocks.SMELTER));
        slotDrawable = guiHelper.getSlotDrawable();
        arrow = guiHelper.drawableBuilder(SmelterGui.TEXTURE, 176, 0, 22, 15).buildAnimated(200, IDrawableAnimated.StartDirection.LEFT, false);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public @NotNull RecipeType getRecipeType(){
        return RECIPE_TYPE;
    }
    
    @Override
    public Component getTitle() {
        return new TranslatableComponent("container." + FluxPylons.ID + "." + "smelter");
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public ResourceLocation getUid() {
        return FluxPylonsJeiPlugin.SMELTING_UID;
    }

    @Override
    public Class<? extends SmelterRecipe> getRecipeClass() {
        return SmelterRecipe.class;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder recipeLayout, SmelterRecipe recipe, IFocusGroup focusGroup) {
        for (var i = 0; i < recipe.inputItems.size(); i++) {
            var handler = recipeLayout.addSlot(RecipeIngredientRole.INPUT, 3 + i * 18, 3);
            handler.setSlotName(new TranslatableComponent("terms.fluxpylons.input_slot").getString());
            handler.addIngredients(VanillaTypes.ITEM_STACK, Arrays.stream(recipe.inputItems.get(i).getItems()).toList());
        }

        for (var i = 0; i < recipe.outputItems.size(); i++) {
            var handler = recipeLayout.addSlot(RecipeIngredientRole.OUTPUT, 101 + i * 18, 13);
            handler.setSlotName(new TranslatableComponent("terms.fluxpylons.output_slot").getString());
            handler.addIngredients(VanillaTypes.ITEM_STACK, recipe.outputItems);
        }
    }
}
