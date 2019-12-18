package ninjaphenix.expandedstorage.api.block;

import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.registries.ForgeRegistries;
import ninjaphenix.expandedstorage.api.Registries;
import ninjaphenix.expandedstorage.api.block.entity.OldChestTileEntity;

import javax.annotation.Nullable;

public class OldChestBlock extends AbstractChestBlock
{
	public OldChestBlock(Properties properties) { super(properties); }

	@Nullable
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world)
	{
		ResourceLocation blockId = ForgeRegistries.BLOCKS.getKey(this);
		return new OldChestTileEntity(new ResourceLocation(blockId.getNamespace(), blockId.getPath().substring(4)));
	}

	@Override
	public SimpleRegistry<Registries.TierData> getDataRegistry() { return Registries.OLD; }
}