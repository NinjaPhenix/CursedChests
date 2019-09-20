package ninjaphenix.cursedchests.api.item;

import ninjaphenix.cursedchests.api.block.CursedChestBlock;
import ninjaphenix.cursedchests.api.block.CursedChestType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public abstract class ChestModifierItem extends Item
{
	public ChestModifierItem(Settings settings){ super(settings); }

	@Override public ActionResult useOnBlock(ItemUsageContext context)
	{
		World world = context.getWorld();
		BlockPos pos = context.getBlockPos();
		BlockState state = world.getBlockState(pos);
		if (state.getBlock() instanceof CursedChestBlock)
		{
			ActionResult result = ActionResult.FAIL;
			CursedChestType type = state.get(CursedChestBlock.TYPE);
			Direction facing = state.get(CursedChestBlock.FACING);
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
				BlockPos otherPos = pos.offset(facing.rotateYCounterclockwise());
				result = useModifierOnChestBlock(context, state, pos, world.getBlockState(otherPos), otherPos);
			}
			else if (type == CursedChestType.RIGHT)
			{
				BlockPos otherPos = pos.offset(facing.rotateYClockwise());
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

	@Override public boolean useOnEntity(ItemStack stack, PlayerEntity player, LivingEntity entity, Hand hand)
	{
		return useModifierOnEntity(stack, player, entity, hand);
	}

	@Override public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand)
	{
		TypedActionResult<ItemStack> result = useModifierInAir(world, player, hand);
		if (result.getResult() == ActionResult.SUCCESS) player.getItemCooldownManager().set(this, 5);
		return result;
	}

	protected ActionResult useModifierOnChestBlock(ItemUsageContext context, BlockState mainState, BlockPos mainBlockPos, BlockState otherState, BlockPos otherBlockPos){ return ActionResult.PASS; }

	protected ActionResult useModifierOnBlock(ItemUsageContext context, BlockState state){ return ActionResult.PASS; }

	protected boolean useModifierOnEntity(ItemStack stack, PlayerEntity player, LivingEntity entity, Hand hand){ return false; }

	protected TypedActionResult<ItemStack> useModifierInAir(World world, PlayerEntity player, Hand hand){ return new TypedActionResult<>(ActionResult.PASS, player.getStackInHand(hand)); }
}
