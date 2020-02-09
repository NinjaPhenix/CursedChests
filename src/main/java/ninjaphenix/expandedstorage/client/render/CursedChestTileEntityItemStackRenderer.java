package ninjaphenix.expandedstorage.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemStack;
import ninjaphenix.expandedstorage.api.block.CursedChestBlock;
import ninjaphenix.expandedstorage.api.block.entity.CursedChestTileEntity;

public class CursedChestTileEntityItemStackRenderer extends ItemStackTileEntityRenderer
{
	private static final CursedChestTileEntity cursedChestRenderEntity = new CursedChestTileEntity();
	public static CursedChestRenderer renderer;

	@Override
	public void render(final ItemStack stack, final MatrixStack matrix, final IRenderTypeBuffer buffer, final int x, final int y)
	{
		// todo: eval if these are always my tile entity
		final Block block = Block.getBlockFromItem(stack.getItem());
		if (block instanceof CursedChestBlock)
		{
			cursedChestRenderEntity.setBlock(block.getRegistryName());
			renderer.render(cursedChestRenderEntity, 0, matrix, buffer, x, y);
			//Doesn't work for some reason
			//TileEntityRendererDispatcher.instance.renderNullable(cursedChestRenderEntity, matrix, buffer, x, y);
		}
	}
}
