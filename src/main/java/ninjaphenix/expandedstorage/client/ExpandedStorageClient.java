package ninjaphenix.expandedstorage.client;

import net.minecraft.client.renderer.Atlases;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import ninjaphenix.expandedstorage.ExpandedStorage;
import ninjaphenix.expandedstorage.ModContent;
import ninjaphenix.expandedstorage.api.Registries;
import ninjaphenix.expandedstorage.api.block.enums.CursedChestType;
import ninjaphenix.expandedstorage.client.render.CursedChestRenderer;
import ninjaphenix.expandedstorage.client.render.CursedChestTileEntityItemStackRenderer;

@OnlyIn(Dist.CLIENT)
public class ExpandedStorageClient
{
	@SubscribeEvent
	public static void setup(final FMLClientSetupEvent event)
	{
		ClientRegistry.bindTileEntityRenderer(ModContent.CURSED_CHEST_TE, CursedChestRenderer::new);
		// Work around for TileEntityRendererDispatcher.instance.func_228852_a_ not working because
		// the renders map for some reason doesn't contain my tile entity type despite being registered literally above.
		CursedChestTileEntityItemStackRenderer.renderer = new CursedChestRenderer(TileEntityRendererDispatcher.instance);
	}

	@SubscribeEvent
	public static void preStitchTextures(final TextureStitchEvent.Pre event)
	{
		if (!event.getMap().getBasePath().equals(Atlases.CHEST_ATLAS)) { return; }
		for (ResourceLocation entry : Registries.MODELED.keySet())
		{
			if (entry.getNamespace().equals(ExpandedStorage.MOD_ID))
			{
				if (entry.getPath().equals("null")) { continue; }
				//noinspection OptionalGetWithoutIsPresent
				final Registries.ModeledTierData data = Registries.MODELED.getValue(entry).get();
				for (CursedChestType value : CursedChestType.values())
				{
					if (value.isRenderedType())
					{
						event.addSprite(new ResourceLocation(ExpandedStorage.MOD_ID, data.getChestTexture(value).getPath()));
					}
				}
			}
		}
	}
}
