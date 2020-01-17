package ninjaphenix.expandedstorage.client.render;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;
import ninjaphenix.expandedstorage.ModContent;
import ninjaphenix.expandedstorage.api.Registries;
import ninjaphenix.expandedstorage.api.block.CursedChestBlock;
import ninjaphenix.expandedstorage.api.block.entity.CursedChestTileEntity;
import ninjaphenix.expandedstorage.api.block.enums.CursedChestType;
import ninjaphenix.expandedstorage.client.model.LongChestModel;
import ninjaphenix.expandedstorage.client.model.SingleChestModel;
import ninjaphenix.expandedstorage.client.model.TallChestModel;
import ninjaphenix.expandedstorage.client.model.VanillaChestModel;

@OnlyIn(Dist.CLIENT)
public class CursedChestRenderer extends TileEntityRenderer<CursedChestTileEntity>
{
	private static final SingleChestModel singleChestModel = new SingleChestModel();
	private static final SingleChestModel tallChestModel = new TallChestModel();
	private static final SingleChestModel vanillaChestModel = new VanillaChestModel();
	private static final SingleChestModel longChestModel = new LongChestModel();
	private static final BlockState defaultState = ModContent.WOOD_CHEST.getFirst().getDefaultState().with(CursedChestBlock.FACING, Direction.SOUTH)
																		.with(CursedChestBlock.TYPE, CursedChestType.SINGLE);

	@Override
	public void render(CursedChestTileEntity blockEntity, double x, double y, double z, float lidPitch, int breaking_stage)
	{
		BlockState state = blockEntity.hasWorld() ? blockEntity.getBlockState() : defaultState;
		CursedChestType chestType = state.get(CursedChestBlock.TYPE);
		if (!chestType.isRenderedType() && breaking_stage < 0) return;
		ResourceLocation b = blockEntity.getBlock();
		if (b == null) b = ForgeRegistries.BLOCKS.getKey(ModContent.WOOD_CHEST.getFirst());
		SingleChestModel chestModel = getChestModelAndBindTexture(b, breaking_stage, chestType);
		if (chestModel == null) return;
		GlStateManager.enableDepthTest();
		GlStateManager.depthFunc(515);
		GlStateManager.depthMask(true);
		if (breaking_stage >= 0)
		{
			GlStateManager.matrixMode(5890);
			GlStateManager.pushMatrix();
			if (chestType == CursedChestType.FRONT || chestType == CursedChestType.BACK) GlStateManager.scalef(6, 6, 1);
			else if (chestType == CursedChestType.BOTTOM || chestType == CursedChestType.TOP) GlStateManager.scalef(4, 8, 1);
			else if (chestType == CursedChestType.LEFT || chestType == CursedChestType.RIGHT) GlStateManager.scalef(8, 4, 1);
			else GlStateManager.scalef(4, 4, 1);
			GlStateManager.translatef(0.0625F, 0.0625F, 0.0625F);
			GlStateManager.matrixMode(5888);
		}
		else GlStateManager.color4f(1, 1, 1, 1);
		GlStateManager.pushMatrix();
		GlStateManager.enableRescaleNormal();
		GlStateManager.translated(x, y + 1, z + 1);
		GlStateManager.scalef(1, -1, -1);
		float chestYaw = state.get(CursedChestBlock.FACING).getHorizontalAngle();
		if (Math.abs(chestYaw) > 1.0E-5D)
		{
			GlStateManager.translated(0.5, 0.5, 0.5);
			GlStateManager.rotatef(chestYaw, 0, 1, 0);
			GlStateManager.translated(-0.5, -0.5, -0.5);
		}
		if (chestType == CursedChestType.TOP) GlStateManager.translatef(0, 1, 0);
		else if (chestType == CursedChestType.RIGHT) GlStateManager.translatef(-1, 0, 0);
		else if (chestType == CursedChestType.BACK) GlStateManager.translatef(0, 0, -1);
		setLidPitch(blockEntity, lidPitch, chestModel);
		chestModel.render();
		GlStateManager.disableRescaleNormal();
		GlStateManager.popMatrix();
		GlStateManager.color4f(1, 1, 1, 1);
		if (breaking_stage >= 0)
		{
			GlStateManager.matrixMode(5890);
			GlStateManager.popMatrix();
			GlStateManager.matrixMode(5888);
		}
	}

	private SingleChestModel getChestModelAndBindTexture(ResourceLocation tier, int breaking_stage, CursedChestType chestType)
	{
		bindTexture(breaking_stage >= 0 ? DESTROY_STAGES[breaking_stage] : Registries.MODELED.getValue(tier).get().getChestTexture(chestType));
		if (chestType == CursedChestType.BOTTOM || chestType == CursedChestType.TOP) return tallChestModel;
		else if (chestType == CursedChestType.FRONT || chestType == CursedChestType.BACK) return longChestModel;
		else if (chestType == CursedChestType.LEFT || chestType == CursedChestType.RIGHT) return vanillaChestModel;
		else if (chestType == CursedChestType.SINGLE) return singleChestModel;
		return null;
	}

	private void setLidPitch(CursedChestTileEntity tileEntity, float lidPitch, SingleChestModel model)
	{
		float newPitch = 1.0F - tileEntity.getLidAngle(lidPitch);
		model.setLidPitch(-((1.0F - newPitch * newPitch * newPitch) * 1.5707964F));
	}
}