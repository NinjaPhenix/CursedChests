package ninjaphenix.expandedstorage.api.item;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.ChestType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;
import ninjaphenix.expandedstorage.ExpandedStorage;
import ninjaphenix.expandedstorage.api.Registries;
import ninjaphenix.expandedstorage.api.block.AbstractChestBlock;
import ninjaphenix.expandedstorage.api.block.entity.AbstractChestTileEntity;
import ninjaphenix.expandedstorage.api.block.enums.CursedChestType;

@SuppressWarnings({ "OptionalGetWithoutIsPresent", "ConstantConditions" })
public class ChestConversionItem extends ChestModifierItem
{
	private ResourceLocation from, to;

	public ChestConversionItem(ResourceLocation from, ResourceLocation to)
	{
		super(new Item.Properties().group(ExpandedStorage.group).maxStackSize(16));
		this.from = from;
		this.to = to;
	}

	private void upgradeCursedChest(World world, BlockPos pos, BlockState state)
	{
		AbstractChestTileEntity tileEntity = (AbstractChestTileEntity) world.getTileEntity(pos);
		final SimpleRegistry<Registries.TierData> registry = ((AbstractChestBlock) state.getBlock()).getDataRegistry();
		NonNullList<ItemStack> inventoryData = NonNullList.withSize(registry.getValue(to).get().getSlotCount(), ItemStack.EMPTY);
		ItemStackHelper.loadAllItems(tileEntity.write(new CompoundNBT()), inventoryData);
		world.removeTileEntity(pos);
		BlockState newState = ForgeRegistries.BLOCKS.getValue(registry.getValue(to).get().getBlockId()).getDefaultState();
		if (newState.getBlock() instanceof IWaterLoggable)
			newState = newState.with(BlockStateProperties.WATERLOGGED, state.get(BlockStateProperties.WATERLOGGED));
		world.setBlockState(pos, newState.with(BlockStateProperties.HORIZONTAL_FACING, state.get(BlockStateProperties.HORIZONTAL_FACING))
										 .with(AbstractChestBlock.TYPE, state.get(AbstractChestBlock.TYPE)));
		tileEntity = (AbstractChestTileEntity) world.getTileEntity(pos);
		tileEntity.read(ItemStackHelper.saveAllItems(tileEntity.write(new CompoundNBT()), inventoryData));
	}

	private void upgradeChest(World world, BlockPos pos, BlockState state)
	{
		TileEntity tileEntity = world.getTileEntity(pos);
		NonNullList<ItemStack> inventoryData = NonNullList.withSize(Registries.MODELED.getValue(from).get().getSlotCount(), ItemStack.EMPTY);
		ItemStackHelper.loadAllItems(tileEntity.write(new CompoundNBT()), inventoryData);
		world.removeTileEntity(pos);
		BlockState newState = ForgeRegistries.BLOCKS.getValue(Registries.MODELED.getValue(to).get().getBlockId()).getDefaultState();
		world.setBlockState(pos, newState.with(BlockStateProperties.HORIZONTAL_FACING, state.get(BlockStateProperties.HORIZONTAL_FACING))
										 .with(BlockStateProperties.WATERLOGGED, state.get(BlockStateProperties.WATERLOGGED))
										 .with(AbstractChestBlock.TYPE, CursedChestType.valueOf(state.get(BlockStateProperties.CHEST_TYPE))));
		tileEntity = world.getTileEntity(pos);
		tileEntity.read(ItemStackHelper.saveAllItems(tileEntity.write(new CompoundNBT()), inventoryData));
	}

	@Override
	protected ActionResultType useModifierOnChestBlock(ItemUseContext context, BlockState mainState, BlockPos mainBlockPos, BlockState otherState,
			BlockPos otherBlockPos)
	{
		World world = context.getWorld();
		PlayerEntity player = context.getPlayer();
		AbstractChestBlock chestBlock = (AbstractChestBlock) mainState.getBlock();
		if (!chestBlock.getRegistryName().equals(chestBlock.getDataRegistry().getValue(from).get().getBlockId())) return ActionResultType.FAIL;
		ItemStack handStack = player.getHeldItem(context.getHand());
		if (otherBlockPos == null)
		{
			if (!world.isRemote)
			{
				upgradeCursedChest(world, mainBlockPos, mainState);
				handStack.shrink(1);
			}
			return ActionResultType.SUCCESS;
		}
		else if (handStack.getCount() > 1 || player.isCreative())
		{
			if (!world.isRemote)
			{
				upgradeCursedChest(world, otherBlockPos, world.getBlockState(otherBlockPos));
				upgradeCursedChest(world, mainBlockPos, mainState);
				handStack.shrink(2);
			}
			return ActionResultType.SUCCESS;
		}
		return ActionResultType.FAIL;
	}

	@Override
	protected ActionResultType useModifierOnBlock(ItemUseContext context, BlockState state)
	{
		if (state.getBlock() == Blocks.CHEST && from.equals(ExpandedStorage.getRl("wood_chest")))
		{
			World world = context.getWorld();
			BlockPos mainpos = context.getPos();
			PlayerEntity player = context.getPlayer();
			ItemStack handStack = player.getHeldItem(context.getHand());
			if (state.get(BlockStateProperties.CHEST_TYPE) == ChestType.SINGLE)
			{
				if (!world.isRemote)
				{
					upgradeChest(world, mainpos, state);
					handStack.shrink(1);
				}
				return ActionResultType.SUCCESS;
			}
			else if (handStack.getCount() > 1 || player.isCreative())
			{
				BlockPos otherPos;
				if (state.get(BlockStateProperties.CHEST_TYPE) == ChestType.RIGHT)
					otherPos = mainpos.offset(state.get(BlockStateProperties.HORIZONTAL_FACING).rotateYCCW());
				else if (state.get(BlockStateProperties.CHEST_TYPE) == ChestType.LEFT)
					otherPos = mainpos.offset(state.get(BlockStateProperties.HORIZONTAL_FACING).rotateY());
				else return ActionResultType.FAIL;
				if (!world.isRemote)
				{
					upgradeChest(world, otherPos, world.getBlockState(otherPos));
					upgradeChest(world, mainpos, state);
					handStack.shrink(2);
				}
				return ActionResultType.SUCCESS;
			}
		}
		return ActionResultType.FAIL;
	}
}