package ninjaphenix.expandedstorage.api.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import ninjaphenix.expandedstorage.api.Registries;
import ninjaphenix.expandedstorage.api.block.entity.AbstractChestTileEntity;
import ninjaphenix.expandedstorage.api.block.enums.CursedChestType;
import ninjaphenix.expandedstorage.api.container.ScrollableContainer;
import ninjaphenix.expandedstorage.api.inventory.IDoubleSidedInventory;

import javax.annotation.Nullable;
import java.util.List;

@SuppressWarnings("deprecation")
public abstract class AbstractChestBlock extends Block
{
	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final EnumProperty<CursedChestType> TYPE = EnumProperty.create("type", CursedChestType.class);
	private static final String DOUBLE_PREFIX = "container.expandedstorage.generic_double";
	private static final PropertyRetriever<ISidedInventory> INVENTORY_RETRIEVER = new PropertyRetriever<ISidedInventory>()
	{
		@Override
		public ISidedInventory getFromDoubleChest(AbstractChestTileEntity maintileEntity, AbstractChestTileEntity secondarytileEntity)
		{ return new IDoubleSidedInventory(maintileEntity, secondarytileEntity); }

		@Override
		public ISidedInventory getFromSingleChest(AbstractChestTileEntity maintileEntity) { return maintileEntity; }
	};
	private static final PropertyRetriever<ITextComponent> NAME_RETRIEVER = new PropertyRetriever<ITextComponent>()
	{
		@Override
		public ITextComponent getFromDoubleChest(AbstractChestTileEntity maintileEntity, AbstractChestTileEntity secondarytileEntity)
		{
			if (maintileEntity.hasCustomName()) return maintileEntity.getDisplayName();
			if (secondarytileEntity.hasCustomName()) return secondarytileEntity.getDisplayName();
			return new TranslationTextComponent(DOUBLE_PREFIX, maintileEntity.getDisplayName());
		}

		@Override
		public ITextComponent getFromSingleChest(AbstractChestTileEntity maintileEntity) { return maintileEntity.getDisplayName(); }
	};

	@SuppressWarnings("WeakerAccess")
	public AbstractChestBlock(Properties properties)
	{
		super(properties);
		setDefaultState(getDefaultState().with(FACING, Direction.NORTH).with(TYPE, CursedChestType.SINGLE));
	}

	private static boolean isChestBlocked(IWorld world, BlockPos pos) { return hasBlockOnTop(world, pos) || hasOcelotOnTop(world, pos); }

	public static CursedChestType getChestType(Direction facing, Direction offset)
	{
		if (facing.rotateY() == offset) return CursedChestType.RIGHT;
		else if (facing.rotateYCCW() == offset) return CursedChestType.LEFT;
		else if (facing == offset) return CursedChestType.BACK;
		else if (facing == offset.getOpposite()) return CursedChestType.FRONT;
		else if (offset == Direction.DOWN) return CursedChestType.TOP;
		else if (offset == Direction.UP) return CursedChestType.BOTTOM;
		return CursedChestType.SINGLE;
	}

	public static BlockPos getPairedPos(IWorld world, BlockPos pos)
	{
		BlockState state = world.getBlockState(pos);
		CursedChestType chestType = state.get(TYPE);
		if (chestType == CursedChestType.SINGLE) return null;
		else if (chestType == CursedChestType.TOP) return pos.offset(Direction.DOWN);
		else if (chestType == CursedChestType.BOTTOM) return pos.offset(Direction.UP);
		else if (chestType == CursedChestType.LEFT) return pos.offset(state.get(FACING).rotateYCCW());
		else if (chestType == CursedChestType.RIGHT) return pos.offset(state.get(FACING).rotateY());
		else if (chestType == CursedChestType.FRONT) return pos.offset(state.get(FACING).getOpposite());
		else return pos.offset(state.get(FACING));
	}

	private static <T> T retrieve(BlockState clickedState, IWorld world, BlockPos clickedPos, PropertyRetriever<T> propertyRetriever)
	{
		TileEntity clickedtileEntity = world.getTileEntity(clickedPos);
		if (!(clickedtileEntity instanceof AbstractChestTileEntity) || isChestBlocked(world, clickedPos)) return null;
		AbstractChestTileEntity clickedChesttileEntity = (AbstractChestTileEntity) clickedtileEntity;
		CursedChestType clickedChestType = clickedState.get(TYPE);
		if (clickedChestType == CursedChestType.SINGLE) return propertyRetriever.getFromSingleChest(clickedChesttileEntity);
		BlockPos pairedPos = getPairedPos(world, clickedPos);
		BlockState pairedState = world.getBlockState(pairedPos);
		if (pairedState.getBlock() == clickedState.getBlock())
		{
			CursedChestType pairedChestType = pairedState.get(TYPE);
			if (pairedChestType != CursedChestType.SINGLE && clickedChestType != pairedChestType && pairedState.get(FACING) == clickedState.get(FACING))
			{
				if (isChestBlocked(world, pairedPos)) return null;
				TileEntity pairedtileEntity = world.getTileEntity(pairedPos);
				if (pairedtileEntity instanceof AbstractChestTileEntity)
				{
					if (clickedChestType.isRenderedType())
						return propertyRetriever.getFromDoubleChest(clickedChesttileEntity, (AbstractChestTileEntity) pairedtileEntity);
					else
						return propertyRetriever.getFromDoubleChest((AbstractChestTileEntity) pairedtileEntity, clickedChesttileEntity);
				}
			}
		}
		return propertyRetriever.getFromSingleChest(clickedChesttileEntity);
	}

	private static boolean hasBlockOnTop(IWorld view, BlockPos pos)
	{
		BlockPos up = pos.up();
		BlockState state = view.getBlockState(up);
		return state.isNormalCube(view, up) && !(state.getBlock() instanceof AbstractChestBlock);
	}

	private static boolean hasOcelotOnTop(IWorld world, BlockPos pos)
	{
		List<CatEntity> cats = world.getEntitiesWithinAABB(CatEntity.class, new AxisAlignedBB(
				pos.getX(), pos.getY() + 1, pos.getZ(), pos.getX() + 1, pos.getY() + 2, pos.getZ() + 1));
		for (CatEntity catEntity_1 : cats) if (catEntity_1.isSitting()) return true;
		return false;
	}

	@Override
	public boolean hasComparatorInputOverride(BlockState p_149740_1_) { return true; }

	@Override
	public int getComparatorInputOverride(BlockState state, World world, BlockPos pos)
	{
		return Container.calcRedstoneFromInventory(getInventory(state, world, pos));
	}

	@Override
	public boolean eventReceived(BlockState state, World world, BlockPos pos, int id, int param)
	{
		super.eventReceived(state, world, pos, id, param);
		TileEntity tileEntity = world.getTileEntity(pos);
		return tileEntity != null && tileEntity.receiveClientEvent(id, param);
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirror) { return state.rotate(mirror.toRotation(state.get(FACING))); }

	@Override
	public BlockState rotate(BlockState state, Rotation rotation) { return state.with(FACING, rotation.rotate(state.get(FACING))); }

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) { builder.add(FACING, TYPE); }

	public static ISidedInventory getInventory(BlockState state, IWorld world, BlockPos pos) { return retrieve(state, world, pos, INVENTORY_RETRIEVER); }

	private Stat<ResourceLocation> getOpenStat() { return Stats.CUSTOM.get(Stats.OPEN_CHEST); }

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context)
	{
		World world = context.getWorld();
		BlockPos pos = context.getPos();
		CursedChestType chestType = CursedChestType.SINGLE;
		Direction direction_1 = context.getPlacementHorizontalFacing().getOpposite();
		Direction direction_2 = context.getFace();
		boolean shouldCancelInteraction = context.func_225518_g_(); // Is sneaking
		if (shouldCancelInteraction)
		{
			BlockState state;
			Direction direction_3;
			if (direction_2.getAxis() == Direction.Axis.Y)
			{
				state = world.getBlockState(pos.offset(direction_2.getOpposite()));
				direction_3 = state.getBlock() == this && state.get(TYPE) == CursedChestType.SINGLE ? state.get(FACING) : null;
				if (direction_3 != null && direction_3.getAxis() != direction_2.getAxis() && direction_3 == direction_1)
					chestType = direction_2 == Direction.UP ? CursedChestType.TOP : CursedChestType.BOTTOM;
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
							if (state.get(FACING).getHorizontalIndex() < 2) offsetDir = offsetDir.getOpposite();
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
		return getDefaultState().with(FACING, direction_1).with(TYPE, chestType);
	}

	@Override
	public BlockState updatePostPlacement(BlockState state, Direction direction, BlockState otherState, IWorld world, BlockPos pos, BlockPos otherPos)
	{
		CursedChestType type = state.get(TYPE);
		Direction facing = state.get(FACING);
		if (type == CursedChestType.TOP && world.getBlockState(pos.offset(Direction.DOWN)).getBlock() != this) return state.with(TYPE, CursedChestType.SINGLE);
		if (type == CursedChestType.BOTTOM && world.getBlockState(pos.offset(Direction.UP)).getBlock() != this) return state.with(TYPE, CursedChestType.SINGLE);
		if (type == CursedChestType.FRONT && world.getBlockState(pos.offset(facing.getOpposite())).getBlock() != this)
			return state.with(TYPE, CursedChestType.SINGLE);
		if (type == CursedChestType.BACK && world.getBlockState(pos.offset(facing)).getBlock() != this) return state.with(TYPE, CursedChestType.SINGLE);
		if (type == CursedChestType.LEFT && world.getBlockState(pos.offset(facing.rotateYCCW())).getBlock() != this)
			return state.with(TYPE, CursedChestType.SINGLE);
		if (type == CursedChestType.RIGHT && world.getBlockState(pos.offset(facing.rotateY())).getBlock() != this)
			return state.with(TYPE, CursedChestType.SINGLE);
		if (type == CursedChestType.SINGLE)
		{
			BlockState realOtherState = world.getBlockState(pos.offset(direction));
			if (!realOtherState.has(TYPE)) return state.with(TYPE, CursedChestType.SINGLE);
			CursedChestType newType = getChestType(facing, direction);
			if (realOtherState.get(TYPE) == newType.getOpposite() && facing == realOtherState.get(FACING)) return state.with(TYPE, newType);
		}
		return super.updatePostPlacement(state, direction, otherState, world, pos, otherPos);
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
	{
		if (stack.hasDisplayName())
		{
			TileEntity tileEntity = world.getTileEntity(pos);
			if (tileEntity instanceof AbstractChestTileEntity) ((AbstractChestTileEntity) tileEntity).setCustomName(stack.getDisplayName());
		}
	}

	@Override
	public void onReplaced(BlockState state_1, World world, BlockPos pos, BlockState state_2, boolean boolean_1)
	{
		if (state_1.getBlock() != state_2.getBlock())
		{
			TileEntity tileEntity = world.getTileEntity(pos);
			if (tileEntity instanceof IInventory)
			{
				InventoryHelper.dropInventoryItems(world, pos, (IInventory) tileEntity);
				world.notifyNeighbors(pos, this);
			}
			super.onReplaced(state_1, world, pos, state_2, boolean_1);
		}
	}

	@Override // Block activated / used
	public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit)
	{
		if (!world.isRemote)
		{
			TileEntity tileEntity = world.getTileEntity(pos);
			if (tileEntity instanceof AbstractChestTileEntity)
			{
				IInventory inventory = retrieve(state, world, pos, INVENTORY_RETRIEVER);
				ITextComponent name = retrieve(state, world, pos, NAME_RETRIEVER);
				NetworkHooks.openGui((ServerPlayerEntity) player, new INamedContainerProvider()
				{
					@Override
					public ITextComponent getDisplayName() { return name; }

					@Override
					public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity)
					{
						return new ScrollableContainer(i, playerInventory, inventory, name);
					}
				}, (p) -> {
					p.writeInt(inventory.getSizeInventory());
					p.writeTextComponent(name);
				});
				player.addStat(getOpenStat());
			}

		}
		return ActionResultType.SUCCESS;
	}

	@Override
	public boolean hasTileEntity(BlockState state) { return true; }

	public abstract <T extends Registries.TierData> SimpleRegistry<T> getDataRegistry();

	interface PropertyRetriever<T>
	{
		T getFromDoubleChest(AbstractChestTileEntity var1, AbstractChestTileEntity var2);

		T getFromSingleChest(AbstractChestTileEntity var1);
	}
}