package ninjaphenix.cursedchests.block;

import ninjaphenix.cursedchests.api.CursedChestRegistry;
import ninjaphenix.cursedchests.api.block.CursedChestBlock;
import ninjaphenix.cursedchests.api.block.entity.CursedChestBlockEntity;
import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModBlocks
{
	public static BlockEntityType<CursedChestBlockEntity> CURSED_CHEST;

	public static CursedChestBlock wood_chest;
	public static CursedChestBlock iron_chest;
	public static CursedChestBlock gold_chest;
	public static CursedChestBlock diamond_chest;
	public static CursedChestBlock obsidian_chest;

	public static void init()
	{
		// todo: move to api (wood chest)
		wood_chest = register(new CursedChestBlock(FabricBlockSettings.copy(Blocks.OAK_PLANKS).build()), "wood_chest", 3, new TranslatableText("container.cursedchests.wood_chest"), new Identifier("minecraft", "textures/entity/chest/normal.png"), new Identifier("minecraft", "textures/entity/chest/normal_double.png"), new Identifier("cursedchests", "textures/entity/wood_chest/tall.png"), new Identifier("cursedchests", "textures/entity/wood_chest/long.png"));
		iron_chest = register(new CursedChestBlock(FabricBlockSettings.copy(Blocks.IRON_BLOCK).build()), "iron_chest", 6, new TranslatableText("container.cursedchests.iron_chest"), new Identifier("cursedchests", "textures/entity/iron_chest/single.png"), new Identifier("cursedchests", "textures/entity/iron_chest/vanilla.png"), new Identifier("cursedchests", "textures/entity/iron_chest/tall.png"), new Identifier("cursedchests", "textures/entity/iron_chest/long.png"));
		gold_chest = register(new CursedChestBlock(FabricBlockSettings.copy(Blocks.GOLD_BLOCK).build()), "gold_chest", 9, new TranslatableText("container.cursedchests.gold_chest"), new Identifier("cursedchests", "textures/entity/gold_chest/single.png"), new Identifier("cursedchests", "textures/entity/gold_chest/vanilla.png"), new Identifier("cursedchests", "textures/entity/gold_chest/tall.png"), new Identifier("cursedchests", "textures/entity/gold_chest/long.png"));
		diamond_chest = register(new CursedChestBlock(FabricBlockSettings.copy(Blocks.DIAMOND_BLOCK).build()), "diamond_chest", 12, new TranslatableText("container.cursedchests.diamond_chest"), new Identifier("cursedchests", "textures/entity/diamond_chest/single.png"), new Identifier("cursedchests", "textures/entity/diamond_chest/vanilla.png"), new Identifier("cursedchests", "textures/entity/diamond_chest/tall.png"), new Identifier("cursedchests", "textures/entity/diamond_chest/long.png"));
		obsidian_chest = register(new CursedChestBlock(FabricBlockSettings.copy(Blocks.OBSIDIAN).build()), "obsidian_chest", 12, new TranslatableText("container.cursedchests.obsidian_chest"), new Identifier("cursedchests", "textures/entity/obsidian_chest/single.png"), new Identifier("cursedchests", "textures/entity/obsidian_chest/vanilla.png"), new Identifier("cursedchests", "textures/entity/obsidian_chest/tall.png"), new Identifier("cursedchests", "textures/entity/obsidian_chest/long.png"));
		// todo: move to api
		CURSED_CHEST = Registry.register(Registry.BLOCK_ENTITY, new Identifier("cursedchests", "cursed_chest"), BlockEntityType.Builder.create(CursedChestBlockEntity::new, wood_chest, iron_chest, gold_chest, diamond_chest, obsidian_chest).build(null));
	}

	private static CursedChestBlock register(CursedChestBlock block, String name, int rows, TranslatableText containerName, Identifier singleTexture, Identifier vanillaTexture, Identifier tallTexture, Identifier longTexture)
	{
		Registry.register(Registry.BLOCK, new Identifier("cursedchests", name), block);
		Registry.register(Registry.ITEM, new Identifier("cursedchests", name), new BlockItem(block, new Item.Settings().group(ItemGroup.DECORATIONS)));
		CursedChestRegistry.registerChest(new Identifier("cursedchests", name), rows, containerName, singleTexture, vanillaTexture, tallTexture, longTexture);
		return block;
	}
}
