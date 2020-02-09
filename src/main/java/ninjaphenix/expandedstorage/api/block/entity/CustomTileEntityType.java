package ninjaphenix.expandedstorage.api.block.entity;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

import java.util.function.Predicate;
import java.util.function.Supplier;

public class CustomTileEntityType<T extends TileEntity> extends TileEntityType<T>
{
	Predicate<Block> predicate;

	public CustomTileEntityType(Supplier<? extends T> supplier_1, Predicate<Block> supports)
	{
		super(supplier_1, null, null);
		predicate = supports;
	}

	@Override
	public boolean isValidBlock(final Block block) { return predicate.test(block); }
}
