package com.joelcrosby.fluxpylons.item;

import com.joelcrosby.fluxpylons.setup.Common;
import net.minecraft.world.item.Item;

public class IngotItem extends Item {
    public IngotItem() {
        super(new Item.Properties().stacksTo(64).tab(Common.TAB));
    }
}
