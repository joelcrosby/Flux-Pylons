package com.joelcrosby.fluxpylons.pipe;

import net.minecraft.world.item.ItemStack;

import java.util.HashSet;
import java.util.List;

public record PipeUpgrades(List<ItemStack> items, List<ItemStack> fluids, List<ItemStack> filters, HashSet<String> filterItemRegistryNames) {
}
