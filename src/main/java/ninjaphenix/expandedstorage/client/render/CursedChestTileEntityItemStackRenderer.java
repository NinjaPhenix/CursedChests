package ninjaphenix.expandedstorage.client.render;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import ninjaphenix.expandedstorage.api.block.CursedChestBlock;
import ninjaphenix.expandedstorage.api.block.entity.CursedChestTileEntity;

public class CursedChestTileEntityItemStackRenderer extends ItemStackTileEntityRenderer
{

	private static final CursedChestTileEntity cursedChestRenderEntity = new CursedChestTileEntity();

	@Override
	public void renderByItem(ItemStack stack)
	{
		Block block = Block.getBlockFromItem(stack.getItem());
		if (block instanceof CursedChestBlock)
		{
			ResourceLocation id = ForgeRegistries.BLOCKS.getKey(block);
			cursedChestRenderEntity.setBlock(id);
			TileEntityRendererDispatcher.instance.renderAsItem(cursedChestRenderEntity);
		}
		else
		{
			super.renderByItem(stack);
		}
	}
}
