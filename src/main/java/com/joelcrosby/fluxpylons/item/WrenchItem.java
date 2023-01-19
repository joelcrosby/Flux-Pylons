package com.joelcrosby.fluxpylons.item;

import com.joelcrosby.fluxpylons.Utility;
import com.joelcrosby.fluxpylons.crate.CrateBlock;
import com.joelcrosby.fluxpylons.crate.CrateBlockEntity;
import com.joelcrosby.fluxpylons.pipe.ConnectionType;
import com.joelcrosby.fluxpylons.pipe.PipeBlock;
import com.joelcrosby.fluxpylons.pipe.PipeBlockEntity;
import com.joelcrosby.fluxpylons.pipe.network.NetworkManager;
import com.joelcrosby.fluxpylons.setup.Common;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class WrenchItem extends Item {

    public WrenchItem() {
        super(new Item.Properties().stacksTo(1).tab(Common.TAB));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        var level = context.getLevel();
        var pos = context.getClickedPos();
        var player = context.getPlayer();
        var state = level.getBlockState(pos);
        var block = state.getBlock();

        if (block instanceof PipeBlock) {
            var tile = Utility.getBlockEntity(PipeBlockEntity.class, level, pos);
            if (tile == null)
                return InteractionResult.FAIL;
            
            if (player.isCrouching()) {
                if (!level.isClientSide) {
                    if (tile.cover != null) {
                        tile.removeCover(player, context.getHand());
                        Utility.sendBlockEntityToClients(tile);
                        level.playSound(null, pos, SoundEvents.ITEM_FRAME_ROTATE_ITEM, SoundSource.PLAYERS, 1, 1);
                    } else {
                        Block.dropResources(state, level, pos, tile, null, ItemStack.EMPTY);
                        level.removeBlock(pos, false);
                        level.playSound(null, pos, SoundEvents.COPPER_BREAK, SoundSource.PLAYERS, 1, 1);
                    }
                }
                
                return InteractionResult.sidedSuccess(level.isClientSide);
            } 
            
            if (tile.cover == null) {
                var offhand = player.getOffhandItem();
                if (offhand.getItem() instanceof BlockItem) {
                    if (!level.isClientSide) {
                        var blockContext = new BlockPlaceContext(context);
                        var coverBlock = ((BlockItem) offhand.getItem()).getBlock();
                        var cover = coverBlock.getStateForPlacement(blockContext);
                        if (cover != null && !(coverBlock instanceof EntityBlock)) {
                            tile.cover = cover;
                            Utility.sendBlockEntityToClients(tile);
                            offhand.shrink(1);
                            level.playSound(null, pos, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.PLAYERS, 1, 1);
                        }
                    }
                    
                    return InteractionResult.sidedSuccess(level.isClientSide);
                }
            }

            for (var entry : PipeBlock.DIR_SHAPES.entrySet()) {
                InteractionResult isClientSide = getInteractionResult(context, level, pos, state, (PipeBlock) block, entry);
                if (isClientSide != null) return isClientSide;
            }

            for (var entry : PipeBlock.DIR_SHAPES_END.entrySet()) {
                InteractionResult isClientSide = getInteractionResult(context, level, pos, state, (PipeBlock) block, entry);
                if (isClientSide != null) return isClientSide;
            }
        }

        if (block instanceof CrateBlock) {
            if (player.isCrouching()) {
                if (!level.isClientSide) {
                    var tile = Utility.getBlockEntity(CrateBlockEntity.class, level, pos);
                    if (tile == null)
                        return InteractionResult.FAIL;

                    Block.dropResources(state, level, pos, tile, null, ItemStack.EMPTY);
                    level.removeBlock(pos, false);
                    level.playSound(null, pos, SoundEvents.COPPER_BREAK, SoundSource.PLAYERS, 1, 1);
                    level.updateNeighborsAt(pos, state.getBlock());
                }

                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        }
        
        return InteractionResult.PASS;
    }

    @Nullable
    private InteractionResult getInteractionResult(UseOnContext context, Level level, BlockPos pos, BlockState state, PipeBlock block, Map.Entry<Direction, VoxelShape> entry) {
        var direction = entry.getKey();
        var box = entry.getValue().bounds().move(pos).inflate(0.001F);
        if (!box.contains(context.getClickLocation()))
            return null;

        var prop = PipeBlock.DIRECTIONS.get(direction);
        var curr = state.getValue(prop);

        if (level.isClientSide) {
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        var newType = getOppositeType(level, pos, block, direction, curr);

        var otherPos = pos.relative(direction);
        var otherState = level.getBlockState(otherPos);
        var splitNetworks = false;

        var newState = state.setValue(prop, newType);

        level.setBlockAndUpdate(pos, newState);
        level.playSound(null, pos, SoundEvents.ITEM_FRAME_ROTATE_ITEM, SoundSource.PLAYERS, 1, 1);

        if (otherState.getBlock() instanceof PipeBlock) {
            otherState = otherState.setValue(PipeBlock.DIRECTIONS.get(direction.getOpposite()), newType);
            
            if (newType == ConnectionType.BLOCKED) {
                splitNetworks = true;
            }
            
            level.setBlockAndUpdate(otherPos, otherState);
        
            var networkManager = NetworkManager.get(level);
            var otherNode = networkManager.getNode(otherPos);
            var node = networkManager.getNode(pos);
            
            if (otherNode.getNodeType() != node.getNodeType()) {
                return InteractionResult.FAIL;
            }
            
            if (splitNetworks && otherNode != null && otherNode.getNetwork() != null) {
                networkManager.splitNetworks(otherNode, false);
            } else {
                networkManager.mergeNetworksIntoOne(Set.of(otherNode, node), level, pos);
            }
        }

        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    private ConnectionType getOppositeType(Level world, BlockPos pos, PipeBlock block, Direction direction, ConnectionType curr) {
        if (curr == ConnectionType.BLOCKED) {
            var conType = block.getConnectionType(world, pos, direction);
            if (conType == ConnectionType.BLOCKED) {
                return ConnectionType.CONNECTED;
            }
            
            return conType;
        }
        
        return ConnectionType.BLOCKED;
    }

    @Override
    public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        Utility.addTooltip(ForgeRegistries.BLOCKS.getKey(this).getPath(), tooltip);
    }

    @Override
    public Rarity getRarity(ItemStack itemStack) {
        return Rarity.UNCOMMON;
    }
}
