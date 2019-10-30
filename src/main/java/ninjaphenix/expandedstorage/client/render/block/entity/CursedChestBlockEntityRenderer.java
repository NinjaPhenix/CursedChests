package ninjaphenix.expandedstorage.client.render.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.LayeredVertexConsumerStorage;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MatrixStack;
import net.minecraft.util.math.Quaternion;
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

    public CursedChestBlockEntityRenderer() { super(BlockEntityRenderDispatcher.INSTANCE); }

    @Override
    public void render(CursedChestBlockEntity blockEntity, double xOffset, double yOffset, double zOffset, float tickDelta, MatrixStack stack,
            LayeredVertexConsumerStorage consumerStorage, int textureOffsetX, int textureOffsetY)
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
        stack.multiply(new Quaternion(Vector3f.POSITIVE_Y, -float_2, true));
        stack.translate(-0.5D, -0.5D, -0.5D);
        model.setLidPitch(blockEntity.getAnimationProgress(tickDelta));
        VertexConsumer consumer = consumerStorage.getBuffer(RenderLayer.getEntitySolid(SpriteAtlasTexture.BLOCK_ATLAS_TEX));
        Sprite texture = this.getSprite(Registries.MODELED.get(tier).getChestTexture(chestType));
        if (chestType == CursedChestType.BACK) stack.translate(0.0D, 0.0D, 1.0D);
        else if (chestType == CursedChestType.TOP) stack.translate(0.0D, -1.0D, 0.0D);
        else if (chestType == CursedChestType.RIGHT) stack.translate(-1.0D, 0.0D, 0.0D);
        model.render(stack, consumer, 0.0625f, textureOffsetX, textureOffsetY, texture);
        stack.pop();
    }
}