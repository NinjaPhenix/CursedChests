package ninjaphenix.expandedstorage.client;

import net.minecraft.client.renderer.texture.SpriteUploader;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import ninjaphenix.expandedstorage.ExpandedStorage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Stream;

public class ChestSpriteUploader extends SpriteUploader
{
	public ChestSpriteUploader(TextureManager manager)
	{
		super(manager, ExpandedStorage.getRl("chest_atlas_texture"), "chest_atlas_texture");
	}

	@Override
	protected Stream<ResourceLocation> func_225640_a_()
	{
		return new ArrayList<>(Arrays.asList(ExpandedStorage.getRl("duffney"),
				ExpandedStorage.getRl("duffney2"))).stream();
	}
}
