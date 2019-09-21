package ninjaphenix.expandedstorage.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.render.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.screen.ScreenProviderRegistry;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.Identifier;
import ninjaphenix.expandedstorage.ExpandedStorage;
import ninjaphenix.expandedstorage.api.Registries;
import ninjaphenix.expandedstorage.api.block.entity.CursedChestBlockEntity;
import ninjaphenix.expandedstorage.api.block.enums.CursedChestType;
import ninjaphenix.expandedstorage.api.client.gui.screen.ingame.ScrollableScreen;
import ninjaphenix.expandedstorage.client.render.block.entity.CursedChestBlockEntityRenderer;

@Environment(EnvType.CLIENT)
public class ExpandedStorageClient implements ClientModInitializer
{
    @Override
    public void onInitializeClient()
    {
        BlockEntityRendererRegistry.INSTANCE.register(CursedChestBlockEntity.class, new CursedChestBlockEntityRenderer());
        ScreenProviderRegistry.INSTANCE.registerFactory(ExpandedStorage.getId("scrollcontainer"), ScrollableScreen::createScreen);

        ClientSpriteRegistryCallback.event(SpriteAtlasTexture.BLOCK_ATLAS_TEX).register((atlas, registry) -> {
            for (Identifier id : Registries.MODELED.getIds())
            {
                if (id.getNamespace() == ExpandedStorage.MOD_ID && id.getPath() != "null")
                {
                    Registries.ModeledTierData data = Registries.MODELED.get(id);
                    registry.register(data.getChestTexture(CursedChestType.SINGLE));
                    registry.register(data.getChestTexture(CursedChestType.BOTTOM));
                    registry.register(data.getChestTexture(CursedChestType.LEFT));
                    registry.register(data.getChestTexture(CursedChestType.FRONT));
                }
            }
        });
    }
}
