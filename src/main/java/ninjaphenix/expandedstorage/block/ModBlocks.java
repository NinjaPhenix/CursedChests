package ninjaphenix.expandedstorage.block;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import ninjaphenix.expandedstorage.ExpandedStorage;
import ninjaphenix.expandedstorage.api.Registries;
import ninjaphenix.expandedstorage.api.block.CursedChestBlock;
import ninjaphenix.expandedstorage.api.block.OldChestBlock;
import ninjaphenix.expandedstorage.api.block.entity.CursedChestBlockEntity;
import ninjaphenix.expandedstorage.api.block.entity.CustomBlockEntityType;
import ninjaphenix.expandedstorage.api.block.entity.OldChestBlockEntity;

@SuppressWarnings("WeakerAccess")
public class ModBlocks
{
    public static BlockEntityType<CursedChestBlockEntity> CURSED_CHEST;
    public static BlockEntityType<OldChestBlockEntity> FULL_CURSED_CHEST;

    public static CursedChestBlock wood_chest;
    public static CursedChestBlock pumpkin_chest;
    public static CursedChestBlock christmas_chest;
    public static CursedChestBlock iron_chest;
    public static CursedChestBlock gold_chest;
    public static CursedChestBlock diamond_chest;
    public static CursedChestBlock obsidian_chest;

    public static OldChestBlock old_wood_chest;
    public static OldChestBlock old_iron_chest;
    public static OldChestBlock old_gold_chest;
    public static OldChestBlock old_diamond_chest;
    public static OldChestBlock old_obsidian_chest;

    public static void init()
    {
        wood_chest = register(new CursedChestBlock(Block.Settings.copy(Blocks.OAK_PLANKS)), "wood_chest", 3,
                new TranslatableText("container.expandedstorage.wood_chest"), ExpandedStorage.getId("entity/wood_chest/single"),
                ExpandedStorage.getId("entity/wood_chest/vanilla"), ExpandedStorage.getId("entity/wood_chest/tall"),
                ExpandedStorage.getId("entity/wood_chest/long"));
        pumpkin_chest = register(new CursedChestBlock(Block.Settings.copy(Blocks.PUMPKIN)), "pumpkin_chest", 3,
                new TranslatableText("container.expandedstorage.pumpkin_chest"), ExpandedStorage.getId("entity/pumpkin_chest/single"),
                ExpandedStorage.getId("entity/pumpkin_chest/vanilla"), ExpandedStorage.getId("entity/pumpkin_chest/tall"),
                ExpandedStorage.getId("entity/pumpkin_chest/long"));
        christmas_chest = register(new CursedChestBlock(Block.Settings.copy(Blocks.OAK_PLANKS)), "christmas_chest", 3,
                new TranslatableText("container.expandedstorage.christmas_chest"), ExpandedStorage.getId("entity/christmas_chest/single"),
                ExpandedStorage.getId("entity/christmas_chest/vanilla"), ExpandedStorage.getId("entity/christmas_chest/tall"),
                ExpandedStorage.getId("entity/christmas_chest/long"));
        iron_chest = register(new CursedChestBlock(Block.Settings.copy(Blocks.IRON_BLOCK)), "iron_chest", 6,
                new TranslatableText("container.expandedstorage.iron_chest"), ExpandedStorage.getId("entity/iron_chest/single"),
                ExpandedStorage.getId("entity/iron_chest/vanilla"), ExpandedStorage.getId("entity/iron_chest/tall"),
                ExpandedStorage.getId("entity/iron_chest/long"));
        gold_chest = register(new CursedChestBlock(Block.Settings.copy(Blocks.GOLD_BLOCK)), "gold_chest", 9,
                new TranslatableText("container.expandedstorage.gold_chest"), ExpandedStorage.getId("entity/gold_chest/single"),
                ExpandedStorage.getId("entity/gold_chest/vanilla"), ExpandedStorage.getId("entity/gold_chest/tall"),
                ExpandedStorage.getId("entity/gold_chest/long"));
        diamond_chest = register(new CursedChestBlock(Block.Settings.copy(Blocks.DIAMOND_BLOCK)), "diamond_chest", 12,
                new TranslatableText("container.expandedstorage.diamond_chest"), ExpandedStorage.getId("entity/diamond_chest/single"),
                ExpandedStorage.getId("entity/diamond_chest/vanilla"), ExpandedStorage.getId("entity/diamond_chest/tall"),
                ExpandedStorage.getId("entity/diamond_chest/long"));
        obsidian_chest = register(new CursedChestBlock(Block.Settings.copy(Blocks.OBSIDIAN)), "obsidian_chest", 12,
                new TranslatableText("container.expandedstorage.obsidian_chest"), ExpandedStorage.getId("entity/obsidian_chest/single"),
                ExpandedStorage.getId("entity/obsidian_chest/vanilla"), ExpandedStorage.getId("entity/obsidian_chest/tall"),
                ExpandedStorage.getId("entity/obsidian_chest/long"));
        old_wood_chest = registerOld(new OldChestBlock(Block.Settings.copy(Blocks.OAK_PLANKS)), "wood_chest", 3,
                new TranslatableText("container.expandedstorage.wood_chest"));
        old_iron_chest = registerOld(new OldChestBlock(Block.Settings.copy(Blocks.IRON_BLOCK)), "iron_chest", 6,
                new TranslatableText("container.expandedstorage.iron_chest"));
        old_gold_chest = registerOld(new OldChestBlock(Block.Settings.copy(Blocks.GOLD_BLOCK)), "gold_chest", 9,
                new TranslatableText("container.expandedstorage.gold_chest"));
        old_diamond_chest = registerOld(new OldChestBlock(Block.Settings.copy(Blocks.DIAMOND_BLOCK)), "diamond_chest", 12,
                new TranslatableText("container.expandedstorage.diamond_chest"));
        old_obsidian_chest = registerOld(new OldChestBlock(Block.Settings.copy(Blocks.OBSIDIAN)), "obsidian_chest", 12,
                new TranslatableText("container.expandedstorage.obsidian_chest"));
        CURSED_CHEST = Registry.register(Registry.BLOCK_ENTITY, ExpandedStorage.getId("cursed_chest"),
                new CustomBlockEntityType<>(CursedChestBlockEntity::new, null, (b) -> b instanceof CursedChestBlock));
        FULL_CURSED_CHEST = Registry.register(Registry.BLOCK_ENTITY, ExpandedStorage.getId("old_cursed_chest"),
                new CustomBlockEntityType<>(OldChestBlockEntity::new, null, (b) -> b instanceof OldChestBlock));
    }

    private static OldChestBlock registerOld(OldChestBlock block, String name, int rows, TranslatableText containerName)
    {
        Identifier id = ExpandedStorage.getId("old_" + name);
        Registry.register(Registry.BLOCK, id, block);
        Registry.register(Registry.ITEM, id, new BlockItem(block, new Item.Settings().group(ExpandedStorage.group)));
        Registries.OLD.add(ExpandedStorage.getId(name), new Registries.TierData(rows * 9, containerName, id));
        return block;
    }

    private static CursedChestBlock register(CursedChestBlock block, String name, int rows, TranslatableText containerName, Identifier singleTexture,
            Identifier vanillaTexture, Identifier tallTexture, Identifier longTexture)
    {
        Identifier id = ExpandedStorage.getId(name);
        Registry.register(Registry.BLOCK, id, block);
        Registry.register(Registry.ITEM, id, new BlockItem(block, new Item.Settings().group(ExpandedStorage.group)));
        Registries.MODELED.add(id, new Registries.ModeledTierData(rows * 9, containerName, id, singleTexture, vanillaTexture, tallTexture, longTexture));
        return block;
    }
}
