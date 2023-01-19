package com.joelcrosby.fluxpylons.pipe;

import com.google.common.collect.ImmutableMap;
import com.joelcrosby.fluxpylons.FluxPylons;
import com.joelcrosby.fluxpylons.Utility;
import com.joelcrosby.fluxpylons.item.WrenchItem;
import com.joelcrosby.fluxpylons.item.upgrade.UpgradeItem;
import com.joelcrosby.fluxpylons.pipe.network.NetworkManager;
import com.joelcrosby.fluxpylons.setup.Common;
import com.joelcrosby.fluxpylons.util.Raytracer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
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
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.mutable.MutableObject;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class PipeBlock extends BaseEntityBlock {

    public static final Map<Direction, EnumProperty<ConnectionType>> DIRECTIONS = new HashMap<>();
    
    private static final Map<Pair<BlockState, BlockState>, VoxelShape> SHAPE_CACHE = new HashMap<>();
    private static final Map<Pair<BlockState, BlockState>, VoxelShape> COLL_SHAPE_CACHE = new HashMap<>();
    
    private static final VoxelShape CENTER_SHAPE = box(5, 5, 5, 11, 11, 11);
    
    public static final Map<Direction, VoxelShape> DIR_SHAPES = ImmutableMap.<Direction, VoxelShape>builder()
            .put(Direction.UP, box(5, 10, 5, 11, 16, 11))
            .put(Direction.DOWN, box(5, 0, 5, 11, 6, 11))
            .put(Direction.NORTH, box(5, 5, 0, 11, 11, 6))
            .put(Direction.SOUTH, box(5, 5, 10, 11, 11, 16))
            .put(Direction.EAST, box(10, 5, 5, 16, 11, 11))
            .put(Direction.WEST, box(0, 5, 5, 6, 11, 11))
            .build();

    public static final Map<Direction, VoxelShape> DIR_SHAPES_END = ImmutableMap.<Direction, VoxelShape>builder()
            .put(Direction.UP, box(4, 11, 4, 12, 16, 12))
            .put(Direction.DOWN, box(4, 0, 4, 12, 5, 12))
            .put(Direction.NORTH, box(4, 4, 0, 12, 12, 5))
            .put(Direction.SOUTH, box(4, 4, 11, 12, 12, 16))
            .put(Direction.EAST, box(11, 4, 4, 16, 12, 12))
            .put(Direction.WEST, box(0, 4, 4, 5, 12, 12))
            .build();

    public static final Map<Direction, VoxelShape> DIR_SHAPES_END_ADV = ImmutableMap.<Direction, VoxelShape>builder()
            .put(Direction.UP, Shapes.join(box(4, 13, 4, 12, 16, 12), box(6, 11, 6, 10, 13, 10), BooleanOp.OR))
            .put(Direction.DOWN, Shapes.join(box(4, 0, 4, 12, 3, 12), box(6, 3, 6, 10, 5, 10), BooleanOp.OR))
            .put(Direction.NORTH, Shapes.join(box(4, 4, 0, 12, 12, 3), box(6, 6, 3, 10, 10, 5), BooleanOp.OR))
            .put(Direction.SOUTH, Shapes.join(box(4, 4, 13, 12, 12, 16), box(6, 6, 11, 10, 10, 13), BooleanOp.OR))
            .put(Direction.EAST, Shapes.join(box(13, 4, 4, 16, 12, 12), box(11, 6, 6, 13, 10, 10), BooleanOp.OR))
            .put(Direction.WEST, Shapes.join(box(0, 4, 4, 3, 12, 12), box(3, 6, 6, 5, 10, 10), BooleanOp.OR))
            .build();

    static {
        for (var dir : Direction.values())
            DIRECTIONS.put(dir, EnumProperty.create(dir.getName(), ConnectionType.class));
    }

    private final PipeType pipeType;

    public PipeBlock(PipeType pipeType) {
        super(Block.Properties.of(Material.METAL, MaterialColor.METAL).strength(1.6f).sound(SoundType.COPPER));
        
        this.pipeType = pipeType;

        var state = this.defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, false);
        
        for (var prop : DIRECTIONS.values()) {
            state = state.setValue(prop, ConnectionType.DISCONNECTED);
        }
            
        this.registerDefaultState(state);
    }
    
    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
        var dir = getPipeEndDirectionClicked(pos, result.getLocation());
        var entity = level.getBlockEntity(pos);

        if (dir == null || entity == null || !(state.getBlock() instanceof PipeBlock)) {
            return InteractionResult.FAIL;
        }
        
        var connectionType =  state.getValue(DIRECTIONS.get(dir));

        if (!connectionType.isEnd()) {
            return InteractionResult.FAIL;
        }
        
        if (!(entity instanceof PipeBlockEntity pipeBlockEntity)) {
            return InteractionResult.FAIL;
        }

        var itemStack = player.getItemInHand(player.getUsedItemHand());
        
        if (!player.isCrouching() && itemStack.getItem() instanceof UpgradeItem) {
            if (!level.isClientSide) {
                if (pipeBlockEntity.getUpgradeManager(dir).insertUpgrade(itemStack.copy())) {
                    itemStack.shrink(1);
                }
            }

            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        
        if (!player.isCrouching() && itemStack.getItem() instanceof WrenchItem) {
            return InteractionResult.FAIL;
        }
        
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            pipeBlockEntity.getUpgradeManager(dir).OpenContainerMenu(serverPlayer);
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }

    @Override
    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        var tile = Utility.getBlockEntity(PipeBlockEntity.class, level, pos);
        if (tile != null && level instanceof ServerLevel) {
            for (var dir : Direction.values()) {
                tile.getUpgradeManager(dir).dropContents(level, pos);
            }
            
            if (tile.cover != null)
                tile.removeCover(player, InteractionHand.MAIN_HAND);
        }
        
        super.playerWillDestroy(level, pos, state, player);
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
        return this.cacheAndGetShape(state, worldIn, pos, s -> s.getShape(worldIn, pos, context), SHAPE_CACHE, null);
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getCollisionShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return this.cacheAndGetShape(state, worldIn, pos,  s -> s.getCollisionShape(worldIn, pos, context), COLL_SHAPE_CACHE, s -> {
            // make the shape a bit higher to allow player to jump up onto a higher block
            var newShape = new MutableObject<VoxelShape>(Shapes.empty());
            s.forAllBoxes((x1, y1, z1, x2, y2, z2) -> newShape.setValue(Shapes.join(Shapes.create(x1, y1, z1, x2, y2 + 3 / 16F, z2), newShape.getValue(), BooleanOp.OR)));
            return newShape.getValue().optimize();
        });
    }
    
    private VoxelShape cacheAndGetShape(BlockState state,
                                        BlockGetter worldIn,
                                        BlockPos pos,
                                        Function<BlockState, VoxelShape> coverShapeSelector,
                                        Map<Pair<BlockState, BlockState>, VoxelShape> cache,
                                        Function<VoxelShape, VoxelShape> shapeModifier) {
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
        var shape = cache.get(key);
        
        if (shape == null) {
            shape = CENTER_SHAPE;
            
            for (var entry : DIRECTIONS.entrySet()) {
                var connectionType = state.getValue(entry.getValue());
                
                if (connectionType.isEnd()) {
                    if (pipeType == PipeType.BASIC) {
                        shape = Shapes.or(shape, DIR_SHAPES_END.get(entry.getKey()));
                    } else {
                        shape = Shapes.or(shape, DIR_SHAPES_END_ADV.get(entry.getKey()));
                    }
                } else if (connectionType.isConnected()) {
                    shape = Shapes.or(shape, DIR_SHAPES.get(entry.getKey()));
                }
            }
            
            if (shapeModifier != null) {
                shape = shapeModifier.apply(shape);
            }

            if (coverShape != null) {
                shape = Shapes.or(shape, coverShape);
            }
                
            cache.put(key, shape);
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

    @SuppressWarnings("CommentedOutCode")
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

            var itemHandler = tile.getCapability(ForgeCapabilities.ITEM_HANDLER, opposite).orElse(null);
            if (itemHandler != null) {
                return ConnectionType.END;
            }

            var energyHandler = tile.getCapability(ForgeCapabilities.ENERGY, opposite).orElse(null);
            if (energyHandler != null) {
                return ConnectionType.END;
            }

            var fluidHandler = tile.getCapability(ForgeCapabilities.FLUID_HANDLER, opposite).orElse(null);
            if (fluidHandler != null) {
                return ConnectionType.END;
            }
        }
        
        var blockHandler = Utility.getBlockItemHandler(world, offset, opposite);
        if (blockHandler != null) {
            return ConnectionType.END;
        }
            
        
        return ConnectionType.DISCONNECTED;
    }

    @Nullable
    public Direction getPipeEndDirectionClicked(BlockPos pos, Vec3 hit) {
        for (var dir : Direction.values()) {
            if (Raytracer.inclusiveContains(DIR_SHAPES_END.get(dir).bounds().move(pos), hit)) {
                return dir;
            }
        }
        
        return null;
    }
    
    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        var energyRate = this.pipeType.getNodeType().getEnergyTransferRate();
        var fluidRate = this.pipeType.getNodeType().getFluidTransferRate();
        var itemRate = this.pipeType.getNodeType().getItemTransferRate();

        var formatter = new DecimalFormat("#,###");
        
        var energyRateText = formatter.format(energyRate) + " FE/t";
        var fluidRateText = fluidRate + " MB/t";
        var itemRateText = itemRate + " /0.5s";
        
        var energyText = I18n.get("terms." + FluxPylons.ID + ".energy").concat(" ");
        var fluidText = I18n.get("terms." + FluxPylons.ID + ".fluids").concat(" ");
        var itemText = I18n.get("terms." + FluxPylons.ID + ".items").concat(" ");
        
        var energyComponent = Component.translatable(energyText).setStyle(Style.EMPTY.applyFormat(ChatFormatting.DARK_PURPLE))
                .append(Component.translatable(energyRateText).setStyle(Style.EMPTY.applyFormat(ChatFormatting.GRAY)));
        var fluidComponent = Component.translatable(fluidText).setStyle(Style.EMPTY.applyFormat(ChatFormatting.DARK_PURPLE))
                .append(Component.translatable(fluidRateText).setStyle(Style.EMPTY.applyFormat(ChatFormatting.GRAY)));
        var itemComponent = Component.translatable(itemText).setStyle(Style.EMPTY.applyFormat(ChatFormatting.DARK_PURPLE))
                .append(Component.translatable(itemRateText).setStyle(Style.EMPTY.applyFormat(ChatFormatting.GRAY)));
        
        List<Component> components = Arrays.asList(energyComponent, fluidComponent, itemComponent);
        
        Utility.addTooltip(ForgeRegistries.BLOCKS.getKey(this).getPath(), components, tooltip);
    }
}
