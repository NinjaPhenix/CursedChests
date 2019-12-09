package ninjaphenix.expandedstorage;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.InventoryProvider;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import ninjaphenix.expandedstorage.api.container.ScrollableContainer;
import ninjaphenix.expandedstorage.block.ModBlocks;
import ninjaphenix.expandedstorage.item.ModItems;

public class ExpandedStorage implements ModInitializer
{
    public static final String MOD_ID = "expandedstorage";
    public static final ItemGroup group = FabricItemGroupBuilder.build(getId(MOD_ID), () -> new ItemStack(ModBlocks.diamond_chest));

    public static Identifier getId(String path) { return new Identifier(MOD_ID, path); }

    @Override
    public void onInitialize()
    {
        ModBlocks.init();
        ModItems.init();
        ContainerProviderRegistry.INSTANCE.registerFactory(getId("scrollcontainer"), (syncId, identifier, player, buf) ->
        {
            final BlockPos pos = buf.readBlockPos();
            final Text name = buf.readText();
            final World world = player.getEntityWorld();
            final BlockState state = world.getBlockState(pos);
            final Block block = state.getBlock();
            if (block instanceof InventoryProvider)
                return new ScrollableContainer(syncId, player.inventory, ((InventoryProvider) block).getInventory(state, world, pos), name);
            return null;
        });
    }
}
