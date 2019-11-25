package ninjaphenix.expandedstorage.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.screen.ScreenProviderRegistry;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.minecraft.class_4730;
import net.minecraft.util.Identifier;
import ninjaphenix.expandedstorage.ExpandedStorage;
import ninjaphenix.expandedstorage.api.Registries;
import ninjaphenix.expandedstorage.api.block.enums.CursedChestType;
import ninjaphenix.expandedstorage.api.client.gui.screen.ingame.ScrollableScreen;
import ninjaphenix.expandedstorage.block.ModBlocks;
import ninjaphenix.expandedstorage.client.render.block.entity.CursedChestBlockEntityRenderer;

import java.util.Optional;
import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public class ExpandedStorageClient implements ClientModInitializer
{
    public static void makeAtlases(Consumer<class_4730> consumer)
    {
        for (Identifier id : Registries.MODELED.getIds())
        {
            if (id.getNamespace().equals(ExpandedStorage.MOD_ID) && !id.getPath().equals("null"))
            {
                Optional<Registries.ModeledTierData> data = Registries.MODELED.getOrEmpty(id);
                data.ifPresent((d) ->
                {
                    consumer.accept(
                            new class_4730(new Identifier(ExpandedStorage.MOD_ID, "textures/atlas/chest.png"), d.getChestTexture(CursedChestType.SINGLE)));
                    consumer.accept(
                            new class_4730(new Identifier(ExpandedStorage.MOD_ID, "textures/atlas/chest.png"), d.getChestTexture(CursedChestType.BOTTOM)));
                    consumer.accept(
                            new class_4730(new Identifier(ExpandedStorage.MOD_ID, "textures/atlas/chest.png"), d.getChestTexture(CursedChestType.LEFT)));
                    consumer.accept(
                            new class_4730(new Identifier(ExpandedStorage.MOD_ID, "textures/atlas/chest.png"), d.getChestTexture(CursedChestType.FRONT)));
                });
            }
        }
    }

    @Override
    public void onInitializeClient()
    {
        BlockEntityRendererRegistry.INSTANCE.register(ModBlocks.CURSED_CHEST, CursedChestBlockEntityRenderer::new);
        ScreenProviderRegistry.INSTANCE.registerFactory(ExpandedStorage.getId("scrollcontainer"), ScrollableScreen::createScreen);
        ClientSpriteRegistryCallback.event(new Identifier(ExpandedStorage.MOD_ID, "textures/atlas/chest.png")).register((atlas, registry) -> {
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
