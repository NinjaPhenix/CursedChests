package ninjaphenix.cursedchests.api.block;

import ninjaphenix.cursedchests.api.block.entity.CursedChestBlockEntity;
import ninjaphenix.cursedchests.api.inventory.DoubleSidedInventory;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.container.Container;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.stat.Stat;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import java.util.List;

@SuppressWarnings("deprecation")
public class CursedChestBlock extends BlockWithEntity implements Waterloggable, InventoryProvider
{
	@Override public BlockEntity createBlockEntity(BlockView var1){ return new CursedChestBlockEntity(Registry.BLOCK.getId(this)); }

	interface PropertyRetriever<T>
	{
		T getFromDoubleChest(CursedChestBlockEntity var1, CursedChestBlockEntity var2);

		T getFromSingleChest(CursedChestBlockEntity var1);
	}

	public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
	public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
	public static final EnumProperty<CursedChestType> TYPE = EnumProperty.of("type", CursedChestType.class);
	private static final VoxelShape SINGLE_SHAPE = Block.createCuboidShape(1, 0, 1, 15, 14, 15);
	private static final VoxelShape TOP_SHAPE = Block.createCuboidShape(1, -16, 1, 15, 14, 15);
	private static final VoxelShape BOTTOM_SHAPE = Block.createCuboidShape(1, 0, 1, 15, 30, 15);
	private static final VoxelShape A = Block.createCuboidShape(1, 0, 1, 31, 14, 15);
	private static final VoxelShape B = Block.createCuboidShape(1 - 16, 0, 1, 15, 14, 15);
	private static final VoxelShape C = Block.createCuboidShape(1, 0, 1 - 16, 15, 14, 15);
	private static final VoxelShape D = Block.createCuboidShape(1, 0, 1, 15, 14, 31);
	private static final String DOUBLE_PREFIX = "container.cursedchests.generic_double";

	private static final PropertyRetriever<SidedInventory> INVENTORY_RETRIEVER = new PropertyRetriever<SidedInventory>()
	{
		@Override public SidedInventory getFromDoubleChest(CursedChestBlockEntity mainBlockEntity, CursedChestBlockEntity secondaryBlockEntity){ return new DoubleSidedInventory(mainBlockEntity, secondaryBlockEntity); }

		@Override public SidedInventory getFromSingleChest(CursedChestBlockEntity mainBlockEntity){ return mainBlockEntity; }
	};

	private static final PropertyRetriever<Text> NAME_RETRIEVER = new PropertyRetriever<Text>()
	{
		@Override public Text getFromDoubleChest(CursedChestBlockEntity mainBlockEntity, CursedChestBlockEntity secondaryBlockEntity)
		{
			if (mainBlockEntity.hasCustomName()) return mainBlockEntity.getDisplayName();
			if (secondaryBlockEntity.hasCustomName()) return secondaryBlockEntity.getDisplayName();
			return new TranslatableText(DOUBLE_PREFIX, mainBlockEntity.getDisplayName());
		}

		@Override public Text getFromSingleChest(CursedChestBlockEntity mainBlockEntity){ return mainBlockEntity.getDisplayName(); }
	};

	public CursedChestBlock(Settings settings)
	{
		super(settings);
		setDefaultState(getDefaultState().with(FACING, Direction.NORTH).with(WATERLOGGED, false).with(TYPE, CursedChestType.SINGLE));
	}

	@Environment(EnvType.CLIENT) @Override public boolean hasBlockEntityBreakingRender(BlockState state){ return true; }

	@Override public BlockRenderType getRenderType(BlockState state){ return BlockRenderType.ENTITYBLOCK_ANIMATED; }

	@Override public FluidState getFluidState(BlockState state){ return state.get(WATERLOGGED) ? Fluids.WATER.getDefaultState() : super.getFluidState(state); }

	@Override protected void appendProperties(StateFactory.Builder<Block, BlockState> stateBuilder){ stateBuilder.add(FACING, TYPE, WATERLOGGED); }

	@Override public boolean hasComparatorOutput(BlockState state){ return true; }

	@Override public int getComparatorOutput(BlockState state, World world, BlockPos pos){ return Container.calculateComparatorOutput(getInventory(state, world, pos)); }

	@Override public BlockState rotate(BlockState state, BlockRotation rotation){ return state.with(FACING, rotation.rotate(state.get(FACING))); }

	@Override public BlockState mirror(BlockState state, BlockMirror mirror){ return state.rotate(mirror.getRotation(state.get(FACING))); }

	@Override public SidedInventory getInventory(BlockState state, IWorld world, BlockPos pos){ return retrieve(state, world, pos, INVENTORY_RETRIEVER); }

	private static boolean isChestBlocked(IWorld world, BlockPos pos){ return hasBlockOnTop(world, pos) || hasOcelotOnTop(world, pos); }

	private Stat<Identifier> getOpenStat(){ return Stats.CUSTOM.getOrCreateStat(Stats.OPEN_CHEST); }

	public static SidedInventory getInventoryStatic(BlockState state, IWorld world, BlockPos pos){ return retrieve(state, world, pos, INVENTORY_RETRIEVER); }

	// Todo: tidy this up.
	@Override public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, EntityContext verticalEntityPosition)
	{
		switch (state.get(TYPE))
		{
			case TOP: return TOP_SHAPE;
			case BOTTOM: return BOTTOM_SHAPE;
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
			default: return SINGLE_SHAPE;
		}
	}

	// todo: go over this to optimize it
	@Override public BlockState getPlacementState(ItemPlacementContext context)
	{
		World world = context.getWorld();
		BlockPos pos = context.getBlockPos();
		CursedChestType chestType = CursedChestType.SINGLE;
		Direction direction_1 = context.getPlayerFacing().getOpposite();
		Direction direction_2 = context.getSide();
		boolean sneaking = context.isPlayerSneaking();
		if (sneaking)
		{
			BlockState state;
			Direction direction_3;
			if (direction_2.getAxis() == Direction.Axis.Y)
			{
				state = world.getBlockState(pos.offset(direction_2.getOpposite()));
				direction_3 = state.getBlock() == this && state.get(TYPE) == CursedChestType.SINGLE ? state.get(FACING) : null;
				if (direction_3 != null && direction_3.getAxis() != direction_2.getAxis() && direction_3 == direction_1) chestType = direction_2 == Direction.UP ? CursedChestType.TOP : CursedChestType.BOTTOM;
			}
			else
			{
				Direction offsetDir = direction_2.getOpposite();
				BlockState clickedBlock = world.getBlockState(pos.offset(offsetDir));
				if (clickedBlock.getBlock() == this)
				{
					if (clickedBlock.get(TYPE) == CursedChestType.SINGLE)
					{
						if (clickedBlock.get(FACING) == direction_2 && clickedBlock.get(FACING) == direction_1)
						{
							chestType = CursedChestType.FRONT;
						}
						else
						{
							state = world.getBlockState(pos.offset(direction_2.getOpposite()));
							if (state.get(FACING).getHorizontal() < 2) offsetDir = offsetDir.getOpposite();
							if (direction_1 == state.get(FACING))
							{
								if (offsetDir == Direction.WEST || offsetDir == Direction.NORTH) chestType = CursedChestType.LEFT;
								else chestType = CursedChestType.RIGHT;
							}
						}
					}
				}
			}
		}
		else
		{
			for (Direction dir : Direction.values())
			{
				BlockState state = world.getBlockState(pos.offset(dir));
				if (state.getBlock() != this || state.get(TYPE) != CursedChestType.SINGLE || state.get(FACING) != direction_1) continue;
				CursedChestType type = getChestType(direction_1, dir);
				if (type != CursedChestType.SINGLE)
				{
					chestType = type;
					break;
				}
			}
		}
		return getDefaultState().with(FACING, direction_1).with(TYPE, chestType).with(WATERLOGGED, world.getFluidState(pos).getFluid() == Fluids.WATER);
	}

	public static CursedChestType getChestType(Direction facing, Direction offset)
	{
		if (facing.rotateYClockwise() == offset) return CursedChestType.RIGHT;
		else if (facing.rotateYCounterclockwise() == offset) return CursedChestType.LEFT;
		else if (facing == offset) return CursedChestType.BACK;
		else if (facing == offset.getOpposite()) return CursedChestType.FRONT;
		else if (offset == Direction.DOWN) return CursedChestType.TOP;
		else if (offset == Direction.UP) return CursedChestType.BOTTOM;
		return CursedChestType.SINGLE;
	}

	@Override public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState otherState, IWorld world, BlockPos pos, BlockPos otherPos)
	{
		if (state.get(WATERLOGGED)) world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
		CursedChestType type = state.get(TYPE);
		Direction facing = state.get(FACING);
		if (type == CursedChestType.TOP && world.getBlockState(pos.offset(Direction.DOWN)).getBlock() != this) return state.with(TYPE, CursedChestType.SINGLE);
		if (type == CursedChestType.BOTTOM && world.getBlockState(pos.offset(Direction.UP)).getBlock() != this) return state.with(TYPE, CursedChestType.SINGLE);
		if (type == CursedChestType.FRONT && world.getBlockState(pos.offset(facing.getOpposite())).getBlock() != this) return state.with(TYPE, CursedChestType.SINGLE);
		if (type == CursedChestType.BACK && world.getBlockState(pos.offset(facing)).getBlock() != this) return state.with(TYPE, CursedChestType.SINGLE);
		if (type == CursedChestType.LEFT && world.getBlockState(pos.offset(facing.rotateYCounterclockwise())).getBlock() != this) return state.with(TYPE, CursedChestType.SINGLE);
		if (type == CursedChestType.RIGHT && world.getBlockState(pos.offset(facing.rotateYClockwise())).getBlock() != this) return state.with(TYPE, CursedChestType.SINGLE);
		if (type == CursedChestType.SINGLE)
		{
			BlockState realOtherState = world.getBlockState(pos.offset(direction));
			if (!realOtherState.contains(TYPE)) return state.with(TYPE, CursedChestType.SINGLE);
			CursedChestType newType = getChestType(facing, direction);
			if (realOtherState.get(TYPE) == newType.getOpposite() && facing == realOtherState.get(FACING))
			{
				return state.with(TYPE, newType);
			}
		}
		return super.getStateForNeighborUpdate(state, direction, otherState, world, pos, otherPos);
	}

	@Override public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack)
	{
		if (stack.hasCustomName())
		{
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof CursedChestBlockEntity) ((CursedChestBlockEntity) blockEntity).setCustomName(stack.getName());
		}
	}

	@Override public void onBlockRemoved(BlockState state_1, World world, BlockPos pos, BlockState state_2, boolean boolean_1)
	{
		if (state_1.getBlock() != state_2.getBlock())
		{
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof Inventory)
			{
				ItemScatterer.spawn(world, pos, (Inventory) blockEntity);
				world.updateNeighbors(pos, this);
			}
			super.onBlockRemoved(state_1, world, pos, state_2, boolean_1);
		}
	}

	@Override public boolean activate(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hitResult)
	{
		if (!world.isClient)
		{
			openContainer(state, world, pos, player, hand, hitResult);
			player.incrementStat(getOpenStat());
		}
		return true;
	}

	/*
		This method must be overridden if you are not using cursed chests mod with this api. ( soon not going to be the case )
	*/
	protected void openContainer(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hitResult)
	{
		Text containerName = retrieve(state, world, pos, NAME_RETRIEVER);
		if (containerName == null) return;
		BlockEntity clickedBlockEntity = world.getBlockEntity(pos);
		BlockPos pairedPos = getPairedPos(world, pos);
		if (pairedPos == null)
		{
			if (clickedBlockEntity instanceof CursedChestBlockEntity)
			{
				CursedChestBlockEntity cursedClickBlockEntity = (CursedChestBlockEntity) clickedBlockEntity;
				if (cursedClickBlockEntity.checkUnlocked(player))
				{
					cursedClickBlockEntity.checkLootInteraction(player);
					ContainerProviderRegistry.INSTANCE.openContainer(new Identifier("cursedchests", "scrollcontainer"), player, (packetByteBuf ->
					{
						packetByteBuf.writeBlockPos(pos);
						packetByteBuf.writeText(containerName);
					}));
				}
			}
		}
		else
		{
			BlockEntity pairedBlockEntity = world.getBlockEntity(pairedPos);
			if (clickedBlockEntity instanceof CursedChestBlockEntity && pairedBlockEntity instanceof CursedChestBlockEntity)
			{
				CursedChestBlockEntity cursedClickBlockEntity = (CursedChestBlockEntity) clickedBlockEntity;
				CursedChestBlockEntity cursedPairedBlockEntity = (CursedChestBlockEntity) pairedBlockEntity;
				if (cursedClickBlockEntity.checkUnlocked(player) && cursedPairedBlockEntity.checkUnlocked(player))
				{
					cursedClickBlockEntity.checkLootInteraction(player);
					cursedPairedBlockEntity.checkLootInteraction(player);
					ContainerProviderRegistry.INSTANCE.openContainer(new Identifier("cursedchests", "scrollcontainer"), player, (packetByteBuf ->
					{
						packetByteBuf.writeBlockPos(pos);
						packetByteBuf.writeText(containerName);
					}));
				}
			}
		}
	}

	public static BlockPos getPairedPos(IWorld world, BlockPos pos)
	{
		BlockState state = world.getBlockState(pos);
		CursedChestType chestType = state.get(TYPE);
		if (chestType == CursedChestType.SINGLE) return null;
		else if (chestType == CursedChestType.TOP) return pos.offset(Direction.DOWN);
		else if (chestType == CursedChestType.BOTTOM) return pos.offset(Direction.UP);
		else if (chestType == CursedChestType.LEFT) return pos.offset(state.get(FACING).rotateYCounterclockwise());
		else if (chestType == CursedChestType.RIGHT) return pos.offset(state.get(FACING).rotateYClockwise());
		else if (chestType == CursedChestType.FRONT) return pos.offset(state.get(FACING).getOpposite());
		else return pos.offset(state.get(FACING));
	}

	private static <T> T retrieve(BlockState clickedState, IWorld world, BlockPos clickedPos, PropertyRetriever<T> propertyRetriever)
	{
		BlockEntity clickedBlockEntity = world.getBlockEntity(clickedPos);
		if (!(clickedBlockEntity instanceof CursedChestBlockEntity) || isChestBlocked(world, clickedPos)) return null;
		CursedChestBlockEntity clickedChestBlockEntity = (CursedChestBlockEntity) clickedBlockEntity;
		CursedChestType clickedChestType = clickedState.get(TYPE);
		if (clickedChestType == CursedChestType.SINGLE) return propertyRetriever.getFromSingleChest(clickedChestBlockEntity);
		BlockPos pairedPos = getPairedPos(world, clickedPos);
		BlockState pairedState = world.getBlockState(pairedPos);
		if (pairedState.getBlock() == clickedState.getBlock())
		{
			CursedChestType pairedChestType = pairedState.get(TYPE);
			if (pairedChestType != CursedChestType.SINGLE && clickedChestType != pairedChestType && pairedState.get(FACING) == clickedState.get(FACING))
			{
				if (isChestBlocked(world, pairedPos)) return null;
				BlockEntity pairedBlockEntity = world.getBlockEntity(pairedPos);
				if (pairedBlockEntity instanceof CursedChestBlockEntity)
				{
					if (clickedChestType.isRenderedType())
					{
						return propertyRetriever.getFromDoubleChest(clickedChestBlockEntity, (CursedChestBlockEntity) pairedBlockEntity);
					}
					else
					{
						return propertyRetriever.getFromDoubleChest((CursedChestBlockEntity) pairedBlockEntity, clickedChestBlockEntity);
					}
				}
			}
		}
		return propertyRetriever.getFromSingleChest(clickedChestBlockEntity);
	}

	private static boolean hasBlockOnTop(BlockView view, BlockPos pos)
	{
		BlockPos blockPos_2 = pos.up();
		return view.getBlockState(blockPos_2).isSimpleFullBlock(view, blockPos_2);
	}

	private static boolean hasOcelotOnTop(IWorld world, BlockPos pos)
	{
		List<CatEntity> cats = world.getEntities(CatEntity.class, new Box(pos.getX(), pos.getY() + 1, pos.getZ(), pos.getX() + 1, pos.getY() + 2, pos.getZ() + 1));
		for (CatEntity catEntity_1 : cats) if (catEntity_1.isSitting()) return true;
		return false;
	}
}
