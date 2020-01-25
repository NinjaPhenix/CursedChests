package ninjaphenix.expandedstorage.item;

import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.registry.Registry;
import ninjaphenix.expandedstorage.ExpandedStorage;
import ninjaphenix.expandedstorage.api.item.ChestConversionItem;

public class ModItems
{
	public static void init()
	{
		Pair<Identifier, String> wood = new Pair<>(ExpandedStorage.getId("wood_chest"), "wood");
		Pair<Identifier, String> iron = new Pair<>(ExpandedStorage.getId("iron_chest"), "iron");
		Pair<Identifier, String> gold = new Pair<>(ExpandedStorage.getId("gold_chest"), "gold");
		Pair<Identifier, String> diamond = new Pair<>(ExpandedStorage.getId("diamond_chest"), "diamond");
		Pair<Identifier, String> obsidian = new Pair<>(ExpandedStorage.getId("obsidian_chest"), "obsidian");
		registerConversionItem(wood, iron);
		registerConversionItem(wood, gold);
		registerConversionItem(wood, diamond);
		registerConversionItem(wood, obsidian);
		registerConversionItem(iron, gold);
		registerConversionItem(iron, diamond);
		registerConversionItem(iron, obsidian);
		registerConversionItem(gold, diamond);
		registerConversionItem(gold, obsidian);
		registerConversionItem(diamond, obsidian);
		ChestMutatorItem chestMutator = new ChestMutatorItem();
		Registry.register(Registry.ITEM, ExpandedStorage.getId("chest_mutator"), chestMutator);
	}

	private static void registerConversionItem(Pair<Identifier, String> from, Pair<Identifier, String> to)
	{
		Item conversionKit = new ChestConversionItem(from.getLeft(), to.getLeft());
		Registry.register(Registry.ITEM, ExpandedStorage.getId(from.getRight() + "_to_" + to.getRight() + "_conversion_kit"), conversionKit);
	}
}
