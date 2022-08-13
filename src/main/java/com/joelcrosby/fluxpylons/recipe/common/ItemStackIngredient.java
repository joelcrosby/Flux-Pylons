package com.joelcrosby.fluxpylons.recipe.common;

import com.google.gson.JsonElement;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.AbstractIngredient;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import net.minecraftforge.common.crafting.VanillaIngredientSerializer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ItemStackIngredient extends AbstractIngredient {

    @Nonnull
    private final Ingredient ingredient;
    private final int amount;
    
    public ItemStackIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
        this.amount = 1;
    }

    public ItemStackIngredient(Ingredient ingredient, int amount) {
        this.ingredient = ingredient;
        this.amount = amount;
    }
    
    public int getAmount() {
        return this.amount;
    }
    
    public static ItemStackIngredient of(ItemStack itemStack) {
        return new ItemStackIngredient(Ingredient.of(itemStack));
    }

    public static ItemStackIngredient fromJson(JsonElement element) {
        return new ItemStackIngredient(Ingredient.fromJson(element));
    }

    public static ItemStackIngredient fromJson(JsonElement element, int amount) {
        return new ItemStackIngredient(Ingredient.fromJson(element), amount);
    }

    @Override
    public ItemStack[] getItems() {
        var count = ingredient.getItems().length;
        var items = new ItemStack[count];
        
        for (var i = 0; i < count; i++) {
            var stack = ingredient.getItems()[i];
            var copy = stack.copy();
            copy.setCount(this.amount);
            
            items[i] = copy;
        }
        
        return items;
    }
    
    @Override
    public boolean test(@Nullable ItemStack stack) {

        return ingredient.test(stack);
    }

    @Override
    public IntList getStackingIds() {

        return ingredient.getStackingIds();
    }

    @Override
    public boolean isEmpty() {

        return ingredient.isEmpty();
    }
    
    @Override
    public boolean isSimple() {
        return ingredient.isSimple();
    }

    @Override
    public IIngredientSerializer<? extends Ingredient> getSerializer() {
        return ingredient.isVanilla() ? VanillaIngredientSerializer.INSTANCE : ingredient.getSerializer();
    }

    @Override
    public JsonElement toJson() {
        return ingredient.toJson();
    }
}
