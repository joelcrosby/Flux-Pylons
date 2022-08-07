package com.joelcrosby.fluxpylons.recipe.common;

import cofh.lib.fluid.FluidIngredient;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class BaseRecipeSerializer<T extends BaseRecipe> extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<T> {

    private final RecipeConstructor<T> recipeConstructor;
    private ResourceLocation resourceLocation;

    public BaseRecipeSerializer(RecipeConstructor<T> recipeConstructor) {
        this.recipeConstructor = recipeConstructor;
    }
    
    private Ingredient ingredient(JsonElement element) {
        if (element == null || element.isJsonNull() || !element.isJsonObject()) {
            return Ingredient.of(ItemStack.EMPTY);
        }

        var object = element.getAsJsonObject();
        
        if (object.has("value")) {
            return Ingredient.fromJson(object.get("value"));
        }
        
        return Ingredient.fromJson(element);
    }

    private FluidIngredient fluidIngredient(JsonElement element) {
        if (element == null || element.isJsonNull()) {
            return FluidIngredient.of(FluidStack.EMPTY);
        }

        var object = element.getAsJsonObject();

        if (object.has("value")) {
            return FluidIngredient.fromJson(object.get("value"));
        }
        
        return FluidIngredient.fromJson(element);
    }

    private ItemStack itemStack(JsonElement element) {
        if (element == null || element.isJsonNull()) {
            return ItemStack.EMPTY;
        }
        
        var jsonObject = element.getAsJsonObject();

        var count = jsonObject.get("count").getAsInt();
        var itemJson = jsonObject.get("item").getAsString();
        
        var item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemJson));

        return new ItemStack(item, count);
    }

    private FluidStack fluidStack(JsonElement element) {
        if (element == null || element.isJsonNull()) {
            return FluidStack.EMPTY;
        }

        var jsonObject = element.getAsJsonObject();

        var amount = jsonObject.get("amount").getAsInt();
        var itemJson = jsonObject.get("fluid").getAsString();

        var fluid = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(itemJson));

        return new FluidStack(fluid, amount);
    }
    
    @Override
    public T fromJson(ResourceLocation recipeId, JsonObject json) {

        var energy = json.get("energy").getAsInt();

        var inputItems = new ArrayList<Ingredient>();
        var inputFluids = new ArrayList<FluidIngredient>();
        var outputItems = new ArrayList<ItemStack>();
        var outputFluids = new ArrayList<FluidStack>();
        
        var ingredientJson = json.get("ingredient");
        var ingredientsJson = json.get("ingredients");
        
        if (ingredientJson != null && ingredientJson.isJsonObject()) {
            inputItems.add(ingredient(ingredientJson)); 
        } else if (ingredientsJson != null && ingredientsJson.isJsonArray()) {
            for (var in : ingredientsJson.getAsJsonArray()) {
                if (in.isJsonObject()) {
                    var object = in.getAsJsonObject();

                    if (object.has("fluid")) {
                        inputFluids.add(fluidIngredient(object));
                    } else {
                        inputItems.add(ingredient(object));
                    }
                }
            }
        }

        var resultJson = json.get("result");
        var resultsJson = json.get("results");
        
        if (resultJson != null) {
            if (resultJson.isJsonObject()) {
                if (resultJson.getAsJsonObject().has("fluid")) {
                    outputItems.add(itemStack(resultJson));
                }
            } else if (resultJson.isJsonArray()) {
                for (var output : resultJson.getAsJsonArray()) {
                    if (output.isJsonObject()) {
                        var object = output.getAsJsonObject();

                        if (object.has("fluid")) {
                            outputFluids.add(fluidStack(object));
                        } else {
                            outputItems.add(itemStack(object));
                        }
                    }
                }
            }
        } else if (resultsJson != null && resultsJson.isJsonArray()) {
            for (var in : resultsJson.getAsJsonArray()) {
                if (in.isJsonObject()) {
                    var object = in.getAsJsonObject();

                    if (object.has("fluid")) {
                        outputFluids.add(fluidStack(object));
                    } else {
                        outputItems.add(itemStack(object));
                    }
                }
            }
        }

        return recipeConstructor.create(new RecipeData(energy, inputItems, inputFluids, outputItems, outputFluids));
    }

    @Nullable
    @Override
    public T fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {

        var energy = buffer.readVarInt();
        
        var inputItemCount = buffer.readVarInt();
        var inputItems = new ArrayList<Ingredient>(inputItemCount);
        for (int i = 0; i < inputItemCount; ++i) {
            inputItems.add(Ingredient.fromNetwork(buffer));
        }

        var inputFluidCount = buffer.readVarInt();
        var inputFluids = new ArrayList<FluidIngredient>(inputFluidCount);
        for (int i = 0; i < inputFluidCount; ++i) {
            inputFluids.add(FluidIngredient.fromNetwork(buffer));
        }

        var outputItemCount = buffer.readVarInt();
        var outputItems = new ArrayList<ItemStack>(outputItemCount);
        for (int i = 0; i < outputItemCount; ++i) {
            outputItems.add(buffer.readItem());
        }

        var outputFluidCount = buffer.readVarInt();
        var outputFluids = new ArrayList<FluidStack>(outputFluidCount);
        for (int i = 0; i < outputFluidCount; ++i) {
            outputFluids.add(buffer.readFluidStack());
        }
        
        return recipeConstructor.create(new RecipeData(energy, inputItems, inputFluids, outputItems, outputFluids));
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer, T recipe) {
        buffer.writeVarInt(recipe.energy);
        
        buffer.writeVarInt(recipe.inputItems.size());
        for (var item : recipe.inputItems) {
            item.toNetwork(buffer);
        }

        buffer.writeVarInt(recipe.inputFluids.size());
        for (var fluid : recipe.inputFluids) {
            fluid.toNetwork(buffer);
        }

        buffer.writeVarInt(recipe.outputItems.size());
        for (var item : recipe.outputItems) {
            buffer.writeItem(item);
        }

        buffer.writeVarInt(recipe.outputFluids.size());
        for (var fluid : recipe.outputFluids) {
            buffer.writeFluidStack(fluid);
        }
    }
}
