package ninjaphenix.expandedstorage.block;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import ninjaphenix.expandedstorage.ExpandedStorage;
import ninjaphenix.expandedstorage.api.Registries;
import ninjaphenix.expandedstorage.api.block.CursedChestBlock;
import ninjaphenix.expandedstorage.api.block.OldChestBlock;
import ninjaphenix.expandedstorage.api.block.entity.CursedChestBlockEntity;
import ninjaphenix.expandedstorage.api.block.entity.OldChestBlockEntity;

public class ModBlocks
{
    public static BlockEntityType<CursedChestBlockEntity> CURSED_CHEST;
    public static BlockEntityType<OldChestBlockEntity> FULL_CURSED_CHEST;

    public static CursedChestBlock wood_chest;
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
                new TranslatableText("container.expandedstorage.wood_chest"), ExpandedStorage.getId("textures/entity/wood_chest/single.png"),
                ExpandedStorage.getId("textures/entity/wood_chest/vanilla.png"), ExpandedStorage.getId("textures/entity/wood_chest/tall.png"),
                ExpandedStorage.getId("textures/entity/wood_chest/long.png"));
        iron_chest = register(new CursedChestBlock(Block.Settings.copy(Blocks.IRON_BLOCK)), "iron_chest", 6,
                new TranslatableText("container.expandedstorage.iron_chest"), ExpandedStorage.getId("textures/entity/iron_chest/single.png"),
                ExpandedStorage.getId("textures/entity/iron_chest/vanilla.png"), ExpandedStorage.getId("textures/entity/iron_chest/tall.png"),
                ExpandedStorage.getId("textures/entity/iron_chest/long.png"));
        gold_chest = register(new CursedChestBlock(Block.Settings.copy(Blocks.GOLD_BLOCK)), "gold_chest", 9,
                new TranslatableText("container.expandedstorage.gold_chest"), ExpandedStorage.getId("textures/entity/gold_chest/single.png"),
                ExpandedStorage.getId("textures/entity/gold_chest/vanilla.png"), ExpandedStorage.getId("textures/entity/gold_chest/tall.png"),
                ExpandedStorage.getId("textures/entity/gold_chest/long.png"));
        diamond_chest = register(new CursedChestBlock(Block.Settings.copy(Blocks.DIAMOND_BLOCK)), "diamond_chest", 12,
                new TranslatableText("container.expandedstorage.diamond_chest"), ExpandedStorage.getId("textures/entity/diamond_chest/single.png"),
                ExpandedStorage.getId("textures/entity/diamond_chest/vanilla.png"), ExpandedStorage.getId("textures/entity/diamond_chest/tall.png"),
                ExpandedStorage.getId("textures/entity/diamond_chest/long.png"));
        // todo: Make obsidian chests an upgrade (when I implement crystal chests)
        obsidian_chest = register(new CursedChestBlock(Block.Settings.copy(Blocks.OBSIDIAN)), "obsidian_chest", 12,
                new TranslatableText("container.expandedstorage.obsidian_chest"), ExpandedStorage.getId("textures/entity/obsidian_chest/single.png"),
                ExpandedStorage.getId("textures/entity/obsidian_chest/vanilla.png"), ExpandedStorage.getId("textures/entity/obsidian_chest/tall.png"),
                ExpandedStorage.getId("textures/entity/obsidian_chest/long.png"));
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
        // todo: move to api
        CURSED_CHEST = Registry.register(Registry.BLOCK_ENTITY, ExpandedStorage.getId("cursed_chest"),
                BlockEntityType.Builder.create(CursedChestBlockEntity::new, wood_chest, iron_chest, gold_chest, diamond_chest, obsidian_chest).build(null));
        FULL_CURSED_CHEST = Registry.register(Registry.BLOCK_ENTITY, ExpandedStorage.getId("old_cursed_chest"), BlockEntityType.Builder.create(
                OldChestBlockEntity::new, old_wood_chest, old_iron_chest, old_gold_chest, old_diamond_chest, old_obsidian_chest).build(null));
    }

    private static OldChestBlock registerOld(OldChestBlock block, String name, int rows, TranslatableText containerName)
    {
        Identifier id = ExpandedStorage.getId("old_" + name);
        Registry.register(Registry.BLOCK, id, block);
        Registry.register(Registry.ITEM, id, new BlockItem(block, new Item.Settings().group(ItemGroup.DECORATIONS)));
        Registries.OLD.add(ExpandedStorage.getId(name), new Registries.TierData(rows * 9, containerName, id));
        return block;
    }

    private static CursedChestBlock register(CursedChestBlock block, String name, int rows, TranslatableText containerName, Identifier singleTexture,
            Identifier vanillaTexture, Identifier tallTexture, Identifier longTexture)
    {
        Identifier id = ExpandedStorage.getId(name);
        Registry.register(Registry.BLOCK, id, block);
        Registry.register(Registry.ITEM, id, new BlockItem(block, new Item.Settings().group(ItemGroup.DECORATIONS)));
        Registries.MODELED.add(id, new Registries.ModeledTierData(rows * 9, containerName, id, singleTexture, vanillaTexture, tallTexture, longTexture));
        return block;
    }
}
