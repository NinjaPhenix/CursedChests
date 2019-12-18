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

	public ChestModifierItem(Item.Properties properties) { super(properties); }

	@Override
	public ActionResultType onItemUse(ItemUseContext context)
	{
		World world = context.getWorld();
		BlockPos pos = context.getPos();
		BlockState state = world.getBlockState(pos);
		if (state.getBlock() instanceof AbstractChestBlock)
		{
			ActionResultType result = ActionResultType.FAIL;
			CursedChestType type = state.get(TYPE);
			Direction facing = state.get(FACING);
			if (type == CursedChestType.SINGLE) result = useModifierOnChestBlock(context, state, pos, null, null);
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
	public boolean itemInteractionForEntity(ItemStack stack, PlayerEntity player, LivingEntity entity, Hand hand)
	{
		return useModifierOnEntity(stack, player, entity, hand);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand)
	{
		ActionResult<ItemStack> result = useModifierInAir(world, player, hand);
		if (result.getType() == ActionResultType.SUCCESS) player.getCooldownTracker().setCooldown(this, 5);
		return result;
	}

	protected ActionResultType useModifierOnChestBlock(ItemUseContext context, BlockState mainState, BlockPos mainBlockPos, BlockState otherState,
			BlockPos otherBlockPos)
	{ return ActionResultType.PASS; }

	protected ActionResultType useModifierOnBlock(ItemUseContext context, BlockState state) { return ActionResultType.PASS; }

	protected boolean useModifierOnEntity(ItemStack stack, PlayerEntity player, LivingEntity entity, Hand hand) { return false; }

	protected ActionResult<ItemStack> useModifierInAir(World world, PlayerEntity player, Hand hand)
	{
		return ActionResult.newResult(ActionResultType.PASS, player.getHeldItem(hand));
	}
}
