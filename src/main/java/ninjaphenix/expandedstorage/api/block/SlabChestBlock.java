package ninjaphenix.expandedstorage.api.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import ninjaphenix.expandedstorage.api.Registries;
import ninjaphenix.expandedstorage.api.block.base.AbstractSlabChestBlock;
import ninjaphenix.expandedstorage.api.block.entity.SlabChestBlockEntity;

public class SlabChestBlock extends AbstractSlabChestBlock
{
	public SlabChestBlock(Settings settings)
	{
		super(settings);
		DOUBLE_SHAPE = Block.createCuboidShape(1, 0, 1, 15, 14, 15);
		BOTTOM_SHAPE = Block.createCuboidShape(1, 0, 1, 15, 7, 15);
		TOP_SHAPE = Block.createCuboidShape(1, 7, 1, 15, 14, 15);
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) { return BlockRenderType.ENTITYBLOCK_ANIMATED; }

	@Override
	public BlockEntity createBlockEntity(BlockView view)
	{
		final Identifier id = Registry.BLOCK.getId(this);
		return new SlabChestBlockEntity(new Identifier(id.getNamespace(), id.getPath().substring(0, id.getPath().length() - 5)));
	}

	@Override
	public SidedInventory getInventory(BlockState state, IWorld world, BlockPos pos)
	{
		BlockEntity be = world.getBlockEntity(pos);
		if (be instanceof SlabChestBlockEntity) { return (SlabChestBlockEntity) be; }
		return null;
	}

	@Override
	@SuppressWarnings("unchecked")
	public SimpleRegistry<Registries.SlabTierData> getDataRegistry() { return Registries.SLAB; }
}
