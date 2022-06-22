package com.joelcrosby.fluxpylons.util;

import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapelessRecipe;

public class ClearNbtRecipe extends ShapelessRecipe {
    public ClearNbtRecipe(ResourceLocation resourceLocation, String group, ItemStack result, NonNullList<Ingredient> ingredients) {
        super(resourceLocation, group, result, ingredients);
    }

    public static class Serializer extends ShapelessRecipe.Serializer {
        public ClearNbtRecipe fromJson(ResourceLocation resourceLocation, JsonObject jsonObject) {
            ShapelessRecipe vanilla = super.fromJson(resourceLocation, jsonObject);
            return new ClearNbtRecipe(vanilla.getId(), vanilla.getGroup(), vanilla.getResultItem(), vanilla.getIngredients());
        }

        public ClearNbtRecipe fromNetwork(ResourceLocation resourceLocation, FriendlyByteBuf byteBuf) {
            ShapelessRecipe vanilla = super.fromNetwork(resourceLocation, byteBuf);
            return new ClearNbtRecipe(vanilla.getId(), vanilla.getGroup(), vanilla.getResultItem(), vanilla.getIngredients());
        }

        public void toNetwork(FriendlyByteBuf byteBuf, ShapelessRecipe shapelessRecipe) {
            super.toNetwork(byteBuf, shapelessRecipe);
        }
    }
}
