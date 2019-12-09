package ninjaphenix.expandedstorage.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.screen.ScreenProviderRegistry;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.minecraft.block.enums.SlabType;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.SimpleRegistry;
import ninjaphenix.expandedstorage.ExpandedStorage;
import ninjaphenix.expandedstorage.api.ExpandedStorageAPI;
import ninjaphenix.expandedstorage.api.Registries;
import ninjaphenix.expandedstorage.api.block.enums.CursedChestType;
import ninjaphenix.expandedstorage.api.client.gui.screen.ingame.ScrollableScreen;
import ninjaphenix.expandedstorage.client.render.block.entity.CursedChestBlockEntityRenderer;
import ninjaphenix.expandedstorage.client.render.block.entity.SlabChestBlockEntityRenderer;

import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public class ExpandedStorageClient implements ClientModInitializer
{
    public static final Identifier CHEST_TEXTURE_ATLAS = ExpandedStorage.getId("textures/atlas/chest.png");

    public static void makeAtlases(Consumer<SpriteIdentifier> consumer)
    {
        iterateOurTiers(Registries.CHEST, (data) ->
        {
            consumer.accept(new SpriteIdentifier(CHEST_TEXTURE_ATLAS, data.getChestTexture(CursedChestType.SINGLE)));
            consumer.accept(new SpriteIdentifier(CHEST_TEXTURE_ATLAS, data.getChestTexture(CursedChestType.BOTTOM)));
            consumer.accept(new SpriteIdentifier(CHEST_TEXTURE_ATLAS, data.getChestTexture(CursedChestType.LEFT)));
            consumer.accept(new SpriteIdentifier(CHEST_TEXTURE_ATLAS, data.getChestTexture(CursedChestType.FRONT)));
        });
        iterateOurTiers(Registries.SLAB, (data) -> consumer.accept(new SpriteIdentifier(CHEST_TEXTURE_ATLAS, data.getChestTexture(SlabType.BOTTOM))));
    }

    private static <T extends Registries.TierData> void iterateOurTiers(SimpleRegistry<T> registry, Consumer<T> consumer)
    {
        for (Identifier id : registry.getIds())
            if (id.getNamespace().equals(ExpandedStorage.MOD_ID) && !id.getPath().equals("null"))
                registry.getOrEmpty(id).ifPresent(consumer);
    }

    @Override
    public void onInitializeClient()
    {
        BlockEntityRendererRegistry.INSTANCE.register(ExpandedStorageAPI.CURSED_CHEST, CursedChestBlockEntityRenderer::new);
        BlockEntityRendererRegistry.INSTANCE.register(ExpandedStorageAPI.SLAB_CHEST, SlabChestBlockEntityRenderer::new);
        ScreenProviderRegistry.INSTANCE.registerFactory(ExpandedStorage.getId("scrollcontainer"), ScrollableScreen::createScreen);
        ClientSpriteRegistryCallback.event(CHEST_TEXTURE_ATLAS).register((atlas, registry) ->
        {
            iterateOurTiers(Registries.CHEST, (data) ->
            {
                registry.register(data.getChestTexture(CursedChestType.SINGLE));
                registry.register(data.getChestTexture(CursedChestType.BOTTOM));
                registry.register(data.getChestTexture(CursedChestType.LEFT));
                registry.register(data.getChestTexture(CursedChestType.FRONT));
            });
            iterateOurTiers(Registries.SLAB, (data) -> registry.register(data.getChestTexture(SlabType.BOTTOM)));
        });
    }
}
