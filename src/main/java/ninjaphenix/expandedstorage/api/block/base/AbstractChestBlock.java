package ninjaphenix.expandedstorage.api.block.base;

import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.container.Container;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.stat.Stat;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
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
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import ninjaphenix.expandedstorage.ExpandedStorage;
import ninjaphenix.expandedstorage.api.Registries;
import ninjaphenix.expandedstorage.api.block.base.entity.AbstractChestBlockEntity;
import ninjaphenix.expandedstorage.api.block.enums.CursedChestType;
import ninjaphenix.expandedstorage.api.inventory.DoubleSidedInventory;

import java.util.List;
import java.util.Optional;

@SuppressWarnings("deprecation")
public abstract class AbstractChestBlock extends BlockWithEntity implements InventoryProvider
{
	public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
	public static final EnumProperty<CursedChestType> TYPE = EnumProperty.of("type", CursedChestType.class);
	private static final String DOUBLE_PREFIX = "container.expandedstorage.generic_double";
	private static final DoubleBlockProperties.PropertyRetriever<AbstractChestBlockEntity, Optional<SidedInventory>> INVENTORY_RETRIEVER =
			new DoubleBlockProperties.PropertyRetriever<AbstractChestBlockEntity, Optional<SidedInventory>>()
			{

				@Override
				public Optional<SidedInventory> getFromBoth(AbstractChestBlockEntity object, AbstractChestBlockEntity object2)
				{
					return Optional.of(new DoubleSidedInventory(object, object2));
				}

				@Override
				public Optional<SidedInventory> getFrom(AbstractChestBlockEntity object) { return Optional.of(object); }

				@Override
				public Optional<SidedInventory> getFallback() { return Optional.empty(); }
			};
	private static final DoubleBlockProperties.PropertyRetriever<AbstractChestBlockEntity, Optional<Text>> NAME_RETRIEVER =
			new DoubleBlockProperties.PropertyRetriever<AbstractChestBlockEntity, Optional<Text>>()
			{
				@Override
				public Optional<Text> getFromBoth(AbstractChestBlockEntity mainBlockEntity, AbstractChestBlockEntity secondaryBlockEntity)
				{
					Text v = new TranslatableText(DOUBLE_PREFIX, mainBlockEntity.getDisplayName());
					if (mainBlockEntity.hasCustomName()) { v = mainBlockEntity.getDisplayName(); }
					if (secondaryBlockEntity.hasCustomName()) { v = secondaryBlockEntity.getDisplayName(); }
					return Optional.of(v);
				}

				@Override
				public Optional<Text> getFrom(AbstractChestBlockEntity mainBlockEntity) { return Optional.of(mainBlockEntity.getDisplayName()); }

				@Override
				public Optional<Text> getFallback() { return Optional.empty(); }
			};

	public AbstractChestBlock(Settings settings)
	{
		super(settings);
		setDefaultState(getDefaultState().with(FACING, Direction.SOUTH).with(TYPE, CursedChestType.SINGLE));
	}

	private static boolean isChestBlocked(IWorld world, BlockPos pos) { return hasBlockOnTop(world, pos) || hasOcelotOnTop(world, pos); }

	public static SidedInventory getInventoryStatic(IWorld world, BlockPos pos)
	{
		Optional<SidedInventory> inventory = retrieve(world.getBlockState(pos), world, pos, INVENTORY_RETRIEVER);
		return inventory.orElse(null);
	}

	public static CursedChestType getChestType(Direction facing, Direction offset)
	{
		if (facing.rotateYClockwise() == offset) { return CursedChestType.RIGHT; }
		else if (facing.rotateYCounterclockwise() == offset) { return CursedChestType.LEFT; }
		else if (facing == offset) { return CursedChestType.BACK; }
		else if (facing == offset.getOpposite()) { return CursedChestType.FRONT; }
		else if (offset == Direction.DOWN) { return CursedChestType.TOP; }
		else if (offset == Direction.UP) { return CursedChestType.BOTTOM; }
		return CursedChestType.SINGLE;
	}

	public static BlockPos getPairedPos(IWorld world, BlockPos pos)
	{
		BlockState state = world.getBlockState(pos);
		CursedChestType chestType = state.get(TYPE);
		if (chestType == CursedChestType.SINGLE) { return null; }
		else if (chestType == CursedChestType.TOP) { return pos.offset(Direction.DOWN); }
		else if (chestType == CursedChestType.BOTTOM) { return pos.offset(Direction.UP); }
		else if (chestType == CursedChestType.LEFT) { return pos.offset(state.get(FACING).rotateYCounterclockwise()); }
		else if (chestType == CursedChestType.RIGHT) { return pos.offset(state.get(FACING).rotateYClockwise()); }
		else if (chestType == CursedChestType.FRONT) { return pos.offset(state.get(FACING).getOpposite()); }
		else { return pos.offset(state.get(FACING)); }
	}

	private static <T> T retrieve(BlockState clickedState, IWorld world, BlockPos clickedPos,
			DoubleBlockProperties.PropertyRetriever<AbstractChestBlockEntity, T> propertyRetriever)
	{
		BlockEntity clickedBlockEntity = world.getBlockEntity(clickedPos);
		if (!(clickedBlockEntity instanceof AbstractChestBlockEntity) || isChestBlocked(world, clickedPos)) { return propertyRetriever.getFallback(); }
		AbstractChestBlockEntity clickedChestBlockEntity = (AbstractChestBlockEntity) clickedBlockEntity;
		CursedChestType clickedChestType = clickedState.get(TYPE);
		if (clickedChestType == CursedChestType.SINGLE) { return propertyRetriever.getFrom(clickedChestBlockEntity); }
		BlockPos pairedPos = getPairedPos(world, clickedPos);
		BlockState pairedState = world.getBlockState(pairedPos);
		if (pairedState.getBlock() == clickedState.getBlock())
		{
			CursedChestType pairedChestType = pairedState.get(TYPE);
			if (pairedChestType != CursedChestType.SINGLE && clickedChestType != pairedChestType && pairedState.get(FACING) == clickedState.get(FACING))
			{
				if (isChestBlocked(world, pairedPos)) { return propertyRetriever.getFallback(); }
				BlockEntity pairedBlockEntity = world.getBlockEntity(pairedPos);
				if (pairedBlockEntity instanceof AbstractChestBlockEntity)
				{
					if (clickedChestType.isRenderedType())
					{ return propertyRetriever.getFromBoth(clickedChestBlockEntity, (AbstractChestBlockEntity) pairedBlockEntity); }
					else
					{ return propertyRetriever.getFromBoth((AbstractChestBlockEntity) pairedBlockEntity, clickedChestBlockEntity); }
				}
			}
		}
		return propertyRetriever.getFrom(clickedChestBlockEntity);
	}

	private static boolean hasBlockOnTop(BlockView view, BlockPos pos)
	{
		BlockPos up = pos.up();
		BlockState state = view.getBlockState(up);
		return state.isSimpleFullBlock(view, up) && !(state.getBlock() instanceof AbstractChestBlock);
	}

	private static boolean hasOcelotOnTop(IWorld world, BlockPos pos)
	{
		List<CatEntity> cats = world.getNonSpectatingEntities(CatEntity.class,
				new Box(pos.getX(), pos.getY() + 1, pos.getZ(), pos.getX() + 1, pos.getY() + 2, pos.getZ() + 1));
		for (CatEntity catEntity_1 : cats) { if (catEntity_1.isSitting()) { return true; } }
		return false;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) { builder.add(FACING, TYPE); }

	@Override
	public boolean hasComparatorOutput(BlockState state) { return true; }

	@Override
	public int getComparatorOutput(BlockState state, World world, BlockPos pos) { return Container.calculateComparatorOutput(getInventory(state, world, pos)); }

	@Override
	public BlockState rotate(BlockState state, BlockRotation rotation) { return state.with(FACING, rotation.rotate(state.get(FACING))); }

	@Override
	public BlockState mirror(BlockState state, BlockMirror mirror) { return state.rotate(mirror.getRotation(state.get(FACING))); }

	@Override
	public SidedInventory getInventory(BlockState state, IWorld world, BlockPos pos)
	{
		Optional<SidedInventory> inventory = retrieve(state, world, pos, INVENTORY_RETRIEVER);
		return inventory.orElse(null);
	}

	private Stat<Identifier> getOpenStat() { return Stats.CUSTOM.getOrCreateStat(Stats.OPEN_CHEST); }

	// todo: go over this to optimize it aka just rewrite it
	@Override
	public BlockState getPlacementState(ItemPlacementContext context)
	{
		World world = context.getWorld();
		BlockPos pos = context.getBlockPos();
		CursedChestType chestType = CursedChestType.SINGLE;
		Direction direction_1 = context.getPlayerFacing().getOpposite();
		Direction direction_2 = context.getSide();
		boolean shouldCancelInteraction = context.shouldCancelInteraction();
		if (shouldCancelInteraction)
		{
			BlockState state;
			Direction direction_3;
			if (direction_2.getAxis() == Direction.Axis.Y)
			{
				state = world.getBlockState(pos.offset(direction_2.getOpposite()));
				direction_3 = state.getBlock() == this && state.get(TYPE) == CursedChestType.SINGLE ? state.get(FACING) : null;
				if (direction_3 != null && direction_3.getAxis() != direction_2.getAxis() && direction_3 == direction_1)
				{ chestType = direction_2 == Direction.UP ? CursedChestType.TOP : CursedChestType.BOTTOM; }
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
							if (state.get(FACING).getHorizontal() < 2) { offsetDir = offsetDir.getOpposite(); }
							if (direction_1 == state.get(FACING))
							{
								if (offsetDir == Direction.WEST || offsetDir == Direction.NORTH) { chestType = CursedChestType.LEFT; }
								else { chestType = CursedChestType.RIGHT; }
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
				if (state.getBlock() != this || state.get(TYPE) != CursedChestType.SINGLE || state.get(FACING) != direction_1) { continue; }
				CursedChestType type = getChestType(direction_1, dir);
				if (type != CursedChestType.SINGLE)
				{
					chestType = type;
					break;
				}
			}
		}
		return getDefaultState().with(FACING, direction_1).with(TYPE, chestType);
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, IWorld world, BlockPos pos,
			BlockPos neighborPos)
	{
		CursedChestType type = state.get(TYPE);
		Direction facing = state.get(FACING);
		if (type == CursedChestType.TOP && world.getBlockState(pos.offset(Direction.DOWN)).getBlock() != this)
		{
			return state.with(TYPE, CursedChestType.SINGLE);
		}
		if (type == CursedChestType.BOTTOM && world.getBlockState(pos.offset(Direction.UP)).getBlock() != this)
		{
			return state.with(TYPE, CursedChestType.SINGLE);
		}
		if (type == CursedChestType.FRONT && world.getBlockState(pos.offset(facing.getOpposite())).getBlock() != this)
		{ return state.with(TYPE, CursedChestType.SINGLE); }
		if (type == CursedChestType.BACK && world.getBlockState(pos.offset(facing)).getBlock() != this) { return state.with(TYPE, CursedChestType.SINGLE); }
		if (type == CursedChestType.LEFT && world.getBlockState(pos.offset(facing.rotateYCounterclockwise())).getBlock() != this)
		{ return state.with(TYPE, CursedChestType.SINGLE); }
		if (type == CursedChestType.RIGHT && world.getBlockState(pos.offset(facing.rotateYClockwise())).getBlock() != this)
		{ return state.with(TYPE, CursedChestType.SINGLE); }
		if (type == CursedChestType.SINGLE)
		{
			BlockState realOtherState = world.getBlockState(pos.offset(direction));
			if (!realOtherState.contains(TYPE)) { return state.with(TYPE, CursedChestType.SINGLE); }
			CursedChestType newType = getChestType(facing, direction);
			if (realOtherState.get(TYPE) == newType.getOpposite() && facing == realOtherState.get(FACING))
			{
				return state.with(TYPE, newType);
			}
		}
		return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack)
	{
		if (stack.hasCustomName())
		{
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof AbstractChestBlockEntity) { ((AbstractChestBlockEntity) blockEntity).setCustomName(stack.getName()); }
		}
	}

	@Override
	public void onBlockRemoved(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved)
	{
		if (state.getBlock() != newState.getBlock())
		{
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof Inventory)
			{
				ItemScatterer.spawn(world, pos, (Inventory) blockEntity);
				world.updateNeighbors(pos, this);
			}
			super.onBlockRemoved(state, world, pos, newState, moved);
		}
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
	{
		if (!world.isClient)
		{
			openContainer(state, world, pos, player, hand, hit);
			player.incrementStat(getOpenStat());
		}
		return ActionResult.SUCCESS;
	}

	/*
		This method must be overridden if you are not using cursed chests mod with this api. ( soon not going to be the case )
	*/
	protected void openContainer(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
	{
		Optional<Text> containerName = retrieve(state, world, pos, NAME_RETRIEVER);
		if (!containerName.isPresent()) { return; }
		BlockEntity clickedBlockEntity = world.getBlockEntity(pos);
		BlockPos pairedPos = getPairedPos(world, pos);
		if (pairedPos == null)
		{
			if (clickedBlockEntity instanceof AbstractChestBlockEntity)
			{
				AbstractChestBlockEntity cursedClickBlockEntity = (AbstractChestBlockEntity) clickedBlockEntity;
				if (cursedClickBlockEntity.checkUnlocked(player))
				{
					cursedClickBlockEntity.checkLootInteraction(player);
					ContainerProviderRegistry.INSTANCE.openContainer(ExpandedStorage.getId("scrollcontainer"), player, (packetByteBuf ->
					{
						packetByteBuf.writeBlockPos(pos);
						packetByteBuf.writeText(containerName.get());
					}));
				}
			}
		}
		else
		{
			BlockEntity pairedBlockEntity = world.getBlockEntity(pairedPos);
			if (clickedBlockEntity instanceof AbstractChestBlockEntity && pairedBlockEntity instanceof AbstractChestBlockEntity)
			{
				AbstractChestBlockEntity cursedClickBlockEntity = (AbstractChestBlockEntity) clickedBlockEntity;
				AbstractChestBlockEntity cursedPairedBlockEntity = (AbstractChestBlockEntity) pairedBlockEntity;
				if (cursedClickBlockEntity.checkUnlocked(player) && cursedPairedBlockEntity.checkUnlocked(player))
				{
					cursedClickBlockEntity.checkLootInteraction(player);
					cursedPairedBlockEntity.checkLootInteraction(player);
					ContainerProviderRegistry.INSTANCE.openContainer(ExpandedStorage.getId("scrollcontainer"), player, (packetByteBuf ->
					{
						packetByteBuf.writeBlockPos(pos);
						packetByteBuf.writeText(containerName.get());
					}));
				}
			}
		}
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) { return BlockRenderType.MODEL; }

	@Override
	public PistonBehavior getPistonBehavior(BlockState state) { return PistonBehavior.IGNORE; }

	public abstract <T extends Registries.TierData> SimpleRegistry<T> getDataRegistry();
}
