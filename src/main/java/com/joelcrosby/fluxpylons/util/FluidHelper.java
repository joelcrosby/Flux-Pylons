package com.joelcrosby.fluxpylons.util;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import org.apache.commons.lang3.tuple.Pair;

public class FluidHelper {
    public static boolean isFluidHandler(ItemStack stack) {
        return !getFromStack(stack, true).getKey().isEmpty();
    }
    
    public static Pair<ItemStack, FluidStack> getFromStack(ItemStack stack, boolean simulate) {
        if (stack.isEmpty()) {
            return Pair.of(ItemStack.EMPTY, FluidStack.EMPTY);
        }

        if (stack.getCount() > 1) {
            stack = ItemHandlerHelper.copyStackWithSize(stack, 1);
        }

        var handler = stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM, null).orElse(null);
        if (handler == null) {
            return Pair.of(ItemStack.EMPTY, FluidStack.EMPTY);
        }

        var result = handler.drain(FluidAttributes.BUCKET_VOLUME, simulate ? IFluidHandler.FluidAction.SIMULATE : IFluidHandler.FluidAction.EXECUTE);

        return Pair.of(handler.getContainer(), result);
    }
}
