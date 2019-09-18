package ninjaphenix.expandedstorage;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import ninjaphenix.expandedstorage.api.block.AbstractChestBlock;
import ninjaphenix.expandedstorage.api.container.ScrollableContainer;
import ninjaphenix.expandedstorage.block.ModBlocks;
import ninjaphenix.expandedstorage.item.ModItems;

public class ExpandedStorage implements ModInitializer
{
    public static final String MOD_ID = "expandedstorage";
    public static ItemGroup group;

    public static Identifier getId(String path) { return new Identifier(MOD_ID, path); }

    @Override
    public void onInitialize()
    {
        group = FabricItemGroupBuilder.build(getId(MOD_ID), () -> new ItemStack(ModBlocks.diamond_chest.asItem()));
        ModBlocks.init();
        ModItems.init();
        ContainerProviderRegistry.INSTANCE.registerFactory(getId("scrollcontainer"), ((syncId, identifier, player, buf) ->
        {
            BlockPos pos = buf.readBlockPos();
            Text name = buf.readText();
            World world = player.getEntityWorld();
            return new ScrollableContainer(syncId, player.inventory, AbstractChestBlock.getInventoryStatic(world, pos), name);
        }));
    }
}
