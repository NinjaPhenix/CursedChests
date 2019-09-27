package ninjaphenix.expandedstorage.client.render.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.block.BlockState;
import net.minecraft.class_4576;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
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

    public CursedChestBlockEntityRenderer(BlockEntityRenderDispatcher dispatcher)
    {
        super(dispatcher);
    }

    @Override
    protected void method_22738(CursedChestBlockEntity blockEntity, double xOffset, double yOffset, double zOffset, float tickDelta, int blockBreakStage,
            BlockRenderLayer renderLayer, BufferBuilder bufferBuilder, int textureOffsetX, int textureOffsetY)
    {
        BlockState state = blockEntity.hasWorld() ? blockEntity.getCachedState() : defaultState;
        CursedChestType chestType = state.get(CursedChestBlock.TYPE);
        if (!chestType.isRenderedType()) return;
        Identifier tier = blockEntity.getBlock();
        SingleChestModel model;
        if (chestType == CursedChestType.BOTTOM || chestType == CursedChestType.TOP) model = tallChestModel;
        else if (chestType == CursedChestType.FRONT || chestType == CursedChestType.BACK) model = longChestModel;
        else if (chestType == CursedChestType.LEFT || chestType == CursedChestType.RIGHT) model = vanillaChestModel;
        else if (chestType == CursedChestType.SINGLE) model = singleChestModel;
        else return;
        bufferBuilder.method_22629();
        float float_2 = state.get(Properties.HORIZONTAL_FACING).asRotation();
        bufferBuilder.method_22626(0.5D, 0.5D, 0.5D);
        bufferBuilder.method_22622(new Quaternion(Vector3f.field_20705, -float_2, true));
        bufferBuilder.method_22626(-0.5D, -0.5D, -0.5D);
        model.setLidPitch(blockEntity.getAnimationProgress(tickDelta));
        Sprite texture = this.method_22739(blockBreakStage >= 0 ? ModelLoader.field_20848.get(blockBreakStage) :
                Registries.MODELED.get(tier).getChestTexture(chestType));
        model.appendToBuffer(bufferBuilder, 0.0625f, textureOffsetX, textureOffsetY, texture);
        bufferBuilder.method_22630();
    }

    @Override
    public void render(CursedChestBlockEntity var1, double xOffset, double yOffset, double zOffset, float tickDelta, class_4587 var9, class_4597 var10, int var11)
    {

    }
}