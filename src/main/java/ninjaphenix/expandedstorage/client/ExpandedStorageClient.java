package ninjaphenix.expandedstorage.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.screen.ScreenProviderRegistry;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.Identifier;
import ninjaphenix.expandedstorage.ExpandedStorage;
import ninjaphenix.expandedstorage.api.Registries;
import ninjaphenix.expandedstorage.api.block.enums.CursedChestType;
import ninjaphenix.expandedstorage.api.client.gui.screen.ingame.ScrollableScreen;
import ninjaphenix.expandedstorage.block.ModBlocks;
import ninjaphenix.expandedstorage.client.render.block.entity.CursedChestBlockEntityRenderer;

import java.util.Optional;

@Environment(EnvType.CLIENT)
public class ExpandedStorageClient implements ClientModInitializer
{
    @Override
    public void onInitializeClient()
    {
        BlockEntityRendererRegistry.INSTANCE.register(ModBlocks.CURSED_CHEST, new CursedChestBlockEntityRenderer());
        ScreenProviderRegistry.INSTANCE.registerFactory(ExpandedStorage.getId("scrollcontainer"), ScrollableScreen::createScreen);

        ClientSpriteRegistryCallback.event(SpriteAtlasTexture.BLOCK_ATLAS_TEX).register((atlas, registry) -> {
            for (Identifier id : Registries.MODELED.getIds())
            {
                if (id.getNamespace().equals(ExpandedStorage.MOD_ID) && !id.getPath().equals("null"))
                {
                    Optional<Registries.ModeledTierData> data = Registries.MODELED.getOrEmpty(id);
                    data.ifPresent((d) ->
                    {
                        registry.register(d.getChestTexture(CursedChestType.SINGLE));
                        registry.register(d.getChestTexture(CursedChestType.BOTTOM));
                        registry.register(d.getChestTexture(CursedChestType.LEFT));
                        registry.register(d.getChestTexture(CursedChestType.FRONT));
                    });

                }
            }
        });
    }
}
