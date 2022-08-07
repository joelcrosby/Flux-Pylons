package com.joelcrosby.fluxpylons.machine.common;

import com.joelcrosby.fluxpylons.item.WrenchItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;

public abstract class MachineBlock extends BaseEntityBlock {
    public MachineBlock(Block.Properties props) {
        super(props);
        
        var state = this.defaultBlockState()
                .setValue(BlockStateProperties.LIT, false)
                .setValue(BlockStateProperties.FACING, Direction.NORTH);

        this.registerDefaultState(state);
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
        var itemStack = player.getItemInHand(player.getUsedItemHand());
        var entity = level.getBlockEntity(pos);
        
        if (!player.isCrouching() && itemStack.getItem() instanceof WrenchItem) {
            return InteractionResult.FAIL;
        }

        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer && entity instanceof MachineBlockEntity) {
            NetworkHooks.openGui(serverPlayer, (MenuProvider) entity, entity.getBlockPos());
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        return InteractionResult.SUCCESS;
    }
    
    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }
    
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
        return this.defaultBlockState().setValue(BlockStateProperties.FACING, blockPlaceContext.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.LIT);
        builder.add(BlockStateProperties.FACING);
    }

    @Nullable
    protected static <T extends BlockEntity> BlockEntityTicker<T> createTicker(Level level, BlockEntityType<T> passedBlockEntity, BlockEntityType<? extends MachineBlockEntity> entityType) {
        return level.isClientSide ? null : createTickerHelper(passedBlockEntity, entityType, MachineBlockEntity::serverTick);
    }
}
