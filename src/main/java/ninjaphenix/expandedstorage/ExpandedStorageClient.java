package ninjaphenix.expandedstorage;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.render.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.screen.ScreenProviderRegistry;
import ninjaphenix.expandedstorage.api.block.entity.CursedChestBlockEntity;
import ninjaphenix.expandedstorage.api.client.gui.container.ScrollableScreen;
import ninjaphenix.expandedstorage.client.render.block.entity.CursedChestBlockEntityRenderer;

@Environment(EnvType.CLIENT)
public class ExpandedStorageClient implements ClientModInitializer
{
    @Override
    public void onInitializeClient()
    {
        BlockEntityRendererRegistry.INSTANCE.register(CursedChestBlockEntity.class, new CursedChestBlockEntityRenderer());
        ScreenProviderRegistry.INSTANCE.registerFactory(ExpandedStorage.getId("scrollcontainer"), ScrollableScreen::createScreen);
    }
}
