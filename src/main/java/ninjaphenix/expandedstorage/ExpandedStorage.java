package ninjaphenix.expandedstorage;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import ninjaphenix.expandedstorage.client.ExpandedStorageClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("expandedstorage")
public class ExpandedStorage
{
	private static final String MOD_ID = "expandedstorage";
	private static final Logger LOGGER = LogManager.getLogger();
	public static final ItemGroup group = new ItemGroup(MOD_ID)
	{
		@OnlyIn(Dist.CLIENT)
		@Override
		public ItemStack createIcon() { return new ItemStack(ModContent.DIAMOND_CHEST.getSecond()); }
	};

	public static ResourceLocation getRl(String path) { return new ResourceLocation(MOD_ID, path); }

	public ExpandedStorage()
	{
		LOGGER.info("MOD CONSTRUCTED!");
		ModContent.initializeContent();
		FMLJavaModLoadingContext.get().getModEventBus().register(ModContent.class);
		DistExecutor.runWhenOn(Dist.CLIENT, () -> ExpandedStorageClient::init);
	}
}
