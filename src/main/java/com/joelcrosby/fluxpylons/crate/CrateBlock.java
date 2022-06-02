package com.joelcrosby.fluxpylons.crate;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;

public class CrateBlock extends BaseEntityBlock {

    public CrateBlock() {
        super(Block.Properties.of(Material.METAL, MaterialColor.METAL).strength(1.0F));
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }
    
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CrateBlockEntity(pos, state);
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
            
        var entity = level.getBlockEntity(pos);

        if (entity == null)
            return InteractionResult.PASS;

        if (entity instanceof CrateBlockEntity) {
            level.playSound(null, pos, SoundEvents.BARREL_OPEN, SoundSource.PLAYERS, 1, 1);
            
            NetworkHooks.openGui((ServerPlayer) player, (MenuProvider) entity, entity.getBlockPos());
        } 
        
        return InteractionResult.SUCCESS;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            var entity = world.getBlockEntity(pos);
            
            if (entity instanceof CrateBlockEntity) {
                Containers.dropContents(world, pos, (Container)entity);
                world.updateNeighbourForOutputSignal(pos, this);
            }

            super.onRemove(state, world, pos, newState, isMoving);
        }
    }
}
