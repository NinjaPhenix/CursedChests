package ninjaphenix.expandedstorage.item;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.ChestType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;
import ninjaphenix.expandedstorage.ExpandedStorage;
import ninjaphenix.expandedstorage.api.Registries;
import ninjaphenix.expandedstorage.api.block.AbstractChestBlock;
import ninjaphenix.expandedstorage.api.block.CursedChestBlock;
import ninjaphenix.expandedstorage.api.block.enums.CursedChestType;
import ninjaphenix.expandedstorage.api.item.ChestModifierItem;

import javax.annotation.Nullable;
import java.util.List;

import static net.minecraft.state.properties.BlockStateProperties.WATERLOGGED;
import static net.minecraft.util.Rotation.CLOCKWISE_180;
import static net.minecraft.util.Rotation.CLOCKWISE_90;

@SuppressWarnings("ConstantConditions")
public class ChestMutatorItem extends ChestModifierItem
{
	private static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	private static final EnumProperty<CursedChestType> TYPE = AbstractChestBlock.TYPE;

	public ChestMutatorItem() { super(new Item.Properties().maxStackSize(1).group(ExpandedStorage.group)); }

	@Override
	protected ActionResultType useModifierOnChestBlock(ItemUseContext context, BlockState mainState, BlockPos mainBlockPos, BlockState otherState,
			BlockPos otherBlockPos)
	{
		PlayerEntity player = context.getPlayer();
		World world = context.getWorld();
		ItemStack stack = context.getItem();
		switch (getMode(stack))
		{
			case MERGE:
				CompoundNBT tag = stack.getOrCreateTag();
				if (tag.contains("pos"))
				{
					if (mainState.get(TYPE) == CursedChestType.SINGLE)
					{
						BlockPos pos = NBTUtil.readBlockPos(tag.getCompound("pos"));
						BlockState realOtherState = world.getBlockState(pos);
						if (realOtherState.getBlock() == mainState.getBlock() && realOtherState.get(FACING) == mainState.get(FACING) &&
								realOtherState.get(TYPE) == CursedChestType.SINGLE)
						{
							if (!world.isRemote)
							{
								BlockPos vec = pos.subtract(mainBlockPos);
								int sum = vec.getX() + vec.getY() + vec.getZ();
								if (sum == 1 || sum == -1)
								{
									CursedChestType mainChestType = CursedChestBlock
											.getChestType(mainState.get(FACING), Direction.getFacingFromVector(vec.getX(), vec.getY(), vec.getZ()));
									world.setBlockState(mainBlockPos, mainState.with(TYPE, mainChestType));
									world.setBlockState(pos, world.getBlockState(pos).with(TYPE, mainChestType.getOpposite()));
									tag.remove("pos");
									player.sendStatusMessage(new TranslationTextComponent("tooltip.expandedstorage.chest_mutator.merge_end"), true);
									player.getCooldownTracker().setCooldown(this, 5);
									return ActionResultType.SUCCESS;
								}

							}
						}
						return ActionResultType.FAIL;
					}
				}
				else
				{
					if (mainState.get(TYPE) == CursedChestType.SINGLE)
					{
						tag.put("pos", NBTUtil.writeBlockPos(mainBlockPos));
						player.sendStatusMessage(new TranslationTextComponent("tooltip.expandedstorage.chest_mutator.merge_start"), true);
						player.getCooldownTracker().setCooldown(this, 5);
						return ActionResultType.SUCCESS;
					}

				}
				break;
			case UNMERGE:
				if (otherState != null)
				{
					if (!world.isRemote)
					{
						world.setBlockState(mainBlockPos, world.getBlockState(mainBlockPos).with(TYPE, CursedChestType.SINGLE));
						world.setBlockState(otherBlockPos, world.getBlockState(otherBlockPos).with(TYPE, CursedChestType.SINGLE));
					}
					player.getCooldownTracker().setCooldown(this, 5);
					return ActionResultType.SUCCESS;
				}
				break;
			case ROTATE:
				switch (mainState.get(CursedChestBlock.TYPE))
				{
					case SINGLE:
						if (!world.isRemote) world.setBlockState(mainBlockPos, mainState.rotate(CLOCKWISE_90));
						player.getCooldownTracker().setCooldown(this, 5);
						return ActionResultType.SUCCESS;
					case FRONT:
					case BACK:
					case LEFT:
					case RIGHT:
						if (!world.isRemote)
						{
							world.setBlockState(mainBlockPos, mainState.rotate(CLOCKWISE_180).with(TYPE, mainState.get(TYPE).getOpposite()));
							world.setBlockState(otherBlockPos, otherState.rotate(CLOCKWISE_180).with(TYPE, otherState.get(TYPE).getOpposite()));
						}
						player.getCooldownTracker().setCooldown(this, 5);
						return ActionResultType.SUCCESS;
					case TOP:
					case BOTTOM:
						if (!world.isRemote)
						{
							world.setBlockState(mainBlockPos, mainState.rotate(CLOCKWISE_90));
							world.setBlockState(otherBlockPos, otherState.rotate(CLOCKWISE_90));
						}
						player.getCooldownTracker().setCooldown(this, 5);
						return ActionResultType.SUCCESS;
				}
		}
		return ActionResultType.FAIL;
	}

	@Override
	protected ActionResultType useModifierOnBlock(ItemUseContext context, BlockState state)
	{
		PlayerEntity player = context.getPlayer();
		ItemStack stack = context.getItem();
		World world = context.getWorld();
		BlockPos mainPos = context.getPos();
		MutatorModes mode = getMode(stack);
		if (state.getBlock() instanceof ChestBlock)
		{
			if (mode == MutatorModes.MERGE)
			{
				CompoundNBT tag = stack.getOrCreateTag();
				if (tag.contains("pos"))
				{
					if (state.get(ChestBlock.TYPE) == ChestType.SINGLE)
					{
						BlockPos otherPos = NBTUtil.readBlockPos(tag.getCompound("pos"));
						BlockState realOtherState = world.getBlockState(otherPos);
						if (realOtherState.getBlock() == state.getBlock() && realOtherState.get(FACING) == state.get(FACING) &&
								realOtherState.get(ChestBlock.TYPE) == ChestType.SINGLE)
						{
							BlockPos vec = otherPos.subtract(mainPos);
							int sum = vec.getX() + vec.getY() + vec.getZ();
							if (sum == 1 || sum == -1)
							{
								if (!world.isRemote)
								{
									Registries.TierData entry = Registries.MODELED.getValue(ExpandedStorage.getRl("wood_chest")).get();
									BlockState defState = ForgeRegistries.BLOCKS.getValue(entry.getBlockId())
																				.getDefaultState().with(FACING, state.get(FACING));
									CursedChestType mainChestType = AbstractChestBlock
											.getChestType(state.get(FACING), Direction.getFacingFromVector(vec.getX(), vec.getY(), vec.getZ()));

									TileEntity blockEntity = world.getTileEntity(mainPos);
									NonNullList<ItemStack> invData = NonNullList.withSize(entry.getSlotCount(), ItemStack.EMPTY);
									ItemStackHelper.loadAllItems(blockEntity.write(new CompoundNBT()), invData);
									world.removeTileEntity(mainPos);
									world.setBlockState(mainPos, defState.with(WATERLOGGED, state.get(WATERLOGGED)).with(TYPE, mainChestType));
									blockEntity = world.getTileEntity(mainPos);
									blockEntity.read(ItemStackHelper.saveAllItems(blockEntity.write(new CompoundNBT()), invData));

									blockEntity = world.getTileEntity(otherPos);
									invData = NonNullList.withSize(entry.getSlotCount(), ItemStack.EMPTY);
									ItemStackHelper.loadAllItems(blockEntity.write(new CompoundNBT()), invData);
									world.removeTileEntity(otherPos);
									world.setBlockState(otherPos, defState.with(WATERLOGGED, state.get(WATERLOGGED)).with(TYPE, mainChestType.getOpposite()));
									blockEntity = world.getTileEntity(otherPos);
									blockEntity.read(ItemStackHelper.saveAllItems(blockEntity.write(new CompoundNBT()), invData));

									tag.remove("pos");
									player.sendStatusMessage(new TranslationTextComponent("tooltip.expandedstorage.chest_mutator.merge_end"), true);
									player.getCooldownTracker().setCooldown(this, 5);
								}
								return ActionResultType.SUCCESS;
							}
						}
						return ActionResultType.FAIL;
					}
				}
				else
				{
					if (state.get(ChestBlock.TYPE) == ChestType.SINGLE)
					{
						tag.put("pos", NBTUtil.writeBlockPos(mainPos));
						player.sendStatusMessage(new TranslationTextComponent("tooltip.expandedstorage.chest_mutator.merge_start"), true);
						player.getCooldownTracker().setCooldown(this, 5);
						return ActionResultType.SUCCESS;
					}
				}
			}
			else if (mode == MutatorModes.UNMERGE)
			{
				BlockPos otherPos;
				switch (state.get(ChestBlock.TYPE))
				{
					case LEFT:
						otherPos = mainPos.offset(state.get(ChestBlock.FACING).rotateY());
						break;
					case RIGHT:
						otherPos = mainPos.offset(state.get(ChestBlock.FACING).rotateYCCW());
						break;
					default:
						return ActionResultType.FAIL;
				}
				if (!world.isRemote)
				{
					world.setBlockState(mainPos, state.with(ChestBlock.TYPE, ChestType.SINGLE));
					world.setBlockState(otherPos, world.getBlockState(otherPos).with(ChestBlock.TYPE, ChestType.SINGLE));
				}
				player.getCooldownTracker().setCooldown(this, 5);
				return ActionResultType.SUCCESS;
			}
			else if (mode == MutatorModes.ROTATE)
			{
				BlockPos otherPos;
				switch (state.get(ChestBlock.TYPE))
				{
					case LEFT:
						otherPos = mainPos.offset(state.get(ChestBlock.FACING).rotateY());
						break;
					case RIGHT:
						otherPos = mainPos.offset(state.get(ChestBlock.FACING).rotateYCCW());
						break;
					case SINGLE:
						if (!world.isRemote) world.setBlockState(mainPos, state.rotate(CLOCKWISE_90));
						player.getCooldownTracker().setCooldown(this, 5);
						return ActionResultType.SUCCESS;
					default:
						return ActionResultType.FAIL;
				}
				if (!world.isRemote)
				{
					BlockState otherState = world.getBlockState(otherPos);
					world.setBlockState(mainPos, state.rotate(CLOCKWISE_180)
													  .with(ChestBlock.TYPE, state.get(ChestBlock.TYPE).opposite()));
					world.setBlockState(otherPos, otherState.rotate(CLOCKWISE_180)
															.with(ChestBlock.TYPE, otherState.get(ChestBlock.TYPE).opposite()));
				}
				player.getCooldownTracker().setCooldown(this, 5);
				return ActionResultType.SUCCESS;
			}
		}
		else if (state.getBlock() == Blocks.ENDER_CHEST)
		{
			if (mode == MutatorModes.ROTATE)
			{
				if (!world.isRemote) world.setBlockState(mainPos, state.rotate(CLOCKWISE_90));
				player.getCooldownTracker().setCooldown(this, 5);
				return ActionResultType.SUCCESS;
			}
			return ActionResultType.FAIL;
		}
		return super.useModifierOnBlock(context, state);
	}

	@Override
	protected ActionResult<ItemStack> useModifierInAir(World world, PlayerEntity player, Hand hand)
	{
		if (player.isSneaking())
		{
			ItemStack stack = player.getHeldItem(hand);
			CompoundNBT tag = stack.getOrCreateTag();
			tag.putByte("mode", getMode(stack).next);
			if (tag.contains("pos")) tag.remove("pos");
			if (!world.isRemote) player.sendStatusMessage(getMode(stack).translation, true);
			return new ActionResult<>(ActionResultType.SUCCESS, stack);
		}
		return super.useModifierInAir(world, player, hand);
	}

	@Override
	public void onCreated(ItemStack stack, World world, PlayerEntity player)
	{
		super.onCreated(stack, world, player);
		getMode(stack);
	}

	@Override
	public ItemStack getDefaultInstance()
	{
		ItemStack stack = super.getDefaultInstance();
		getMode(stack);
		return stack;
	}

	@Override
	public void fillItemGroup(ItemGroup itemGroup, NonNullList<ItemStack> stackList)
	{
		if (this.isInGroup(itemGroup)) stackList.add(getDefaultInstance());
	}

	private MutatorModes getMode(ItemStack stack)
	{
		CompoundNBT tag = stack.getOrCreateTag();
		if (!tag.contains("mode", 1)) tag.putByte("mode", (byte) 0);
		return MutatorModes.values()[tag.getByte("mode")];
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag)
	{
		tooltip.add(new TranslationTextComponent("tooltip.expandedstorage.tool_mode", getMode(stack).translation).applyTextStyle(TextFormatting.GRAY));
		super.addInformation(stack, world, tooltip, flag);
	}
}