package ninjaphenix.expandedstorage.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.SimpleRegistry;
import ninjaphenix.expandedstorage.ExpandedStorage;
import ninjaphenix.expandedstorage.api.Registries;
import ninjaphenix.expandedstorage.api.block.misc.CursedChestType;
import ninjaphenix.expandedstorage.api.client.ExpandedStorageAPIClient;

import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public class ExpandedStorageClient implements ClientModInitializer
{
    private static <T extends Registries.TierData> void iterateOurTiers(SimpleRegistry<T> registry, Consumer<T> consumer)
    {
        for (Identifier id : registry.getIds())
            if (id.getNamespace().equals(ExpandedStorage.MOD_ID) && !id.getPath().equals("null"))
                registry.getOrEmpty(id).ifPresent(consumer);
    }

    public static void appendTexturesToAtlas(Consumer<Identifier> consumer)
    {
        iterateOurTiers(Registries.CHEST, (data) ->
        {
            consumer.accept(data.getChestTexture(CursedChestType.SINGLE));
            consumer.accept(data.getChestTexture(CursedChestType.BOTTOM));
            consumer.accept(data.getChestTexture(CursedChestType.LEFT));
            consumer.accept(data.getChestTexture(CursedChestType.FRONT));
        });
    }

    @Override
    public void onInitializeClient()
    {
        ClientSpriteRegistryCallback.event(ExpandedStorageAPIClient.CHEST_TEXTURE_ATLAS).register((atlas, registry) ->
                iterateOurTiers(Registries.CHEST, (data) ->
                {
                    registry.register(data.getChestTexture(CursedChestType.SINGLE));
                    registry.register(data.getChestTexture(CursedChestType.BOTTOM));
                    registry.register(data.getChestTexture(CursedChestType.LEFT));
                    registry.register(data.getChestTexture(CursedChestType.FRONT));
                })
        );
    }
}
