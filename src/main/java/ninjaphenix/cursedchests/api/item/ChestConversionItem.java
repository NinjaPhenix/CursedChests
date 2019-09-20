package ninjaphenix.cursedchests.api.item;

import ninjaphenix.cursedchests.api.CursedChestRegistry;
import ninjaphenix.cursedchests.api.block.CursedChestBlock;
import ninjaphenix.cursedchests.api.block.CursedChestType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class ChestConversionItem extends ChestModifierItem
{
    private Identifier from, to;

    public ChestConversionItem(Identifier from, Identifier to)
    {
        super(new Item.Settings().itemGroup(ItemGroup.TOOLS).stackSize(16));
        this.from = from;
        this.to = to;
    }

    public ChestConversionItem(CursedChestBlock from, CursedChestBlock to)
    {
        this(Registry.BLOCK.getId(from), Registry.BLOCK.getId(to));
    }

    private void upgradeCursedChest(World world, BlockPos pos, BlockState state)
    {
        BlockEntity blockEnity = world.getBlockEntity(pos);
        DefaultedList<ItemStack> inventoryData = DefaultedList.create(CursedChestRegistry.getSlots(to), ItemStack.EMPTY);
        Inventories.fromTag(blockEnity.toTag(new CompoundTag()), inventoryData);
        world.removeBlockEntity(pos);
        world.setBlockState(pos, Registry.BLOCK.get(to).getDefaultState().with(Properties.FACING_HORIZONTAL, state.get(Properties.FACING_HORIZONTAL))
                                               .with(Properties.WATERLOGGED, state.get(Properties.WATERLOGGED))
                                               .with(CursedChestBlock.TYPE, state.get(CursedChestBlock.TYPE)));
        BlockEntity blockEntity = world.getBlockEntity(pos);
        blockEntity.fromTag(Inventories.toTag(blockEntity.toTag(new CompoundTag()), inventoryData));
    }

    private void upgradeChest(World world, BlockPos pos, BlockState state)
    {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        DefaultedList<ItemStack> inventoryData = DefaultedList.create(CursedChestRegistry.getSlots(to), ItemStack.EMPTY);
        Inventories.fromTag(blockEntity.toTag(new CompoundTag()), inventoryData);
        world.removeBlockEntity(pos);
        world.setBlockState(pos, Registry.BLOCK.get(to).getDefaultState().with(Properties.FACING_HORIZONTAL, state.get(Properties.FACING_HORIZONTAL))
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
        if (Registry.BLOCK.getId(mainState.getBlock()) != from) return ActionResult.FAIL;
        ItemStack handStack = player.getStackInHand(context.getHand());
        if (otherBlockPos == null || (handStack.getAmount() == 1 && !player.isCreative()))
        {
            if (!world.isClient)
            {
                upgradeCursedChest(world, mainBlockPos, mainState);
                handStack.subtractAmount(1);
            }
            return ActionResult.SUCCESS;
        }
        else
        {
            if (!world.isClient)
            {
                upgradeCursedChest(world, otherBlockPos, world.getBlockState(otherBlockPos));
                upgradeCursedChest(world, mainBlockPos, mainState);
                handStack.subtractAmount(2);
            }
            return ActionResult.SUCCESS;
        }
    }

    @Override
    protected ActionResult useModifierOnBlock(ItemUsageContext context, BlockState state)
    {
        if (state.getBlock() == Blocks.CHEST && from.equals(new Identifier("cursedchests", "wood_chest")))
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
                    handStack.subtractAmount(1);
                }
            }
            else
            {
                BlockPos otherPos;
                if (state.get(Properties.CHEST_TYPE) == ChestType.RIGHT)
                    otherPos = mainpos.offset(state.get(Properties.FACING_HORIZONTAL).rotateYCounterclockwise());
                else if (state.get(Properties.CHEST_TYPE) == ChestType.LEFT)
                    otherPos = mainpos.offset(state.get(Properties.FACING_HORIZONTAL).rotateYClockwise());
                else return ActionResult.FAIL;
                if (!world.isClient)
                {
                    upgradeChest(world, otherPos, world.getBlockState(otherPos));
                    upgradeChest(world, mainpos, state);
                    handStack.subtractAmount(2);
                }
            }
            return ActionResult.SUCCESS;
        }
        return super.useModifierOnBlock(context, state);
    }
}
