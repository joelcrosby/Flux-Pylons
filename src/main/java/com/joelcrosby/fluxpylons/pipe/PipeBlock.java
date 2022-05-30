package com.joelcrosby.fluxpylons.pipe;

import com.google.common.collect.ImmutableMap;
import com.joelcrosby.fluxpylons.setup.Common;
import com.joelcrosby.fluxpylons.Utility;
import com.joelcrosby.fluxpylons.network.NetworkManager;
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
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.energy.CapabilityEnergy;
import org.apache.commons.lang3.mutable.MutableObject;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class PipeBlock extends BaseEntityBlock {

    public static final Map<Direction, EnumProperty<ConnectionType>> DIRECTIONS = new HashMap<>();
    private static final Map<BlockState, VoxelShape> SHAPE_CACHE = new HashMap<>();
    private static final Map<BlockState, VoxelShape> COLL_SHAPE_CACHE = new HashMap<>();
    
    private static final VoxelShape CENTER_SHAPE = box(5, 5, 5, 11, 11, 11);
    
    public static final Map<Direction, VoxelShape> DIR_SHAPES = ImmutableMap.<Direction, VoxelShape>builder()
            .put(Direction.UP, box(5, 10, 5, 11, 16, 11))
            .put(Direction.DOWN, box(5, 0, 5, 11, 6, 11))
            .put(Direction.NORTH, box(5, 5, 0, 11, 11, 6))
            .put(Direction.SOUTH, box(5, 5, 10, 11, 11, 16))
            .put(Direction.EAST, box(10, 5, 5, 16, 11, 11))
            .put(Direction.WEST, box(0, 5, 5, 6, 11, 11))
            .build();

    static {
        for (var dir : Direction.values())
            DIRECTIONS.put(dir, EnumProperty.create(dir.getName(), ConnectionType.class));
    }

    private final PipeType pipeType;

    public PipeBlock(PipeType pipeType) {
        super(Block.Properties.of(Material.STONE).strength(2).sound(SoundType.STONE).noOcclusion());
        
        this.pipeType = pipeType;

        var state = this.defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, false);
        
        for (var prop : DIRECTIONS.values()) {
            state = state.setValue(prop, ConnectionType.DISCONNECTED);
        }
            
        this.registerDefaultState(state);
    }
    
    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PipeBlockEntity(pos, state, pipeType);
    }

    @Override
    @SuppressWarnings("deprecation")
    public FluidState getFluidState(BlockState state) {
        return state.getValue(BlockStateProperties.WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        updateState(state, level, pos);

        if (!level.isClientSide) {
            var pipe = NetworkManager.get(level).getNode(pos);

            if (pipe != null && pipe.getNetwork() != null) {
                pipe.getNetwork().scanGraph(level, pos);
            }
        }
    }

    public void updateState(BlockState state, Level level, BlockPos pos) {
        var newState = this.createState(level, pos, state);
        if (newState != state) {
            level.setBlockAndUpdate(pos, newState);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(DIRECTIONS.values().toArray(new EnumProperty[0]));
        builder.add(BlockStateProperties.WATERLOGGED);
    }
    
    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.createState(context.getLevel(), context.getClickedPos(), this.defaultBlockState());
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos) {
        if (stateIn.getValue(BlockStateProperties.WATERLOGGED))
            worldIn.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(worldIn));
        return createState(worldIn, currentPos, stateIn);
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return this.cacheAndGetShape(state, SHAPE_CACHE, null);
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getCollisionShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return this.cacheAndGetShape(state, COLL_SHAPE_CACHE, s -> {
            // make the shape a bit higher to allow player to jump up onto a higher block
            var newShape = new MutableObject<VoxelShape>(Shapes.empty());
            s.forAllBoxes((x1, y1, z1, x2, y2, z2) -> newShape.setValue(Shapes.join(Shapes.create(x1, y1, z1, x2, y2 + 3 / 16F, z2), newShape.getValue(), BooleanOp.OR)));
            return newShape.getValue().optimize();
        });
    }
    
    private VoxelShape cacheAndGetShape(BlockState state, Map<BlockState, VoxelShape> cache, Function<VoxelShape, VoxelShape> shapeModifier) {
        var shape = cache.get(state);
        
        if (shape == null) {
            shape = CENTER_SHAPE;
            for (var entry : DIRECTIONS.entrySet()) {
                if (state.getValue(entry.getValue()).isConnected())
                    shape = Shapes.or(shape, DIR_SHAPES.get(entry.getKey()));
            }
            if (shapeModifier != null)
                shape = shapeModifier.apply(shape);
            cache.put(state, shape);
        }
        
        return shape;
    }

    private BlockState createState(LevelAccessor world, BlockPos pos, BlockState curr) {
        var state = this.defaultBlockState();
        var fluid = world.getFluidState(pos);
        
        if (fluid.is(FluidTags.WATER) && fluid.getAmount() == 8) {
            state = state.setValue(BlockStateProperties.WATERLOGGED, true);
        }

        for (var dir : Direction.values()) {
            var prop = DIRECTIONS.get(dir);
            var type = this.getConnectionType(world, pos, dir);
            // don't reconnect on blocked faces
            if (type.isConnected() && curr.getValue(prop) == ConnectionType.BLOCKED) {
                type = ConnectionType.BLOCKED;
            }
                
            state = state.setValue(prop, type);
        }
        
        return state;
    }

    public ConnectionType getConnectionType(LevelAccessor world, BlockPos pos, Direction direction) {
        var offset = pos.relative(direction);
        
        // TODO: Determine if below is needed
        
//        if (!world.isLoaded(offset))
//            return ConnectionType.DISCONNECTED;
        
        var opposite = direction.getOpposite();
        var tile = world.getBlockEntity(offset);
        
        if (tile != null) {
            // TODO: Implement a cleaner way of ensuring mismatched types don't connect
            if (tile instanceof PipeBlockEntity) {
                if (((PipeBlockEntity) tile).getPipeType() != pipeType) {
                    return ConnectionType.DISCONNECTED;
                }
            }
            
            var connectable = tile.getCapability(Common.pipeConnectableCapability, opposite).orElse(null);
            if (connectable != null) {
                return connectable.getConnectionType(pos, direction);
            }

            var energyHandler = tile.getCapability(CapabilityEnergy.ENERGY, opposite).orElse(null);
            if (energyHandler != null) {
                return ConnectionType.END;
            }
        }
        
        var blockHandler = Utility.getBlockItemHandler(world, offset, opposite);
        if (blockHandler != null) {
            return ConnectionType.END;
        }
            
        
        return ConnectionType.DISCONNECTED;
    }
}
