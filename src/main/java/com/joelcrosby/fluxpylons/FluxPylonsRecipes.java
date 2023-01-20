package com.joelcrosby.fluxpylons;

import com.joelcrosby.fluxpylons.pipe.network.NetworkManager;
import com.joelcrosby.fluxpylons.recipe.SmelterRecipe;
import com.joelcrosby.fluxpylons.recipe.WasherRecipe;
import com.joelcrosby.fluxpylons.recipe.common.FluxPylonsRecipeType;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Supplier;

public class FluxPylonsRecipes {
    private static final Logger LOGGER = LogManager.getLogger(NetworkManager.class);

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, FluxPylons.ID);
    
    public static final ResourceLocation RESOURCE_SMELTING = new ResourceLocation(FluxPylons.ID, "smelting");
    public static final ResourceLocation RESOURCE_WASHING = new ResourceLocation(FluxPylons.ID, "washing");
    
    
    public static final class FluxPylonsRecipeTypes {
        public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES_REGISTRY = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, FluxPylons.ID);

        public static final RegistryObject<RecipeType<SmelterRecipe>> SMELTING = RECIPE_TYPES_REGISTRY.register("smelting",  () -> new FluxPylonsRecipeType<>(RESOURCE_SMELTING));
        public static final RegistryObject<RecipeType<WasherRecipe>> WASHING = RECIPE_TYPES_REGISTRY.register("washing",  () -> new FluxPylonsRecipeType<>(RESOURCE_WASHING));
    }

    public static final RegistryObject<RecipeSerializer<?>> SMELTING = registerSerializer(RESOURCE_SMELTING, () -> SmelterRecipe.SERIALIZER);
    public static final RegistryObject<RecipeSerializer<?>> WASHING = registerSerializer(RESOURCE_WASHING, () -> WasherRecipe.SERIALIZER);

    private static RegistryObject<RecipeSerializer<?>> registerSerializer(ResourceLocation name, Supplier<RecipeSerializer<?>> serializer) {
        LOGGER.info("Registering Serializer for Recipe: " + name.toString());
        return RECIPE_SERIALIZERS.register(name.getPath(), serializer);
    }
    
    private static <T extends Recipe<?>> RecipeType<T> registerType(ResourceLocation name) {
        LOGGER.info("Registering Recipe Type: " + name.toString());

        return Registry.register(Registry.RECIPE_TYPE, name, new RecipeType<T>() {
            @Override
            public String toString() {
                return name.toString();
            }
        });
    }
}
