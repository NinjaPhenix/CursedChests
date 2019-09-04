package ninjaphenix.expandedstorage.client.renderer.blockentity;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import ninjaphenix.expandedstorage.api.Registries;
import ninjaphenix.expandedstorage.api.world.level.block.CursedChestBlock;
import ninjaphenix.expandedstorage.api.world.level.block.state.properties.CursedChestType;
import ninjaphenix.expandedstorage.api.world.level.block.entity.CursedChestBlockEntity;
import ninjaphenix.expandedstorage.world.level.block.ModBlocks;
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
        RenderSystem.enableDepthTest();
        RenderSystem.depthFunc(515);
        RenderSystem.depthMask(true);
        if (breaking_stage >= 0)
        {
            RenderSystem.matrixMode(5890);
            RenderSystem.pushMatrix();
            if (chestType == CursedChestType.FRONT || chestType == CursedChestType.BACK) RenderSystem.scalef(6, 6, 1);
            else if (chestType == CursedChestType.BOTTOM || chestType == CursedChestType.TOP) RenderSystem.scalef(4, 8, 1);
            else if (chestType == CursedChestType.LEFT || chestType == CursedChestType.RIGHT) RenderSystem.scalef(8, 4, 1);
            else RenderSystem.scalef(4, 4, 1);
            RenderSystem.translatef(0.0625F, 0.0625F, 0.0625F);
            RenderSystem.matrixMode(5888);
        }
        else RenderSystem.color4f(1, 1, 1, 1);
        RenderSystem.pushMatrix();
        RenderSystem.enableRescaleNormal();
        RenderSystem.translated(x, y + 1, z + 1);
        RenderSystem.scalef(1, -1, -1);
        float chestYaw = state.get(CursedChestBlock.FACING).asRotation();
        if (Math.abs(chestYaw) > 1.0E-5D)
        {
            RenderSystem.translated(0.5, 0.5, 0.5);
            RenderSystem.rotatef(chestYaw, 0, 1, 0);
            RenderSystem.translated(-0.5, -0.5, -0.5);
        }
        if (chestType == CursedChestType.TOP) RenderSystem.translatef(0, 1, 0);
        else if (chestType == CursedChestType.RIGHT) RenderSystem.translatef(-1, 0, 0);
        else if (chestType == CursedChestType.BACK) RenderSystem.translatef(0, 0, -1);
        setLidPitch(blockEntity, lidPitch, chestModel);
        chestModel.render();
        RenderSystem.disableRescaleNormal();
        RenderSystem.popMatrix();
        RenderSystem.color4f(1, 1, 1, 1);
        if (breaking_stage >= 0)
        {
            RenderSystem.matrixMode(5890);
            RenderSystem.popMatrix();
            RenderSystem.matrixMode(5888);
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