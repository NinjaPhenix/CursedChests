package ninjaphenix.expandedstorage.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import ninjaphenix.expandedstorage.api.block.CursedChestBlock;
import ninjaphenix.expandedstorage.api.block.entity.CursedChestTileEntity;

public class CursedChestTileEntityItemStackRenderer extends ItemStackTileEntityRenderer
{
	private static final CursedChestTileEntity cursedChestRenderEntity = new CursedChestTileEntity();
	public static CursedChestRenderer renderer;

	@Override
	public void render(ItemStack stack, MatrixStack matrix, IRenderTypeBuffer buffer, int x, int y)
	{
		Block block = Block.getBlockFromItem(stack.getItem());
		if (block instanceof CursedChestBlock)
		{
			ResourceLocation id = ForgeRegistries.BLOCKS.getKey(block);
			cursedChestRenderEntity.setBlock(id);
			renderer.render(cursedChestRenderEntity, 0, matrix, buffer, x, y);
			//Doesn't work for some reason
			//TileEntityRendererDispatcher.instance.renderNullable(cursedChestRenderEntity, matrix, buffer, x, y);
		}
	}
}
