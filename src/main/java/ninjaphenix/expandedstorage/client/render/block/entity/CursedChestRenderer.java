package ninjaphenix.expandedstorage.client.render.block.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
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
public class CursedChestRenderer extends BlockEntityRenderer<CursedChestBlockEntity>
{
    private static final SingleChestModel singleChestModel = new SingleChestModel();
    private static final SingleChestModel tallChestModel = new TallChestModel();
    private static final SingleChestModel vanillaChestModel = new VanillaChestModel();
    private static final SingleChestModel longChestModel = new LongChestModel();
    private static final BlockState defaultState = ModBlocks.wood_chest.getDefaultState().with(CursedChestBlock.FACING, Direction.SOUTH)
                                                                       .with(CursedChestBlock.TYPE, CursedChestType.SINGLE);

    @Override
    public void render(CursedChestBlockEntity blockEntity, double x, double y, double z, float lidPitch, int breaking_stage)
    {
        BlockState state = blockEntity.hasWorld() ? blockEntity.getCachedState() : defaultState;
        CursedChestType chestType = state.get(CursedChestBlock.TYPE);
        if (!chestType.isRenderedType() && breaking_stage < 0) return;
        Identifier b = blockEntity.getBlock();
        if (b == null) b = Registry.BLOCK.getId(ModBlocks.wood_chest);
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
        float chestYaw = state.get(CursedChestBlock.FACING).asRotation();
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

    private SingleChestModel getChestModelAndBindTexture(Identifier tier, int breaking_stage, CursedChestType chestType)
    {
        bindTexture(breaking_stage >= 0 ? DESTROY_STAGE_TEXTURES[breaking_stage] : Registries.MODELED.get(tier).getChestTexture(chestType));
        if (chestType == CursedChestType.BOTTOM || chestType == CursedChestType.TOP) return tallChestModel;
        else if (chestType == CursedChestType.FRONT || chestType == CursedChestType.BACK) return longChestModel;
        else if (chestType == CursedChestType.LEFT || chestType == CursedChestType.RIGHT) return vanillaChestModel;
        else if (chestType == CursedChestType.SINGLE) return singleChestModel;
        return null;
    }

    private void setLidPitch(CursedChestBlockEntity blockEntity, float lidPitch, SingleChestModel model)
    {
        float newPitch = 1.0F - blockEntity.getAnimationProgress(lidPitch);
        model.setLidPitch(-((1.0F - newPitch * newPitch * newPitch) * 1.5707964F));
    }
}