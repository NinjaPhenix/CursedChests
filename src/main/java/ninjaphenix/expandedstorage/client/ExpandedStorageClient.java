package ninjaphenix.expandedstorage.client;

import net.minecraftforge.fml.client.registry.ClientRegistry;
import ninjaphenix.expandedstorage.api.block.entity.CursedChestTileEntity;
import ninjaphenix.expandedstorage.client.render.CursedChestRenderer;

public class ExpandedStorageClient
{

	public static void init()
	{
		ClientRegistry.bindTileEntitySpecialRenderer(CursedChestTileEntity.class, new CursedChestRenderer());
	}

}
