package ninjaphenix.expandedstorage;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import ninjaphenix.expandedstorage.api.plugins.ExpandedStoragePluginV0;
import ninjaphenix.expandedstorage.block.ModBlocks;
import ninjaphenix.expandedstorage.client.ExpandedStorageClient;
import ninjaphenix.expandedstorage.item.ModItems;

import java.util.function.Consumer;

public class ExpandedStorage implements ExpandedStoragePluginV0
{
    public static final String MOD_ID = "expandedstorage";
    public static final ItemGroup group = FabricItemGroupBuilder.build(getId(MOD_ID), () -> new ItemStack(ModBlocks.diamond_chest));

    public static Identifier getId(String path) { return new Identifier(MOD_ID, path); }

    @Override
    @Environment(EnvType.CLIENT)
    public void appendTexturesToAtlas(Consumer<Identifier> consumer) { ExpandedStorageClient.appendTexturesToAtlas(consumer); }

    @Override
    public void initialize()
    {
        ModBlocks.init();
        ModItems.init();
    }
}
