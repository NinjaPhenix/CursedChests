package ninjaphenix.expandedstorage.item;

import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.registry.Registry;
import ninjaphenix.expandedstorage.ExpandedStorage;
import ninjaphenix.expandedstorage.api.item.ChestConversionItem;

import java.util.ArrayList;

public final class ModItems
{
	public static void init()
	{
		ArrayList<Pair<Identifier, String>> tiers = new ArrayList<>();
		tiers.add(new Pair<>(ExpandedStorage.getId("wood_chest"), "wood"));
		tiers.add(new Pair<>(ExpandedStorage.getId("iron_chest"), "iron"));
		tiers.add(new Pair<>(ExpandedStorage.getId("gold_chest"), "gold"));
		tiers.add(new Pair<>(ExpandedStorage.getId("diamond_chest"), "diamond"));
		tiers.add(new Pair<>(ExpandedStorage.getId("obsidian_chest"), "obsidian"));
		tiers.add(new Pair<>(ExpandedStorage.getId("netherite_chest"), "netherite"));
		final int size = tiers.size();
		for (int i = 0; i < size; i++)
		{
			final Pair<Identifier, String> from = tiers.get(i);
			for (int j = i + 1; j < size; j++)
			{
				registerConversionItem(from, tiers.get(j));
			}
		}
		ChestMutatorItem chestMutator = new ChestMutatorItem();
		Registry.register(Registry.ITEM, ExpandedStorage.getId("chest_mutator"), chestMutator);
	}

	private static void registerConversionItem(Pair<Identifier, String> from, Pair<Identifier, String> to)
	{
		Item conversionKit = new ChestConversionItem(new Item.Settings().group(ExpandedStorage.group).maxCount(16), from.getLeft(), to.getLeft());
		Registry.register(Registry.ITEM, ExpandedStorage.getId(from.getRight() + "_to_" + to.getRight() + "_conversion_kit"), conversionKit);
	}
}
