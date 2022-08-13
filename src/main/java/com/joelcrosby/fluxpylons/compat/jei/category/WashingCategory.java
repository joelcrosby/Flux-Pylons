package com.joelcrosby.fluxpylons.compat.jei.category;

import com.joelcrosby.fluxpylons.FluxPylons;
import com.joelcrosby.fluxpylons.FluxPylonsBlocks;
import com.joelcrosby.fluxpylons.compat.jei.FluxPylonsJeiPlugin;
import com.joelcrosby.fluxpylons.machine.WasherGui;
import com.joelcrosby.fluxpylons.recipe.WasherRecipe;
import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
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
import java.util.List;

public class WashingCategory implements IRecipeCategory<WasherRecipe> {
    private final IDrawable background;
    private final IDrawable icon;
    
    @SuppressWarnings("FieldCanBeLocal")
    private final IDrawable slotDrawable;
    
    @SuppressWarnings("FieldCanBeLocal")
    private final IDrawable arrow;
    
    @SuppressWarnings("rawtypes")
    public static final RecipeType RECIPE_TYPE = new RecipeType<>(FluxPylonsJeiPlugin.WASHING_UID, WasherRecipe.class);

    public WashingCategory(IGuiHelper guiHelper) {
        background = guiHelper.drawableBuilder(WasherGui.TEXTURE, 40, 16, 132, 53).build();
        icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(FluxPylonsBlocks.WASHER));
        slotDrawable = guiHelper.getSlotDrawable();
        arrow = guiHelper.drawableBuilder(WasherGui.TEXTURE, 176, 0, 22, 15).buildAnimated(200, IDrawableAnimated.StartDirection.LEFT, false);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public @NotNull RecipeType getRecipeType(){
        return RECIPE_TYPE;
    }
    
    @Override
    public Component getTitle() {
        return new TranslatableComponent("container." + FluxPylons.ID + "." + "washer");
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
        return FluxPylonsJeiPlugin.WASHING_UID;
    }

    @Override
    public Class<? extends WasherRecipe> getRecipeClass() {
        return WasherRecipe.class;
    }

    @Override
    public void draw(WasherRecipe recipe, IRecipeSlotsView recipeSlotsView, PoseStack matrixStack, double mouseX, double mouseY) {
        arrow.draw(matrixStack, 49, 18);
    }
    
    @Override
    public void setRecipe(IRecipeLayoutBuilder recipeLayout, WasherRecipe recipe, IFocusGroup focusGroup) {
        var inputSlot = recipeLayout.addSlot(RecipeIngredientRole.INPUT, 26, 19);
        inputSlot.setSlotName(new TranslatableComponent("terms.fluxpylons.input_slot").getString());
        inputSlot.addIngredients(VanillaTypes.ITEM_STACK, Arrays.stream(recipe.inputItems.get(0).getItems()).toList());

        var fluidSlot = recipeLayout.addSlot(RecipeIngredientRole.INPUT, 2, 3);
        fluidSlot.setSlotName(new TranslatableComponent("terms.fluxpylons.input_slot").getString());
        fluidSlot.addIngredients(ForgeTypes.FLUID_STACK, Arrays.stream(recipe.inputFluids.get(0).getFluids()).toList());
        fluidSlot.setFluidRenderer(10_000, true, 16, 47);

        for (var i = 0; i < recipe.outputItems.size(); i++) {
            var handler = recipeLayout.addSlot(RecipeIngredientRole.OUTPUT, 87 + i * 18, 19);
            handler.setSlotName(new TranslatableComponent("terms.fluxpylons.output_slot").getString());
            handler.addIngredients(VanillaTypes.ITEM_STACK, List.of(recipe.outputItems.get(i)));
        }
    }
}
