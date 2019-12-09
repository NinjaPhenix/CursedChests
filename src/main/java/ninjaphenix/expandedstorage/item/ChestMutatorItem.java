package ninjaphenix.expandedstorage.item;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import ninjaphenix.expandedstorage.ExpandedStorage;
import ninjaphenix.expandedstorage.api.Registries;
import ninjaphenix.expandedstorage.api.block.AbstractChestBlock;
import ninjaphenix.expandedstorage.api.block.CursedChestBlock;
import ninjaphenix.expandedstorage.api.block.enums.CursedChestType;
import ninjaphenix.expandedstorage.api.item.ChestModifierItem;

import java.util.List;

import static net.minecraft.state.property.Properties.WATERLOGGED;
import static net.minecraft.util.BlockRotation.CLOCKWISE_180;
import static net.minecraft.util.BlockRotation.CLOCKWISE_90;

@SuppressWarnings("ConstantConditions")
public class ChestMutatorItem extends ChestModifierItem
{
    private static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    private static final EnumProperty<CursedChestType> TYPE = AbstractChestBlock.TYPE;

    ChestMutatorItem() { super(new Item.Settings().maxCount(1).group(ExpandedStorage.group)); }

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
                if (tag.contains("pos"))
                {
                    if (mainState.get(TYPE) == CursedChestType.SINGLE)
                    {
                        BlockPos pos = NbtHelper.toBlockPos(tag.getCompound("pos"));
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
                        tag.put("pos", NbtHelper.fromBlockPos(mainBlockPos));
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
                        if (!world.isClient) world.setBlockState(mainBlockPos, mainState.rotate(CLOCKWISE_90));
                        player.getItemCooldownManager().set(this, 5);
                        return ActionResult.SUCCESS;
                    case FRONT:
                    case BACK:
                    case LEFT:
                    case RIGHT:
                        if (!world.isClient)
                        {
                            world.setBlockState(mainBlockPos, mainState.rotate(CLOCKWISE_180).with(TYPE, mainState.get(TYPE).getOpposite()));
                            world.setBlockState(otherBlockPos, otherState.rotate(CLOCKWISE_180).with(TYPE, otherState.get(TYPE).getOpposite()));
                        }
                        player.getItemCooldownManager().set(this, 5);
                        return ActionResult.SUCCESS;
                    case TOP:
                    case BOTTOM:
                        if (!world.isClient)
                        {
                            world.setBlockState(mainBlockPos, mainState.rotate(CLOCKWISE_90));
                            world.setBlockState(otherBlockPos, otherState.rotate(CLOCKWISE_90));
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
                CompoundTag tag = stack.getOrCreateTag();
                if (tag.contains("pos"))
                {
                    if (state.get(ChestBlock.CHEST_TYPE) == ChestType.SINGLE)
                    {
                        BlockPos otherPos = NbtHelper.toBlockPos(tag.getCompound("pos"));
                        BlockState realOtherState = world.getBlockState(otherPos);
                        if (realOtherState.getBlock() == state.getBlock() && realOtherState.get(FACING) == state.get(FACING) &&
                                realOtherState.get(ChestBlock.CHEST_TYPE) == ChestType.SINGLE)
                        {
                            BlockPos vec = otherPos.subtract(mainPos);
                            int sum = vec.getX() + vec.getY() + vec.getZ();
                            if (sum == 1 || sum == -1)
                            {
                                if (!world.isClient)
                                {
                                    Registries.TierData entry = Registries.CHEST.get(ExpandedStorage.getId("wood_chest"));
                                    BlockState defState = Registry.BLOCK.get(entry.getBlockId())
                                                                        .getDefaultState().with(FACING, state.get(FACING));
                                    CursedChestType mainChestType = AbstractChestBlock
                                            .getChestType(state.get(FACING), Direction.fromVector(vec.getX(), vec.getY(), vec.getZ()));

                                    BlockEntity blockEntity = world.getBlockEntity(mainPos);
                                    DefaultedList<ItemStack> invData = DefaultedList.ofSize(entry.getSlotCount(), ItemStack.EMPTY);
                                    Inventories.fromTag(blockEntity.toTag(new CompoundTag()), invData);
                                    world.removeBlockEntity(mainPos);
                                    world.setBlockState(mainPos, defState.with(WATERLOGGED, state.get(WATERLOGGED)).with(TYPE, mainChestType));
                                    blockEntity = world.getBlockEntity(mainPos);
                                    blockEntity.fromTag(Inventories.toTag(blockEntity.toTag(new CompoundTag()), invData));

                                    blockEntity = world.getBlockEntity(otherPos);
                                    invData = DefaultedList.ofSize(entry.getSlotCount(), ItemStack.EMPTY);
                                    Inventories.fromTag(blockEntity.toTag(new CompoundTag()), invData);
                                    world.removeBlockEntity(otherPos);
                                    world.setBlockState(otherPos, defState.with(WATERLOGGED, state.get(WATERLOGGED)).with(TYPE, mainChestType.getOpposite()));
                                    blockEntity = world.getBlockEntity(otherPos);
                                    blockEntity.fromTag(Inventories.toTag(blockEntity.toTag(new CompoundTag()), invData));

                                    tag.remove("pos");
                                    player.addChatMessage(new TranslatableText("tooltip.expandedstorage.chest_mutator.merge_end"), true);
                                    player.getItemCooldownManager().set(this, 5);
                                }
                                return ActionResult.SUCCESS;
                            }
                        }
                        return ActionResult.FAIL;
                    }
                }
                else
                {
                    if (state.get(ChestBlock.CHEST_TYPE) == ChestType.SINGLE)
                    {
                        tag.put("pos", NbtHelper.fromBlockPos(mainPos));
                        player.addChatMessage(new TranslatableText("tooltip.expandedstorage.chest_mutator.merge_start"), true);
                        player.getItemCooldownManager().set(this, 5);
                        return ActionResult.SUCCESS;
                    }

                }
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
                        if (!world.isClient) world.setBlockState(mainPos, state.rotate(CLOCKWISE_90));
                        player.getItemCooldownManager().set(this, 5);
                        return ActionResult.SUCCESS;
                    default:
                        return ActionResult.FAIL;
                }
                if (!world.isClient)
                {
                    BlockState otherState = world.getBlockState(otherPos);
                    world.setBlockState(mainPos, state.rotate(CLOCKWISE_180)
                                                      .with(ChestBlock.CHEST_TYPE, state.get(ChestBlock.CHEST_TYPE).getOpposite()));
                    world.setBlockState(otherPos, otherState.rotate(CLOCKWISE_180)
                                                            .with(ChestBlock.CHEST_TYPE, otherState.get(ChestBlock.CHEST_TYPE).getOpposite()));
                }
                player.getItemCooldownManager().set(this, 5);
                return ActionResult.SUCCESS;
            }
        }
        else if (state.getBlock() == Blocks.ENDER_CHEST)
        {
            if (mode == MutatorModes.ROTATE)
            {
                if (!world.isClient) world.setBlockState(mainPos, state.rotate(CLOCKWISE_90));
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
            CompoundTag tag = stack.getOrCreateTag();
            tag.putByte("mode", getMode(stack).next);
            if (tag.contains("pos")) tag.remove("pos");
            if (!world.isClient) player.addChatMessage(getMode(stack).translation, true);
            return TypedActionResult.success(stack);
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
        if (!tag.contains("mode", 1)) tag.putByte("mode", (byte) 0);
        return MutatorModes.values()[tag.getByte("mode")];
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context)
    {
        tooltip.add(new TranslatableText("tooltip.expandedstorage.tool_mode", getMode(stack).translation).formatted(Formatting.GRAY));
        super.appendTooltip(stack, world, tooltip, context);
    }
}
