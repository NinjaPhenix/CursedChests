package ninjaphenix.expandedstorage.client.render.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.state.property.Properties;
import ninjaphenix.expandedstorage.api.Registries;
import ninjaphenix.expandedstorage.api.block.CursedChestBlock;
import ninjaphenix.expandedstorage.api.block.entity.CursedChestBlockEntity;
import ninjaphenix.expandedstorage.api.block.enums.CursedChestType;
import ninjaphenix.expandedstorage.block.ModBlocks;
import ninjaphenix.expandedstorage.client.ExpandedStorageClient;
import ninjaphenix.expandedstorage.client.model.LongChestModel;
import ninjaphenix.expandedstorage.client.model.SingleChestModel;
import ninjaphenix.expandedstorage.client.model.TallChestModel;
import ninjaphenix.expandedstorage.client.model.VanillaChestModel;

@Environment(EnvType.CLIENT)
public class CursedChestBlockEntityRenderer extends BlockEntityRenderer<CursedChestBlockEntity>
{
    private static final SingleChestModel singleChestModel = new SingleChestModel();
    private static final SingleChestModel tallChestModel = new TallChestModel();
    private static final SingleChestModel vanillaChestModel = new VanillaChestModel();
    private static final SingleChestModel longChestModel = new LongChestModel();
    private static final BlockState defaultState = ModBlocks.wood_chest.getDefaultState();

    public CursedChestBlockEntityRenderer(BlockEntityRenderDispatcher dispatcher) { super(dispatcher); }

    @Override
    public void render(CursedChestBlockEntity be, float tickDelta, MatrixStack stack, VertexConsumerProvider vcp, int x, int y)
    {
        BlockState state = be.hasWorld() ? be.getCachedState() : defaultState;
        CursedChestType chestType = state.get(CursedChestBlock.TYPE);
        SingleChestModel model = singleChestModel;
        if (chestType == CursedChestType.BOTTOM || chestType == CursedChestType.TOP) model = tallChestModel;
        else if (chestType == CursedChestType.FRONT || chestType == CursedChestType.BACK) model = longChestModel;
        else if (chestType == CursedChestType.LEFT || chestType == CursedChestType.RIGHT) model = vanillaChestModel;
        stack.push();
        stack.translate(0.5D, 0.5D, 0.5D);
        stack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(-state.get(Properties.HORIZONTAL_FACING).asRotation()));
        stack.translate(-0.5D, -0.5D, -0.5D);
        model.setLidPitch(be.getAnimationProgress(tickDelta));
        if (chestType == CursedChestType.BACK) stack.translate(0.0D, 0.0D, 1.0D);
        else if (chestType == CursedChestType.TOP) stack.translate(0.0D, -1.0D, 0.0D);
        else if (chestType == CursedChestType.RIGHT) stack.translate(-1.0D, 0.0D, 0.0D);
        model.render(stack, new SpriteIdentifier(ExpandedStorageClient.CHEST_TEXTURE_ATLAS,
                Registries.CHEST.get(be.getBlock()).getChestTexture(chestType)).getVertexConsumer(vcp, RenderLayer::getEntityCutout), x, y);
        stack.pop();
    }
}