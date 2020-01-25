package ninjaphenix.expandedstorage.api.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.SlabType;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.BlockView;
import net.minecraft.world.IWorld;
import ninjaphenix.expandedstorage.api.Registries;
import ninjaphenix.expandedstorage.api.block.base.AbstractSlabChestBlock;
import ninjaphenix.expandedstorage.api.block.entity.OldSlabChestBlockEntity;

import static net.minecraft.state.property.Properties.SLAB_TYPE;

@SuppressWarnings("deprecation")
public class OldSlabChestBlock extends AbstractSlabChestBlock
{

	public OldSlabChestBlock(Settings settings) { super(settings); }

	@Override
	public BlockEntity createBlockEntity(BlockView view)
	{
		final Identifier id = Registry.BLOCK.getId(this);
		return new OldSlabChestBlockEntity(new Identifier(id.getNamespace(), id.getPath().substring(4, id.getPath().length() - 5)));
	}

	@Override
	public boolean hasSidedTransparency(BlockState state) { return state.get(SLAB_TYPE) != SlabType.DOUBLE; }

	@Override
	@SuppressWarnings("unchecked")
	public SimpleRegistry<Registries.TierData> getDataRegistry() { return Registries.OLD_SLAB; }

	@Override
	public SidedInventory getInventory(BlockState state, IWorld world, BlockPos pos)
	{
		BlockEntity be = world.getBlockEntity(pos);
		if (be instanceof OldSlabChestBlockEntity) { return (OldSlabChestBlockEntity) be; }
		return null;
	}
}
