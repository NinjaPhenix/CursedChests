package ninjaphenix.expandedstorage.api.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Waterloggable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EntityContext;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import ninjaphenix.expandedstorage.api.Registries;
import ninjaphenix.expandedstorage.api.Registries.ModeledTierData;
import ninjaphenix.expandedstorage.api.block.entity.CursedChestBlockEntity;
import ninjaphenix.expandedstorage.api.block.enums.CursedChestType;

import static ninjaphenix.expandedstorage.api.block.enums.CursedChestType.*;

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
    public BlockEntity createBlockEntity(BlockView view) { return new CursedChestBlockEntity(Registry.BLOCK.getId(this)); }

    @Override
    public FluidState getFluidState(BlockState state) { return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state); }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
    {
        super.appendProperties(builder);
        builder.add(WATERLOGGED);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, EntityContext context)
    {
        CursedChestType type = state.get(TYPE);
        if (type == SINGLE) return SINGLE_SHAPE;
        if (type == TOP) return TOP_SHAPE;
        if (type == BOTTOM) return BOTTOM_SHAPE;
        switch (type)
        {
            case BACK:
                switch (state.get(FACING))
                {
                    case NORTH: return C;
                    case SOUTH: return D;
                    case WEST: return B;
                    case EAST: return A;
                }
                break;
            case RIGHT:
                switch (state.get(FACING))
                {
                    case NORTH: return A;
                    case SOUTH: return B;
                    case WEST: return C;
                    case EAST: return D;
                }
                break;
            case FRONT:
                switch (state.get(FACING))
                {
                    case NORTH: return D;
                    case SOUTH: return C;
                    case WEST: return A;
                    case EAST: return B;
                }
                break;
            case LEFT:
                switch (state.get(FACING))
                {
                    case NORTH: return B;
                    case SOUTH: return A;
                    case WEST: return D;
                    case EAST: return C;
                }
                break;
        }
        return SINGLE_SHAPE;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext context)
    {
        BlockState state = super.getPlacementState(context);
        return state.with(WATERLOGGED, context.getWorld().getFluidState(context.getBlockPos()) == Fluids.WATER.getDefaultState());
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, IWorld world, BlockPos pos,
            BlockPos neighborPos)
    {
        if (state.get(WATERLOGGED)) world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) { return BlockRenderType.ENTITYBLOCK_ANIMATED; }

    @SuppressWarnings("unchecked")
    @Override
    public SimpleRegistry<ModeledTierData> getDataRegistry() { return Registries.MODELED; }
}
