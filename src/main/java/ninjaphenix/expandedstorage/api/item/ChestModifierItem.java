package ninjaphenix.expandedstorage.api.item;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import ninjaphenix.expandedstorage.api.block.AbstractChestBlock;
import ninjaphenix.expandedstorage.api.block.enums.CursedChestType;

public abstract class ChestModifierItem extends Item
{
	private static final DirectionProperty FACING = AbstractChestBlock.FACING;
	private static final EnumProperty<CursedChestType> TYPE = AbstractChestBlock.TYPE;

	public ChestModifierItem(final Item.Properties properties) { super(properties); }

	@Override
	public ActionResultType onItemUse(final ItemUseContext context)
	{
		final World world = context.getWorld();
		final BlockPos pos = context.getPos();
		final BlockState state = world.getBlockState(pos);
		if (state.getBlock() instanceof AbstractChestBlock)
		{
			ActionResultType result = ActionResultType.FAIL;
			final CursedChestType type = state.get(TYPE);
			final Direction facing = state.get(FACING);
			if (type == CursedChestType.SINGLE) { result = useModifierOnChestBlock(context, state, pos, null, null); }
			else if (type == CursedChestType.BOTTOM)
			{
				BlockPos otherPos = pos.offset(Direction.UP);
				result = useModifierOnChestBlock(context, state, pos, world.getBlockState(otherPos), otherPos);
			}
			else if (type == CursedChestType.TOP)
			{
				BlockPos otherPos = pos.offset(Direction.DOWN);
				result = useModifierOnChestBlock(context, world.getBlockState(otherPos), otherPos, state, pos);
			}
			else if (type == CursedChestType.LEFT)
			{
				BlockPos otherPos = pos.offset(facing.rotateYCCW());
				result = useModifierOnChestBlock(context, state, pos, world.getBlockState(otherPos), otherPos);
			}
			else if (type == CursedChestType.RIGHT)
			{
				BlockPos otherPos = pos.offset(facing.rotateY());
				result = useModifierOnChestBlock(context, world.getBlockState(otherPos), otherPos, state, pos);
			}
			else if (type == CursedChestType.FRONT)
			{
				BlockPos otherPos = pos.offset(facing.getOpposite());
				result = useModifierOnChestBlock(context, state, pos, world.getBlockState(otherPos), otherPos);
			}
			else if (type == CursedChestType.BACK)
			{
				BlockPos otherPos = pos.offset(facing);
				result = useModifierOnChestBlock(context, world.getBlockState(otherPos), otherPos, state, pos);
			}
			return result;
		}
		else
		{
			return useModifierOnBlock(context, state);
		}
	}

	@Override
	public boolean itemInteractionForEntity(final ItemStack stack, final PlayerEntity player, final LivingEntity entity, final Hand hand)
	{
		return useModifierOnEntity(stack, player, entity, hand);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(final World world, final PlayerEntity player, final Hand hand)
	{
		final ActionResult<ItemStack> result = useModifierInAir(world, player, hand);
		if (result.getType() == ActionResultType.SUCCESS) { player.getCooldownTracker().setCooldown(this, 5); }
		return result;
	}

	protected ActionResultType useModifierOnChestBlock(final ItemUseContext context, final BlockState mainState, final BlockPos mainBlockPos,
			final BlockState otherState, final BlockPos otherBlockPos)
	{
		return ActionResultType.PASS;
	}

	protected ActionResultType useModifierOnBlock(final ItemUseContext context, final BlockState state) { return ActionResultType.PASS; }

	protected boolean useModifierOnEntity(final ItemStack stack, final PlayerEntity player, final LivingEntity entity, final Hand hand) { return false; }

	protected ActionResult<ItemStack> useModifierInAir(final World world, final PlayerEntity player, final Hand hand)
	{
		return ActionResult.func_226250_c_(player.getHeldItem(hand));
	}
}
