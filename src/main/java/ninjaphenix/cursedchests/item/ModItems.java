package ninjaphenix.cursedchests.item;

import ninjaphenix.cursedchests.api.block.CursedChestBlock;
import ninjaphenix.cursedchests.api.item.ChestConversionItem;
import ninjaphenix.cursedchests.api.item.ChestMutatorItem;
import ninjaphenix.cursedchests.block.ModBlocks;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModItems
{
    public static void init()
    {
        registerConversionItem(ModBlocks.wood_chest, ModBlocks.iron_chest);
        registerConversionItem(ModBlocks.wood_chest, ModBlocks.gold_chest);
        registerConversionItem(ModBlocks.wood_chest, ModBlocks.diamond_chest);
        registerConversionItem(ModBlocks.wood_chest, ModBlocks.obsidian_chest);
        registerConversionItem(ModBlocks.iron_chest, ModBlocks.gold_chest);
        registerConversionItem(ModBlocks.iron_chest, ModBlocks.diamond_chest);
        registerConversionItem(ModBlocks.iron_chest, ModBlocks.obsidian_chest);
        registerConversionItem(ModBlocks.gold_chest, ModBlocks.diamond_chest);
        registerConversionItem(ModBlocks.gold_chest, ModBlocks.obsidian_chest);
        registerConversionItem(ModBlocks.diamond_chest, ModBlocks.obsidian_chest);
        ChestMutatorItem chestMutator = new ChestMutatorItem();
        Registry.register(Registry.ITEM, new Identifier("cursedchests", "chest_mutator"), chestMutator);
    }

    @SuppressWarnings("UnusedReturnValue")
    private static Item registerConversionItem(CursedChestBlock from, CursedChestBlock to)
    {
        String fromName = Registry.BLOCK.getId(from).getPath();
        String toName = Registry.BLOCK.getId(to).getPath();
        Item conversionKit = new ChestConversionItem(from, to);
        Registry.register(Registry.ITEM, new Identifier("cursedchests",
                fromName.substring(0, fromName.indexOf('_')) + "_to_" + toName.substring(0, toName.indexOf('_')) + "_conversion_kit"), conversionKit);
        return conversionKit;
    }
}
