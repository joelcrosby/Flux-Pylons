package com.joelcrosby.fluxpylons.pipe;

import net.minecraft.world.item.ItemStack;

import java.util.HashSet;
import java.util.List;

public record PipeUpgrades(
        List<ItemStack> extractItems,
        List<ItemStack> extractFluids,
        List<ItemStack> filters,
        HashSet<String> filterItemRegistryNames,
        List<ItemStack> fluidFilters,
        HashSet<String> filterFluidRegistryNames
) {
}
