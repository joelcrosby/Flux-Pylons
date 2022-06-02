package com.joelcrosby.fluxpylons.item;

import com.joelcrosby.fluxpylons.network.graph.GraphNode;
import com.joelcrosby.fluxpylons.pipe.PipeBlock;
import com.joelcrosby.fluxpylons.setup.Common;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;


public abstract class UpgradeItem extends Item {
    public UpgradeItem() {
        super(new Item.Properties().stacksTo(16).tab(Common.TAB));
    }

    public abstract void update(GraphNode node, Direction dir);
    
    @Override
    public InteractionResult useOn(UseOnContext context) {
        var level = context.getLevel();
        var pos = context.getClickedPos();
        var player = context.getPlayer();
        var state = level.getBlockState(pos);
        var block = state.getBlock();

        if (!(block instanceof PipeBlock)) {
            return InteractionResult.FAIL;
        }

        if (!player.isCrouching()) {
            return InteractionResult.FAIL;
        }

        return InteractionResult.PASS;
    }
}
