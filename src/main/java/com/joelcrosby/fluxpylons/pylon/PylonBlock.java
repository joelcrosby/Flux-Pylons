package com.joelcrosby.fluxpylons.pylon;

import com.google.common.collect.ImmutableMap;
import com.joelcrosby.fluxpylons.pylon.network.PylonNetworkManager;
import com.joelcrosby.fluxpylons.pylon.network.graph.PylonGraphNodeType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
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
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Map;

public class PylonBlock extends BaseEntityBlock {
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
        return new PylonBlockEntity(pos, state, PylonGraphNodeType.BASIC);
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
    @SuppressWarnings("deprecation")
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        if (!level.isClientSide) {
            var manager = PylonNetworkManager.get(level);
            
            if (manager.getNode(fromPos) != null) {
                return;
            }
            
            var pylon = manager.getNode(pos);

            if (pylon != null && pylon.getNetwork() != null) {
                pylon.getNetwork().scanGraph(level, pos);
            }
        }
    }
    
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.FACING);
        builder.add(BlockStateProperties.WATERLOGGED);
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return DIR_SHAPES.get(state.getOptionalValue(BlockStateProperties.FACING).orElse(Direction.DOWN));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return createState(context.getLevel(), context.getClickedPos(), this.defaultBlockState())
                .setValue(BlockStateProperties.FACING, context.getClickedFace().getOpposite());
    }

    private BlockState createState(LevelAccessor world, BlockPos pos, BlockState state) {
        var fluid = world.getFluidState(pos);

        if (fluid.is(FluidTags.WATER) && fluid.getAmount() == 8) {
            state = state.setValue(BlockStateProperties.WATERLOGGED, true);
        }

        return state;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter getter, BlockPos pos) {
        return true;
    }
}
