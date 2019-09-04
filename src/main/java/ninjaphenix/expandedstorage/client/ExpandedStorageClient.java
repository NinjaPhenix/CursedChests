package ninjaphenix.expandedstorage.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.render.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.screen.ScreenProviderRegistry;
import ninjaphenix.expandedstorage.ExpandedStorage;
import ninjaphenix.expandedstorage.api.world.level.block.entity.CursedChestBlockEntity;
import ninjaphenix.expandedstorage.api.client.gui.screens.inventory.ScrollableScreen;
import ninjaphenix.expandedstorage.client.renderer.blockentity.CursedChestRenderer;

@Environment(EnvType.CLIENT)
public class ExpandedStorageClient implements ClientModInitializer
{
    @Override
    public void onInitializeClient()
    {
        BlockEntityRendererRegistry.INSTANCE.register(CursedChestBlockEntity.class, new CursedChestRenderer());
        ScreenProviderRegistry.INSTANCE.registerFactory(ExpandedStorage.getId("scrollcontainer"), ScrollableScreen::createScreen);
    }
}
