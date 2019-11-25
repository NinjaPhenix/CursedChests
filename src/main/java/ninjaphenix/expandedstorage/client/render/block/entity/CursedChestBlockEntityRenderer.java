package ninjaphenix.expandedstorage.client.render.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.class_4730;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import ninjaphenix.expandedstorage.ExpandedStorage;
import ninjaphenix.expandedstorage.api.Registries;
import ninjaphenix.expandedstorage.api.block.CursedChestBlock;
import ninjaphenix.expandedstorage.api.block.entity.CursedChestBlockEntity;
import ninjaphenix.expandedstorage.api.block.enums.CursedChestType;
import ninjaphenix.expandedstorage.block.ModBlocks;
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
    private static final BlockState defaultState = ModBlocks.wood_chest.getDefaultState().with(CursedChestBlock.FACING, Direction.SOUTH)
                                                                       .with(CursedChestBlock.TYPE, CursedChestType.SINGLE);

    public CursedChestBlockEntityRenderer(BlockEntityRenderDispatcher dispatcher) { super(dispatcher); }

    @Override
    public void render(CursedChestBlockEntity blockEntity, float tickDelta, MatrixStack stack, VertexConsumerProvider consumerStorage, int textureOffsetX,
            int textureOffsetY)
    {
        BlockState state = blockEntity.hasWorld() ? blockEntity.getCachedState() : defaultState;
        CursedChestType chestType = state.get(CursedChestBlock.TYPE);
        Identifier tier = blockEntity.getBlock();
        SingleChestModel model;
        if (chestType == CursedChestType.BOTTOM || chestType == CursedChestType.TOP) model = tallChestModel;
        else if (chestType == CursedChestType.FRONT || chestType == CursedChestType.BACK) model = longChestModel;
        else if (chestType == CursedChestType.LEFT || chestType == CursedChestType.RIGHT) model = vanillaChestModel;
        else if (chestType == CursedChestType.SINGLE) model = singleChestModel;
        else return;
        stack.push();
        float float_2 = state.get(Properties.HORIZONTAL_FACING).asRotation();
        stack.translate(0.5D, 0.5D, 0.5D);
        stack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(-float_2));
        stack.translate(-0.5D, -0.5D, -0.5D);
        model.setLidPitch(blockEntity.getAnimationProgress(tickDelta));
        if (chestType == CursedChestType.BACK) stack.translate(0.0D, 0.0D, 1.0D);
        else if (chestType == CursedChestType.TOP) stack.translate(0.0D, -1.0D, 0.0D);
        else if (chestType == CursedChestType.RIGHT) stack.translate(-1.0D, 0.0D, 0.0D);
        class_4730 lv = new class_4730(new Identifier(ExpandedStorage.MOD_ID, "textures/atlas/chest.png"),
                Registries.MODELED.get(tier).getChestTexture(chestType));
        VertexConsumer consumer = lv.method_24145(consumerStorage, RenderLayer::getEntityCutout);
        model.render(stack, consumer, textureOffsetX, textureOffsetY);
        stack.pop();
    }
}