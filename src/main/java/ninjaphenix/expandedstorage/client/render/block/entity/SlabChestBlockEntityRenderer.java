package ninjaphenix.expandedstorage.client.render.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.enums.SlabType;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.state.property.Properties;
import ninjaphenix.expandedstorage.api.Registries;
import ninjaphenix.expandedstorage.api.block.entity.SlabChestBlockEntity;
import ninjaphenix.expandedstorage.block.ModBlocks;
import ninjaphenix.expandedstorage.client.ExpandedStorageClient;
import ninjaphenix.expandedstorage.client.model.SingleChestModel;
import ninjaphenix.expandedstorage.client.model.SlabChestModel;

@Environment(EnvType.CLIENT)
public class SlabChestBlockEntityRenderer extends BlockEntityRenderer<SlabChestBlockEntity>
{
	private static final SingleChestModel singleChestModel = new SingleChestModel();
	private static final SingleChestModel slabChestModel = new SlabChestModel();
	private static final BlockState defaultState = ModBlocks.wood_chest_slab.getDefaultState();

	public SlabChestBlockEntityRenderer(BlockEntityRenderDispatcher dispatcher) { super(dispatcher); }

	@Override
	public void render(SlabChestBlockEntity be, float tickDelta, MatrixStack stack, VertexConsumerProvider vcp, int x, int y)
	{
		BlockState state = be.hasWorld() ? be.getCachedState() : defaultState;
		SlabType type = state.get(Properties.SLAB_TYPE);
		SingleChestModel model = slabChestModel;
		if (type == SlabType.DOUBLE) { model = singleChestModel; }
		stack.push();
		stack.translate(0.5D, 0.5D, 0.5D);
		stack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(-state.get(Properties.HORIZONTAL_FACING).asRotation()));
		stack.translate(-0.5D, -0.5D, -0.5D);
		model.setLidPitch(be.getAnimationProgress(tickDelta));
		if (type == SlabType.TOP) { stack.translate(0.0D, 7.0D / 16.0D, 0.0D); }
		model.render(stack, new SpriteIdentifier(ExpandedStorageClient.CHEST_TEXTURE_ATLAS,
				Registries.SLAB.get(be.getBlock()).getChestTexture(type)).getVertexConsumer(vcp, RenderLayer::getEntityCutout), x, y);
		stack.pop();
	}
}