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
import ninjaphenix.expandedstorage.api.block.OldSlabChestBlock;
import ninjaphenix.expandedstorage.api.block.SlabChestBlock;

@SuppressWarnings("WeakerAccess")
public class ModBlocks
{
    public static final CursedChestBlock wood_chest;
    public static final CursedChestBlock pumpkin_chest;
    public static final CursedChestBlock christmas_chest;
    public static final CursedChestBlock iron_chest;
    public static final CursedChestBlock gold_chest;
    public static final CursedChestBlock diamond_chest;
    public static final CursedChestBlock obsidian_chest;

    public static final SlabChestBlock wood_chest_slab;
    public static final SlabChestBlock pumpkin_chest_slab;
    public static final SlabChestBlock christmas_chest_slab;
    public static final SlabChestBlock iron_chest_slab;
    public static final SlabChestBlock gold_chest_slab;
    public static final SlabChestBlock diamond_chest_slab;
    public static final SlabChestBlock obsidian_chest_slab;

    public static final OldChestBlock old_wood_chest;
    public static final OldChestBlock old_iron_chest;
    public static final OldChestBlock old_gold_chest;
    public static final OldChestBlock old_diamond_chest;
    public static final OldChestBlock old_obsidian_chest;

    public static final OldSlabChestBlock old_wood_chest_slab;
    public static final OldSlabChestBlock old_iron_chest_slab;
    public static final OldSlabChestBlock old_gold_chest_slab;
    public static final OldSlabChestBlock old_diamond_chest_slab;
    public static final OldSlabChestBlock old_obsidian_chest_slab;

    static
    {
        wood_chest = chest(Blocks.OAK_PLANKS, "wood_chest", 3);
        pumpkin_chest = chest(Blocks.PUMPKIN, "pumpkin_chest", 3);
        christmas_chest = chest(Blocks.OAK_PLANKS, "christmas_chest", 3);
        iron_chest = chest(Blocks.IRON_BLOCK, "iron_chest", 6);
        gold_chest = chest(Blocks.GOLD_BLOCK, "gold_chest", 9);
        diamond_chest = chest(Blocks.DIAMOND_BLOCK, "diamond_chest", 12);
        obsidian_chest = chest(Blocks.OBSIDIAN, "obsidian_chest", 12);

        wood_chest_slab = slab(Blocks.OAK_PLANKS, "wood_chest", 3);
        pumpkin_chest_slab = slab(Blocks.PUMPKIN, "pumpkin_chest", 3);
        christmas_chest_slab = slab(Blocks.OAK_PLANKS, "christmas_chest", 3);
        iron_chest_slab = slab(Blocks.IRON_BLOCK, "iron_chest", 6);
        gold_chest_slab = slab(Blocks.GOLD_BLOCK, "gold_chest", 9);
        diamond_chest_slab = slab(Blocks.DIAMOND_BLOCK, "diamond_chest", 12);
        obsidian_chest_slab = slab(Blocks.OBSIDIAN, "obsidian_chest", 12);

        old_wood_chest = old(Blocks.OAK_PLANKS, "wood_chest", 3);
        old_iron_chest = old(Blocks.IRON_BLOCK, "iron_chest", 6);
        old_gold_chest = old(Blocks.GOLD_BLOCK, "gold_chest", 9);
        old_diamond_chest = old(Blocks.DIAMOND_BLOCK, "diamond_chest", 12);
        old_obsidian_chest = old(Blocks.OBSIDIAN, "obsidian_chest", 12);

        old_wood_chest_slab = oldSlab(Blocks.OAK_PLANKS, "wood_chest", 3);
        old_iron_chest_slab = oldSlab(Blocks.IRON_BLOCK, "iron_chest", 6);
        old_gold_chest_slab = oldSlab(Blocks.GOLD_BLOCK, "gold_chest", 9);
        old_diamond_chest_slab = oldSlab(Blocks.DIAMOND_BLOCK, "diamond_chest", 12);
        old_obsidian_chest_slab = oldSlab(Blocks.OBSIDIAN, "obsidian_chest", 12);
    }

    private ModBlocks() {}

    private static SlabChestBlock slab(Block material, String name, int rows)
    {
        final SlabChestBlock block = new SlabChestBlock(Block.Settings.copy(material));
        final Text containerName = new TranslatableText("container.expandedstorage." + name);
        final Identifier halfTexture = ExpandedStorage.getId("entity/" + name + "/half");
        final Identifier doubleTexture = ExpandedStorage.getId("entity/" + name + "/single");
        final Identifier id = ExpandedStorage.getId(name + "_slab");
        Registry.register(Registry.BLOCK, id, block);
        Registry.register(Registry.ITEM, id, new BlockItem(block, new Item.Settings().group(ExpandedStorage.group)));
        Registries.SLAB.add(ExpandedStorage.getId(name), new Registries.SlabTierData(rows * 9, containerName, id, halfTexture, doubleTexture));
        return block;
    }

    private static OldSlabChestBlock oldSlab(Block material, String name, int rows)
    {
        final OldSlabChestBlock block = new OldSlabChestBlock(Block.Settings.copy(material));
        final Text containerName = new TranslatableText("container.expandedstorage." + name);
        final Identifier id = ExpandedStorage.getId("old_" + name + "_slab");
        Registry.register(Registry.BLOCK, id, block);
        Registry.register(Registry.ITEM, id, new BlockItem(block, new Item.Settings().group(ExpandedStorage.group)));
        Registries.OLD_SLAB.add(ExpandedStorage.getId(name), new Registries.TierData(rows * 9, containerName, id));
        return block;
    }

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
