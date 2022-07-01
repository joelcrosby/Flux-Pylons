package com.joelcrosby.fluxpylons.pylon;

import com.google.common.collect.ImmutableMap;
import com.joelcrosby.fluxpylons.Utility;
import com.joelcrosby.fluxpylons.pipe.PipeBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class PylonBlock extends BaseEntityBlock {

    private static final Map<Pair<BlockState, BlockState>, VoxelShape> SHAPE_CACHE = new HashMap<>();

    public static final Map<Direction, VoxelShape> DIR_SHAPES = ImmutableMap.<Direction, VoxelShape>builder()
            .put(Direction.UP, box(5, 5, 5, 11, 16, 11))
            .put(Direction.DOWN, box(5, 0, 5, 11, 11, 11))
            .put(Direction.NORTH, box(5, 5, 0, 11, 11, 11))
            .put(Direction.SOUTH, box(5, 5, 5, 11, 11, 16))
            .put(Direction.EAST, box(5, 5, 5, 16, 11, 11))
            .put(Direction.WEST, box(0, 5, 5, 11, 11, 11))
            .build();
    
    public PylonBlock() {
        super(Block.Properties.of(Material.METAL, MaterialColor.METAL).sound(SoundType.NETHERITE_BLOCK).strength(1.2f));

        var state = this.defaultBlockState()
                .setValue(BlockStateProperties.WATERLOGGED, false)
                .setValue(BlockStateProperties.FACING, Direction.DOWN);
        
        this.registerDefaultState(state);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PylonBlockEntity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    @SuppressWarnings("deprecation")
    public FluidState getFluidState(BlockState state) {
        return state.getValue(BlockStateProperties.WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.FACING);
        builder.add(BlockStateProperties.WATERLOGGED);
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return this.cacheAndGetShape(state, worldIn, pos, s -> s.getShape(worldIn, pos, context));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(BlockStateProperties.FACING, context.getClickedFace().getOpposite());
    }
    
    private VoxelShape cacheAndGetShape(BlockState state,
                                        BlockGetter worldIn,
                                        BlockPos pos,
                                        Function<BlockState, VoxelShape> coverShapeSelector) {
        VoxelShape coverShape = null;
        BlockState cover = null;

        var tile = Utility.getBlockEntity(PipeBlockEntity.class, worldIn, pos);
        if (tile != null && tile.cover != null) {
            cover = tile.cover;
            // try catch since the block might expect to find itself at the position
            try {
                coverShape = coverShapeSelector.apply(cover);
            } catch (Exception ignored) {
            }
        }

        var key = Pair.of(state, cover);
        var shape = PylonBlock.SHAPE_CACHE.get(key);

        if (shape == null) {
            shape = DIR_SHAPES.get(state.getValue(BlockStateProperties.FACING));
            
            if (coverShape != null) {
                shape = Shapes.or(shape, coverShape);
            }

            PylonBlock.SHAPE_CACHE.put(key, shape);
        }

        return shape;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter getter, BlockPos pos) {
        return true;
    }
}
