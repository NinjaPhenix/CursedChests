package ninjaphenix.cursedchests;

import ninjaphenix.cursedchests.api.block.CursedChestBlock;
import ninjaphenix.cursedchests.api.block.entity.CursedChestBlockEntity;
import ninjaphenix.cursedchests.api.client.gui.container.ScrollableScreen;
import ninjaphenix.cursedchests.api.container.ScrollableContainer;
import ninjaphenix.cursedchests.block.ModBlocks;
import ninjaphenix.cursedchests.client.render.block.entity.CursedChestBlockEntityRenderer;
import ninjaphenix.cursedchests.item.ModItems;
import net.fabricmc.api.*;
import net.fabricmc.fabric.api.client.render.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.screen.ScreenProviderRegistry;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@EnvironmentInterface(itf = ClientModInitializer.class, value = EnvType.CLIENT)
public class CursedChests implements ModInitializer, ClientModInitializer
{
	@Override public void onInitialize()
	{
		ModBlocks.init();
		ModItems.init();
		ContainerProviderRegistry.INSTANCE.registerFactory(new Identifier("cursedchests", "scrollcontainer"), ((syncId, identifier, player, buf) ->
		{
			BlockPos pos = buf.readBlockPos();
			Text containerName = buf.readText();
			World world = player.getEntityWorld();
			return new ScrollableContainer(syncId, player.inventory, CursedChestBlock.getInventoryStatic(world.getBlockState(pos), world, pos), containerName);
		}));
	}

	@Environment(EnvType.CLIENT) @Override public void onInitializeClient()
	{
		BlockEntityRendererRegistry.INSTANCE.register(CursedChestBlockEntity.class, new CursedChestBlockEntityRenderer());
		ScreenProviderRegistry.INSTANCE.registerFactory(new Identifier("cursedchests", "scrollcontainer"), ScrollableScreen::createScreen);
	}
}
