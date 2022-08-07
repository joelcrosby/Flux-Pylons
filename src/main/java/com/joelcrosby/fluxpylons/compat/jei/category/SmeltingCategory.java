package com.joelcrosby.fluxpylons.compat.jei.category;

import com.joelcrosby.fluxpylons.FluxPylons;
import com.joelcrosby.fluxpylons.FluxPylonsBlocks;
import com.joelcrosby.fluxpylons.compat.jei.FluxPylonsJeiPlugin;
import com.joelcrosby.fluxpylons.machine.SmelterGui;
import com.joelcrosby.fluxpylons.recipe.SmelterRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class SmeltingCategory implements IRecipeCategory<SmelterRecipe> {
    private final IDrawable background;
    private IDrawable icon;
    private IDrawable slotDrawable;
    private IDrawable arrow;
    public static final RecipeType RECIPE_TYPE = new RecipeType(FluxPylonsJeiPlugin.SMELTING_UID, SmelterRecipe.class);

    public SmeltingCategory(IGuiHelper guiHelper) {
        background = guiHelper.drawableBuilder(SmelterGui.TEXTURE, 68, 12, 40, 70).build();
        icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(FluxPylonsBlocks.SMELTER));
        slotDrawable = guiHelper.getSlotDrawable();
        arrow = guiHelper.drawableBuilder(SmelterGui.TEXTURE, 176, 0, 17, 24).buildAnimated(200, IDrawableAnimated.StartDirection.TOP, false);
    }
    
    @Override
    public Component getTitle() {
        return new TranslatableComponent("container." + FluxPylons.ID + "." + "smelting");
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
}
