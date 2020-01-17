package ninjaphenix.expandedstorage.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.IRenderTypeBuffer;
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
	public void func_228364_a_(ItemStack stack, MatrixStack matrix, IRenderTypeBuffer buffer, int x, int y)
	{
		Block block = Block.getBlockFromItem(stack.getItem());
		if (block instanceof CursedChestBlock)
		{
			ResourceLocation id = ForgeRegistries.BLOCKS.getKey(block);
			cursedChestRenderEntity.setBlock(id);
			TileEntityRendererDispatcher.instance.func_228852_a_(cursedChestRenderEntity, matrix, buffer, x, y);
		}
		else
		{
			super.func_228364_a_(stack, matrix, buffer, x, y);
		}
	}
}
