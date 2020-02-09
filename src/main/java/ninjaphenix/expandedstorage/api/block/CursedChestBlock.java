package ninjaphenix.expandedstorage.api.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraftforge.registries.ForgeRegistries;
import ninjaphenix.expandedstorage.api.Registries;
import ninjaphenix.expandedstorage.api.block.entity.CursedChestTileEntity;

import javax.annotation.Nullable;

@SuppressWarnings("deprecation")
public class CursedChestBlock extends AbstractChestBlock implements IWaterLoggable
{
	private static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	private static final VoxelShape SINGLE_SHAPE = Block.makeCuboidShape(1, 0, 1, 15, 14, 15);
	private static final VoxelShape TOP_SHAPE = Block.makeCuboidShape(1, -16, 1, 15, 14, 15);
	private static final VoxelShape BOTTOM_SHAPE = Block.makeCuboidShape(1, 0, 1, 15, 30, 15);
	private static final VoxelShape A = Block.makeCuboidShape(1, 0, 1, 31, 14, 15);
	private static final VoxelShape B = Block.makeCuboidShape(1 - 16, 0, 1, 15, 14, 15);
	private static final VoxelShape C = Block.makeCuboidShape(1, 0, 1 - 16, 15, 14, 15);
	private static final VoxelShape D = Block.makeCuboidShape(1, 0, 1, 15, 14, 31);

	public CursedChestBlock(final Properties properties)
	{
		super(properties);
		setDefaultState(getDefaultState().with(WATERLOGGED, false));
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(final BlockState state, final IBlockReader world)
	{
		return new CursedChestTileEntity(ForgeRegistries.BLOCKS.getKey(this));
	}

	@Override
	public IFluidState getFluidState(final BlockState state)
	{
		return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
	{
		super.fillStateContainer(builder);
		builder.add(WATERLOGGED);
	}

	@Override
	public VoxelShape getShape(final BlockState state, final IBlockReader reader, final BlockPos pos, final ISelectionContext context)
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
	public BlockState getStateForPlacement(final BlockItemUseContext context)
	{
		final BlockState state = super.getStateForPlacement(context);
		return state.with(WATERLOGGED, context.getWorld().getFluidState(context.getPos()).getFluid() == Fluids.WATER);
	}

	@Override
	public BlockState updatePostPlacement(final BlockState state, final Direction direction, final BlockState otherState, final IWorld world,
			final BlockPos pos, final BlockPos otherPos)
	{
		if (state.get(WATERLOGGED)) { world.getPendingFluidTicks().scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world)); }
		return super.updatePostPlacement(state, direction, otherState, world, pos, otherPos);
	}

	@Override
	public BlockRenderType getRenderType(final BlockState state) { return BlockRenderType.ENTITYBLOCK_ANIMATED; }

	@Override
	@SuppressWarnings("unchecked")
	public SimpleRegistry<Registries.ModeledTierData> getDataRegistry() { return Registries.MODELED; }
}