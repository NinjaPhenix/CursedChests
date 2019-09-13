package ninjaphenix.expandedstorage.item;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.EnderChestBlock;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import ninjaphenix.expandedstorage.api.block.AbstractChestBlock;
import ninjaphenix.expandedstorage.api.block.CursedChestBlock;
import ninjaphenix.expandedstorage.api.block.enums.CursedChestType;
import ninjaphenix.expandedstorage.api.item.ChestModifierItem;

import java.util.List;

public class ChestMutatorItem extends ChestModifierItem
{
    private static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    private static final EnumProperty<CursedChestType> TYPE = AbstractChestBlock.TYPE;

    ChestMutatorItem() { super(new Item.Settings().maxCount(1).group(ItemGroup.TOOLS)); }

    @Override
    protected ActionResult useModifierOnChestBlock(ItemUsageContext context, BlockState mainState, BlockPos mainBlockPos, BlockState otherState,
            BlockPos otherBlockPos)
    {
        PlayerEntity player = context.getPlayer();
        World world = context.getWorld();
        ItemStack stack = context.getStack();
        switch (getMode(stack))
        {
            case MERGE:
                CompoundTag tag = stack.getOrCreateTag();
                if (tag.containsKey("pos"))
                {
                    if (mainState.get(TYPE) == CursedChestType.SINGLE)
                    {
                        BlockPos pos = TagHelper.deserializeBlockPos(tag.getCompound("pos"));
                        BlockState realOtherState = world.getBlockState(pos);
                        if (realOtherState.getBlock() == mainState.getBlock() && realOtherState.get(FACING) == mainState.get(FACING) &&
                                realOtherState.get(TYPE) == CursedChestType.SINGLE)
                        {
                            if (!world.isClient)
                            {
                                BlockPos vec = pos.subtract(mainBlockPos);
                                int sum = vec.getX() + vec.getY() + vec.getZ();
                                if (sum == 1 || sum == -1)
                                {
                                    CursedChestType mainChestType = CursedChestBlock
                                            .getChestType(mainState.get(FACING), Direction.fromVector(vec.getX(), vec.getY(), vec.getZ()));
                                    world.setBlockState(mainBlockPos, mainState.with(TYPE, mainChestType));
                                    world.setBlockState(pos, world.getBlockState(pos).with(TYPE, mainChestType.getOpposite()));
                                    tag.remove("pos");
                                    player.addChatMessage(new TranslatableText("tooltip.expandedstorage.chest_mutator.merge_end"), true);
                                    player.getItemCooldownManager().set(this, 5);
                                    return ActionResult.SUCCESS;
                                }

                            }
                        }
                        return ActionResult.FAIL;
                    }
                }
                else
                {
                    if (mainState.get(TYPE) == CursedChestType.SINGLE)
                    {
                        tag.put("pos", TagHelper.serializeBlockPos(mainBlockPos));
                        player.addChatMessage(new TranslatableText("tooltip.expandedstorage.chest_mutator.merge_start"), true);
                        player.getItemCooldownManager().set(this, 5);
                        return ActionResult.SUCCESS;
                    }

                }
                break;
            case UNMERGE:
                if (otherState != null)
                {
                    if (!world.isClient)
                    {
                        world.setBlockState(mainBlockPos, world.getBlockState(mainBlockPos).with(TYPE, CursedChestType.SINGLE));
                        world.setBlockState(otherBlockPos, world.getBlockState(otherBlockPos).with(TYPE, CursedChestType.SINGLE));
                    }
                    player.getItemCooldownManager().set(this, 5);
                    return ActionResult.SUCCESS;
                }
                break;
            case ROTATE:
                switch (mainState.get(CursedChestBlock.TYPE))
                {
                    case SINGLE:
                        if (!world.isClient)
                        {
                            world.setBlockState(mainBlockPos, mainState.with(FACING, mainState.get(FACING).rotateYClockwise()));
                        }
                        player.getItemCooldownManager().set(this, 5);
                        return ActionResult.SUCCESS;
                    case FRONT:
                    case BACK:
                    case LEFT:
                    case RIGHT:
                        if (!world.isClient)
                        {
                            world.setBlockState(mainBlockPos,
                                    mainState.with(FACING, mainState.get(FACING).getOpposite()).with(TYPE, mainState.get(TYPE).getOpposite()));
                            world.setBlockState(otherBlockPos,
                                    otherState.with(FACING, otherState.get(FACING).getOpposite()).with(TYPE, otherState.get(TYPE).getOpposite()));
                        }
                        player.getItemCooldownManager().set(this, 5);
                        return ActionResult.SUCCESS;
                    case TOP:
                    case BOTTOM:
                        if (!world.isClient)
                        {
                            world.setBlockState(mainBlockPos, mainState.with(FACING, mainState.get(FACING).rotateYClockwise()));
                            world.setBlockState(otherBlockPos, otherState.with(FACING, otherState.get(FACING).rotateYClockwise()));
                        }
                        player.getItemCooldownManager().set(this, 5);
                        return ActionResult.SUCCESS;
                }
        }
        return ActionResult.FAIL;
    }

    @Override
    protected ActionResult useModifierOnBlock(ItemUsageContext context, BlockState state)
    {
        PlayerEntity player = context.getPlayer();
        ItemStack stack = context.getStack();
        World world = context.getWorld();
        BlockPos mainPos = context.getBlockPos();
        MutatorModes mode = getMode(stack);
        if (state.getBlock() instanceof ChestBlock)
        {
            if (mode == MutatorModes.MERGE)
            {
                //Direction direction = context.getSide();
                //BlockPos otherPos = mainPos.offset(direction);
                //BlockState otherState = world.getBlockState(otherPos);
                //Direction facing = state.get(ChestBlock.FACING);
                //ChestType type;
                //if (state.getBlock() == otherState.getBlock() && facing == otherState.get(ChestBlock.FACING) &&
                //        state.get(ChestBlock.CHEST_TYPE) == ChestType.SINGLE && otherState.get(ChestBlock.CHEST_TYPE) == ChestType.SINGLE)
                //{
                //    if (facing.rotateYCounterclockwise() == direction)
                //    {
                //        type = ChestType.RIGHT;
                //    }
                //    else if (facing.rotateYClockwise() == direction)
                //    {
                //        type = ChestType.LEFT;
                //    }
                //    else
                //    {
                //        CursedChestType cursedType;
                //        if (direction == Direction.UP) cursedType = CursedChestType.BOTTOM;
                //        else if (direction == Direction.DOWN) cursedType = CursedChestType.TOP;
                //        else if (facing == direction) cursedType = CursedChestType.BACK;
                //        else if (facing == direction.getOpposite()) cursedType = CursedChestType.FRONT;
                //        else return ActionResult.FAIL;
                //        if (!world.isClient)
                //        {
                //            CompoundTag tag = world.getBlockEntity(mainPos).toTag(new CompoundTag());
                //            ListTag items = tag.getList("Items", 10);
                //            CompoundTag otherTag = world.getBlockEntity(otherPos).toTag(new CompoundTag());
                //            ListTag otherItems = otherTag.getList("Items", 10);
                //            world.removeBlockEntity(mainPos);
                //            world.setBlockState(mainPos,
                //                    ModBlocks.wood_chest.getDefaultState().with(CursedChestBlock.TYPE, cursedType).with(CursedChestBlock.FACING, facing)
                //                                        .with(CursedChestBlock.WATERLOGGED, state.get(ChestBlock.WATERLOGGED)));
                //            BlockEntity blockEntity = world.getBlockEntity(mainPos);
                //            tag = blockEntity.toTag(new CompoundTag());
                //            tag.put("Items", items);
                //            blockEntity.fromTag(tag);
                //            world.removeBlockEntity(otherPos);
                //            world.setBlockState(otherPos, ModBlocks.wood_chest.getDefaultState().with(CursedChestBlock.TYPE, cursedType.getOpposite())
                //                                                              .with(CursedChestBlock.FACING, facing)
                //                                                              .with(CursedChestBlock.WATERLOGGED, otherState.get(ChestBlock.WATERLOGGED)));
                //            BlockEntity otherBlockEntity = world.getBlockEntity(otherPos);
                //            otherTag = otherBlockEntity.toTag(new CompoundTag());
                //            otherTag.put("Items", otherItems);
                //            otherBlockEntity.fromTag(otherTag);
                //        }
                //        player.getItemCooldownManager().set(this, 5);
                //        return ActionResult.SUCCESS;
                //    }
                //    if (!world.isClient)
                //    {
                //        world.setBlockState(mainPos, state.with(ChestBlock.CHEST_TYPE, type));
                //        world.setBlockState(otherPos, otherState.with(ChestBlock.CHEST_TYPE, type.getOpposite()));
                //    }
                //    player.getItemCooldownManager().set(this, 5);
                //    return ActionResult.SUCCESS;
                //}
                //return ActionResult.FAIL;
            }
            else if (mode == MutatorModes.UNMERGE)
            {
                BlockPos otherPos;
                switch (state.get(ChestBlock.CHEST_TYPE))
                {
                    case LEFT:
                        otherPos = mainPos.offset(state.get(ChestBlock.FACING).rotateYClockwise());
                        break;
                    case RIGHT:
                        otherPos = mainPos.offset(state.get(ChestBlock.FACING).rotateYCounterclockwise());
                        break;
                    default:
                        return ActionResult.FAIL;
                }
                if (!world.isClient)
                {
                    world.setBlockState(mainPos, state.with(ChestBlock.CHEST_TYPE, ChestType.SINGLE));
                    world.setBlockState(otherPos, world.getBlockState(otherPos).with(ChestBlock.CHEST_TYPE, ChestType.SINGLE));
                }
                player.getItemCooldownManager().set(this, 5);
                return ActionResult.SUCCESS;
            }
            else if (mode == MutatorModes.ROTATE)
            {
                BlockPos otherPos;
                switch (state.get(ChestBlock.CHEST_TYPE))
                {
                    case LEFT:
                        otherPos = mainPos.offset(state.get(ChestBlock.FACING).rotateYClockwise());
                        break;
                    case RIGHT:
                        otherPos = mainPos.offset(state.get(ChestBlock.FACING).rotateYCounterclockwise());
                        break;
                    case SINGLE:
                        if (!world.isClient) world.setBlockState(mainPos, state.rotate(BlockRotation.CLOCKWISE_90));
                        player.getItemCooldownManager().set(this, 5);
                        return ActionResult.SUCCESS;
                    default:
                        return ActionResult.FAIL;
                }
                if (!world.isClient)
                {
                    BlockState otherState = world.getBlockState(otherPos);
                    world.setBlockState(mainPos, state.rotate(BlockRotation.CLOCKWISE_180)
                                                      .with(ChestBlock.CHEST_TYPE, state.get(ChestBlock.CHEST_TYPE).getOpposite()));
                    world.setBlockState(otherPos, otherState.rotate(BlockRotation.CLOCKWISE_180)
                                                            .with(ChestBlock.CHEST_TYPE, otherState.get(ChestBlock.CHEST_TYPE).getOpposite()));
                }
                player.getItemCooldownManager().set(this, 5);
                return ActionResult.SUCCESS;
            }
        }
        else if (state.getBlock() == Blocks.ENDER_CHEST)
        {
            if (getMode(stack) == MutatorModes.ROTATE)
            {
                if (!world.isClient) world.setBlockState(mainPos, state.rotate(BlockRotation.CLOCKWISE_90));
                player.getItemCooldownManager().set(this, 5);
                return ActionResult.SUCCESS;
            }
            return ActionResult.FAIL;
        }
        return super.useModifierOnBlock(context, state);
    }

    @Override
    protected TypedActionResult<ItemStack> useModifierInAir(World world, PlayerEntity player, Hand hand)
    {
        if (player.isSneaking())
        {
            ItemStack stack = player.getStackInHand(hand);
            byte mode = (byte) getMode(stack).ordinal();
            if (++mode >= MutatorModes.values().length) mode = 0;
            CompoundTag tag = stack.getOrCreateTag();
            tag.putByte("mode", mode);
            if (tag.containsKey("pos")) tag.remove("pos");
            if (!world.isClient) player.addChatMessage(getMode(stack).translation, true);
            return new TypedActionResult<>(ActionResult.SUCCESS, stack, false);
        }
        return super.useModifierInAir(world, player, hand);
    }

    @Override
    public void onCraft(ItemStack stack, World world, PlayerEntity player)
    {
        super.onCraft(stack, world, player);
        getMode(stack);
    }

    @Override
    public ItemStack getStackForRender()
    {
        ItemStack stack = super.getStackForRender();
        getMode(stack);
        return stack;
    }

    @Override
    public void appendStacks(ItemGroup itemGroup, DefaultedList<ItemStack> stackList)
    {
        if (this.isIn(itemGroup)) stackList.add(getStackForRender());
    }

    private MutatorModes getMode(ItemStack stack)
    {
        CompoundTag tag = stack.getOrCreateTag();
        if (tag.containsKey("mode", 1)) return MutatorModes.values()[tag.getByte("mode")];
        else
        {
            tag.putByte("mode", (byte) 0);
            return MutatorModes.ROTATE;
        }
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context)
    {
        tooltip.add(new TranslatableText("tooltip.expandedstorage.tool_mode", getMode(stack).translation).formatted(Formatting.GRAY));
        super.appendTooltip(stack, world, tooltip, context);
    }
}
