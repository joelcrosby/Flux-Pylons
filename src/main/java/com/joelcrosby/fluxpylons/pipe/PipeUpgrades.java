package com.joelcrosby.fluxpylons.pipe;

import net.minecraft.world.item.ItemStack;

import java.util.HashSet;
import java.util.List;

public record PipeUpgrades(
        List<ItemStack> extractItems,
        List<ItemStack> extractFluids,
        List<ItemStack> filterItems,
        HashSet<String> filterItemRegistryNames,
        List<ItemStack> filterFluids,
        HashSet<String> filterFluidRegistryNames
) {
}
