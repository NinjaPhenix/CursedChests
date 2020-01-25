package ninjaphenix.expandedstorage.api.block.base;

import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.SlabType;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.stat.Stat;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import ninjaphenix.expandedstorage.ExpandedStorage;
import ninjaphenix.expandedstorage.api.Registries;
import ninjaphenix.expandedstorage.api.block.base.entity.AbstractChestBlockEntity;

import static net.minecraft.state.property.Properties.*;

@SuppressWarnings("deprecation")
public abstract class AbstractSlabChestBlock extends BlockWithEntity implements Waterloggable, InventoryProvider
{
	protected VoxelShape DOUBLE_SHAPE = VoxelShapes.fullCube();
	protected VoxelShape BOTTOM_SHAPE = Block.createCuboidShape(0, 0, 0, 16, 8, 16);
	protected VoxelShape TOP_SHAPE = Block.createCuboidShape(0, 8, 0, 16, 16, 16);

	protected AbstractSlabChestBlock(Settings settings)
	{
		super(settings);
		setDefaultState(getDefaultState().with(HORIZONTAL_FACING, Direction.SOUTH).with(SLAB_TYPE, SlabType.BOTTOM).with(WATERLOGGED, false));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) { builder.add(HORIZONTAL_FACING, SLAB_TYPE, WATERLOGGED); }

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, EntityContext context)
	{
		SlabType slabType = state.get(SLAB_TYPE);
		if (slabType == SlabType.DOUBLE) { return DOUBLE_SHAPE; }
		else if (slabType == SlabType.TOP) { return TOP_SHAPE; }
		else { return BOTTOM_SHAPE; }
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

	public BlockState getPlacementState(ItemPlacementContext context)
	{
		BlockPos blockPos = context.getBlockPos();
		BlockState blockState = context.getWorld().getBlockState(blockPos);
		Direction facing = context.getPlayerFacing().getOpposite();
		if (blockState.getBlock() == this)
		{
			return blockState.with(SLAB_TYPE, SlabType.DOUBLE).with(WATERLOGGED, false).with(HORIZONTAL_FACING, facing);
		}
		else
		{
			FluidState fluidState = context.getWorld().getFluidState(blockPos);
			BlockState blockState2 = this.getDefaultState().with(SLAB_TYPE, SlabType.BOTTOM).with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER)
										 .with(HORIZONTAL_FACING, facing);
			Direction direction = context.getSide();
			return direction != Direction.DOWN && (direction == Direction.UP || context.getHitPos().y - (double) blockPos.getY() <= 0.5D) ? blockState2 :
					blockState2.with(SLAB_TYPE, SlabType.TOP);
		}
	}

	public boolean canReplace(BlockState state, ItemPlacementContext context)
	{
		ItemStack itemStack = context.getStack();
		SlabType slabType = state.get(SLAB_TYPE);
		if (slabType != SlabType.DOUBLE && itemStack.getItem() == this.asItem())
		{
			if (context.canReplaceExisting())
			{
				boolean bl = context.getHitPos().y - (double) context.getBlockPos().getY() > 0.5D;
				Direction direction = context.getSide();
				if (slabType == SlabType.BOTTOM)
				{
					return direction == Direction.UP || bl && direction.getAxis().isHorizontal();
				}
				else
				{
					return direction == Direction.DOWN || !bl && direction.getAxis().isHorizontal();
				}
			}
			else
			{
				return true;
			}
		}
		else
		{
			return false;
		}
	}

	protected void openContainer(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
	{
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity instanceof AbstractChestBlockEntity)
		{
			AbstractChestBlockEntity cursedClickBlockEntity = (AbstractChestBlockEntity) blockEntity;
			if (cursedClickBlockEntity.checkUnlocked(player))
			{
				cursedClickBlockEntity.checkLootInteraction(player);
				ContainerProviderRegistry.INSTANCE.openContainer(ExpandedStorage.getId("scrollcontainer"), player, (packetByteBuf ->
				{
					packetByteBuf.writeBlockPos(pos);
					packetByteBuf.writeText(cursedClickBlockEntity.getDisplayName());
				}));
			}
		}
	}

	private Stat<Identifier> getOpenStat() { return Stats.CUSTOM.getOrCreateStat(Stats.OPEN_CHEST); }

	@Override
	public BlockRenderType getRenderType(BlockState state) { return BlockRenderType.MODEL; }

	@Override
	public PistonBehavior getPistonBehavior(BlockState state) { return PistonBehavior.IGNORE; }

	public abstract <T extends Registries.TierData> SimpleRegistry<T> getDataRegistry();
}
