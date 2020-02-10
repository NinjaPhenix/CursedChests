package ninjaphenix.expandedstorage.block;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import ninjaphenix.expandedstorage.ExpandedStorage;
import ninjaphenix.expandedstorage.api.Registries;
import ninjaphenix.expandedstorage.api.block.CursedChestBlock;
import ninjaphenix.expandedstorage.api.block.OldChestBlock;

@SuppressWarnings("WeakerAccess")
public final class ModBlocks
{
	public static final CursedChestBlock diamond_chest;

	static
	{
		chest(Blocks.OAK_PLANKS, "wood_chest", 3);
		chest(Blocks.PUMPKIN, "pumpkin_chest", 3);
		chest(Blocks.OAK_PLANKS, "christmas_chest", 3);
		chest(Blocks.IRON_BLOCK, "iron_chest", 6);
		chest(Blocks.GOLD_BLOCK, "gold_chest", 9);
		diamond_chest = chest(Blocks.DIAMOND_BLOCK, "diamond_chest", 12);
		chest(Blocks.OBSIDIAN, "obsidian_chest", 12);
		chest(Blocks.NETHERITE_BLOCK, "netherite_chest", 15);

		old(Blocks.OAK_PLANKS, "wood_chest", 3);
		old(Blocks.IRON_BLOCK, "iron_chest", 6);
		old(Blocks.GOLD_BLOCK, "gold_chest", 9);
		old(Blocks.DIAMOND_BLOCK, "diamond_chest", 12);
		old(Blocks.OBSIDIAN, "obsidian_chest", 12);
		old(Blocks.NETHERITE_BLOCK, "netherite_chest", 15);
	}

	private ModBlocks() {}

	private static OldChestBlock old(Block material, String name, int rows)
	{
		final OldChestBlock block = new OldChestBlock(Block.Settings.copy(material));
		final Text containerName = new TranslatableText("container.expandedstorage." + name);
		final Identifier id = ExpandedStorage.getId("old_" + name);
		Registry.register(Registry.BLOCK, id, block);
		Registry.register(Registry.ITEM, id, new BlockItem(block, new Item.Settings().group(ExpandedStorage.group)));
		Registries.OLD_CHEST.add(ExpandedStorage.getId(name), new Registries.TierData(rows * 9, containerName, id));
		return block;
	}

	private static CursedChestBlock chest(Block material, String name, int rows)
	{
		final CursedChestBlock block = new CursedChestBlock(Block.Settings.copy(material));
		final Text containerName = new TranslatableText("container.expandedstorage." + name);
		final Identifier singleTexture = ExpandedStorage.getId("entity/" + name + "/single");
		final Identifier vanillaTexture = ExpandedStorage.getId("entity/" + name + "/vanilla");
		final Identifier tallTexture = ExpandedStorage.getId("entity/" + name + "/tall");
		final Identifier longTexture = ExpandedStorage.getId("entity/" + name + "/long");
		final Identifier id = ExpandedStorage.getId(name);
		Registry.register(Registry.BLOCK, id, block);
		Registry.register(Registry.ITEM, id, new BlockItem(block, new Item.Settings().group(ExpandedStorage.group)));
		Registries.CHEST.add(id, new Registries.ChestTierData(rows * 9, containerName, id, singleTexture, vanillaTexture, tallTexture, longTexture));
		return block;
	}

	public static void init() {}
}
