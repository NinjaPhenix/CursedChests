package ninjaphenix.expandedstorage.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import ninjaphenix.expandedstorage.ModContent;
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

	public CursedChestRenderer(TileEntityRendererDispatcher p_i226006_1_) { super(p_i226006_1_); }

	@Override
	public void func_225616_a_(CursedChestTileEntity te, float v, MatrixStack stack, IRenderTypeBuffer buffer, int x, int y)
	{
		BlockState state = te.hasWorld() ? te.getBlockState() : defaultState;
		CursedChestType chestType = state.get(CursedChestBlock.TYPE);
		SingleChestModel model = singleChestModel;
		if (chestType == CursedChestType.BOTTOM || chestType == CursedChestType.TOP) model = tallChestModel;
		else if (chestType == CursedChestType.FRONT || chestType == CursedChestType.BACK) model = longChestModel;
		else if (chestType == CursedChestType.LEFT || chestType == CursedChestType.RIGHT) model = vanillaChestModel;
		stack.func_227860_a_();
		stack.func_227861_a_(0.5D, 0.5D, 0.5D);
		stack.func_227863_a_(Vector3f.field_229181_d_.func_229187_a_(-state.get(BlockStateProperties.HORIZONTAL_FACING).getHorizontalAngle()));
		stack.func_227861_a_(-0.5D, -0.5D, -0.5D);
		model.setLidPitch(te.getLidAngle(v));
		if (chestType == CursedChestType.BACK) stack.func_227861_a_(0.0D, 0.0D, 1.0D);
		else if (chestType == CursedChestType.TOP) stack.func_227861_a_(0.0D, -1.0D, 0.0D);
		else if (chestType == CursedChestType.RIGHT) stack.func_227861_a_(-1.0D, 0.0D, 0.0D);
		//model.render(stack, new Material(ExpandedStorage.getRl("chest_atlas_texture"),
		//		Registries.MODELED.getValue(te.getBlock()).get().getChestTexture(chestType)).func_229311_a_(buffer, RenderType::func_228638_b_), x, y);
		stack.func_227865_b_();
	}
}