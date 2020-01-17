package ninjaphenix.expandedstorage.client;

import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import ninjaphenix.expandedstorage.ExpandedStorage;
import ninjaphenix.expandedstorage.ModContent;
import ninjaphenix.expandedstorage.client.render.CursedChestRenderer;

public class ExpandedStorageClient
{
	public static final ResourceLocation ATLAS_TEXTURE = ExpandedStorage.getRl("chest_atlas_texture");

	public static void init()
	{
		ClientRegistry.bindTileEntityRenderer(ModContent.CURSED_CHEST_TE, new CursedChestRenderer(TileEntityRendererDispatcher.instance));
	}

}
