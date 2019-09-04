package ninjaphenix.expandedstorage;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import ninjaphenix.expandedstorage.api.world.inventory.ScrollableMenu;
import ninjaphenix.expandedstorage.api.world.level.block.AbstractChestBlock;
import ninjaphenix.expandedstorage.world.item.ModItems;
import ninjaphenix.expandedstorage.world.level.block.ModBlocks;

public class ExpandedStorage implements ModInitializer
{
    public static final String MOD_ID = "expandedstorage";

    public static Identifier getId(String path) { return new Identifier(MOD_ID, path); }

    @Override
    public void onInitialize()
    {
        ModBlocks.init();
        ModItems.init();
        ContainerProviderRegistry.INSTANCE.registerFactory(getId("scrollcontainer"), ((syncId, identifier, player, buf) ->
        {
            BlockPos pos = buf.readBlockPos();
            Text name = buf.readText();
            World world = player.getEntityWorld();
            return new ScrollableMenu(syncId, player.inventory, AbstractChestBlock.getInventoryStatic(world, pos), name);
        }));
    }
}
