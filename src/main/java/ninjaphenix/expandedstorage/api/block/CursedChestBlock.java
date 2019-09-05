package ninjaphenix.expandedstorage.api.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Waterloggable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EntityContext;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import ninjaphenix.expandedstorage.api.block.entity.CursedChestBlockEntity;

@SuppressWarnings("deprecation")
public class CursedChestBlock extends AbstractChestBlock implements Waterloggable
{
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    private static final VoxelShape SINGLE_SHAPE = Block.createCuboidShape(1, 0, 1, 15, 14, 15);
    private static final VoxelShape TOP_SHAPE = Block.createCuboidShape(1, -16, 1, 15, 14, 15);
    private static final VoxelShape BOTTOM_SHAPE = Block.createCuboidShape(1, 0, 1, 15, 30, 15);
    private static final VoxelShape A = Block.createCuboidShape(1, 0, 1, 31, 14, 15);
    private static final VoxelShape B = Block.createCuboidShape(1 - 16, 0, 1, 15, 14, 15);
    private static final VoxelShape C = Block.createCuboidShape(1, 0, 1 - 16, 15, 14, 15);
    private static final VoxelShape D = Block.createCuboidShape(1, 0, 1, 15, 14, 31);

    public CursedChestBlock(Settings settings)
    {
        super(settings);
        setDefaultState(getDefaultState().with(WATERLOGGED, false));
    }

    @Override
    public BlockEntity createBlockEntity(BlockView var1) { return new CursedChestBlockEntity(Registry.BLOCK.getId(this)); }

    @Override
    public FluidState getFluidState(BlockState state) { return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state); }

    @Override
    protected void appendProperties(StateFactory.Builder<Block, BlockState> stateBuilder)
    {
        super.appendProperties(stateBuilder);
        stateBuilder.add(WATERLOGGED);
    }

    // Todo: tidy this up. (replace with if statements)
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, EntityContext verticalEntityPosition)
    {
        switch (state.get(TYPE))
        {
            case TOP:
                return TOP_SHAPE;
            case BOTTOM:
                return BOTTOM_SHAPE;
            case FRONT:
                switch (state.get(FACING))
                {
                    case NORTH:
                        return D;
                    case SOUTH:
                        return C;
                    case EAST:
                        return B;
                    case WEST:
                        return A;
                }
            case BACK:
                switch (state.get(FACING))
                {
                    case NORTH:
                        return C;
                    case SOUTH:
                        return D;
                    case EAST:
                        return A;
                    case WEST:
                        return B;
                }
            case LEFT:
                switch (state.get(FACING))
                {
                    case NORTH:
                        return B;
                    case SOUTH:
                        return A;
                    case EAST:
                        return C;
                    case WEST:
                        return D;
                }
            case RIGHT:
                switch (state.get(FACING))
                {
                    case NORTH:
                        return A;
                    case SOUTH:
                        return B;
                    case EAST:
                        return D;
                    case WEST:
                        return C;
                }
            default:
                return SINGLE_SHAPE;
        }
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context)
    {
        BlockState state = super.getPlacementState(context);
        return state.with(WATERLOGGED, context.getWorld().getFluidState(context.getBlockPos()) == Fluids.WATER);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState otherState, IWorld world, BlockPos pos, BlockPos otherPos)
    {
        if (state.get(WATERLOGGED)) world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        return super.getStateForNeighborUpdate(state, direction, otherState, world, pos, otherPos);
    }

    @Environment(EnvType.CLIENT)
    @Override
    public boolean hasBlockEntityBreakingRender(BlockState state) { return true; }

    @Override
    public BlockRenderType getRenderType(BlockState state) { return BlockRenderType.ENTITYBLOCK_ANIMATED; }
}
