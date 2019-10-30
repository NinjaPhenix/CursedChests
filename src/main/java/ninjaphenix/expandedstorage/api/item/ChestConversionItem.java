package ninjaphenix.expandedstorage.api.item;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Waterloggable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.World;
import ninjaphenix.expandedstorage.ExpandedStorage;
import ninjaphenix.expandedstorage.api.Registries;
import ninjaphenix.expandedstorage.api.block.AbstractChestBlock;
import ninjaphenix.expandedstorage.api.block.CursedChestBlock;
import ninjaphenix.expandedstorage.api.block.entity.AbstractChestBlockEntity;
import ninjaphenix.expandedstorage.api.block.enums.CursedChestType;

public class ChestConversionItem extends ChestModifierItem
{
    private Identifier from, to;

    public ChestConversionItem(Identifier from, Identifier to)
    {
        super(new Item.Settings().group(ExpandedStorage.group).maxCount(16));
        this.from = from;
        this.to = to;
    }

    private void upgradeCursedChest(World world, BlockPos pos, BlockState state)
    {
        AbstractChestBlockEntity blockEntity = (AbstractChestBlockEntity) world.getBlockEntity(pos);
        final SimpleRegistry<Registries.TierData> registry = ((AbstractChestBlock) state.getBlock()).getDataRegistry();
        DefaultedList<ItemStack> inventoryData = DefaultedList.ofSize(registry.get(to).getSlotCount(), ItemStack.EMPTY);
        Inventories.fromTag(blockEntity.toTag(new CompoundTag()), inventoryData);
        world.removeBlockEntity(pos);
        BlockState newState = Registry.BLOCK.get(registry.get(to).getBlockId()).getDefaultState();
        if (newState.getBlock() instanceof Waterloggable) newState = newState.with(Properties.WATERLOGGED, state.get(Properties.WATERLOGGED));
        world.setBlockState(pos, newState.with(Properties.HORIZONTAL_FACING, state.get(Properties.HORIZONTAL_FACING))
                                         .with(CursedChestBlock.TYPE, state.get(CursedChestBlock.TYPE)));
        blockEntity = (AbstractChestBlockEntity) world.getBlockEntity(pos);
        blockEntity.fromTag(Inventories.toTag(blockEntity.toTag(new CompoundTag()), inventoryData));
    }

    private void upgradeChest(World world, BlockPos pos, BlockState state)
    {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        DefaultedList<ItemStack> inventoryData = DefaultedList.ofSize(Registries.MODELED.get(from).getSlotCount(), ItemStack.EMPTY);
        Inventories.fromTag(blockEntity.toTag(new CompoundTag()), inventoryData);
        world.removeBlockEntity(pos);
        BlockState newState = Registry.BLOCK.get(Registries.MODELED.get(to).getBlockId()).getDefaultState();
        world.setBlockState(pos, newState.with(Properties.HORIZONTAL_FACING, state.get(Properties.HORIZONTAL_FACING))
                                         .with(Properties.WATERLOGGED, state.get(Properties.WATERLOGGED))
                                         .with(CursedChestBlock.TYPE, CursedChestType.valueOf(state.get(Properties.CHEST_TYPE))));
        blockEntity = world.getBlockEntity(pos);
        blockEntity.fromTag(Inventories.toTag(blockEntity.toTag(new CompoundTag()), inventoryData));
    }

    @Override
    protected ActionResult useModifierOnChestBlock(ItemUsageContext context, BlockState mainState, BlockPos mainBlockPos, BlockState otherState,
            BlockPos otherBlockPos)
    {
        World world = context.getWorld();
        PlayerEntity player = context.getPlayer();
        AbstractChestBlock chestBlock = (AbstractChestBlock) mainState.getBlock();
        if (Registry.BLOCK.getId(chestBlock) != chestBlock.getDataRegistry().get(from).getBlockId()) return ActionResult.FAIL;
        ItemStack handStack = player.getStackInHand(context.getHand());
        if (otherBlockPos == null)
        {
            if (!world.isClient)
            {
                upgradeCursedChest(world, mainBlockPos, mainState);
                handStack.decrement(1);
            }
            return ActionResult.SUCCESS;
        }
        else if (handStack.getCount() > 1 || player.isCreative())
        {
            if (!world.isClient)
            {
                upgradeCursedChest(world, otherBlockPos, world.getBlockState(otherBlockPos));
                upgradeCursedChest(world, mainBlockPos, mainState);
                handStack.decrement(2);
            }
            return ActionResult.SUCCESS;
        }
        return ActionResult.FAIL;
    }

    @Override
    protected ActionResult useModifierOnBlock(ItemUsageContext context, BlockState state)
    {
        if (state.getBlock() == Blocks.CHEST && from.equals(ExpandedStorage.getId("wood_chest")))
        {
            World world = context.getWorld();
            BlockPos mainpos = context.getBlockPos();
            PlayerEntity player = context.getPlayer();
            ItemStack handStack = player.getStackInHand(context.getHand());
            if (state.get(Properties.CHEST_TYPE) == ChestType.SINGLE)
            {
                if (!world.isClient)
                {
                    upgradeChest(world, mainpos, state);
                    handStack.decrement(1);
                }
                return ActionResult.SUCCESS;
            }
            else if (handStack.getCount() > 1 || player.isCreative())
            {
                BlockPos otherPos;
                if (state.get(Properties.CHEST_TYPE) == ChestType.RIGHT)
                    otherPos = mainpos.offset(state.get(Properties.HORIZONTAL_FACING).rotateYCounterclockwise());
                else if (state.get(Properties.CHEST_TYPE) == ChestType.LEFT)
                    otherPos = mainpos.offset(state.get(Properties.HORIZONTAL_FACING).rotateYClockwise());
                else return ActionResult.FAIL;
                if (!world.isClient)
                {
                    upgradeChest(world, otherPos, world.getBlockState(otherPos));
                    upgradeChest(world, mainpos, state);
                    handStack.decrement(2);
                }
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.FAIL;
    }
}