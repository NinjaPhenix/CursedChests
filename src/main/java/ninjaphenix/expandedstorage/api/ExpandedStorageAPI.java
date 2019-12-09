package ninjaphenix.expandedstorage.api;

import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import ninjaphenix.expandedstorage.api.block.CursedChestBlock;
import ninjaphenix.expandedstorage.api.block.OldChestBlock;
import ninjaphenix.expandedstorage.api.block.OldSlabChestBlock;
import ninjaphenix.expandedstorage.api.block.SlabChestBlock;
import ninjaphenix.expandedstorage.api.block.base.entity.CustomBlockEntityType;
import ninjaphenix.expandedstorage.api.block.entity.CursedChestBlockEntity;
import ninjaphenix.expandedstorage.api.block.entity.OldChestBlockEntity;
import ninjaphenix.expandedstorage.api.block.entity.OldSlabChestBlockEntity;
import ninjaphenix.expandedstorage.api.block.entity.SlabChestBlockEntity;

public final class ExpandedStorageAPI
{
    public static final BlockEntityType<CursedChestBlockEntity> CURSED_CHEST;
    public static final BlockEntityType<OldChestBlockEntity> OLD_CURSED_CHEST;
    public static final BlockEntityType<SlabChestBlockEntity> SLAB_CHEST;
    public static final BlockEntityType<OldSlabChestBlockEntity> OLD_SLAB_CHEST;

    static
    {
        CURSED_CHEST = Registry.register(Registry.BLOCK_ENTITY, new Identifier("expandedstorage", "cursed_chest"), new CustomBlockEntityType<>(
                CursedChestBlockEntity::new, (b) -> b instanceof CursedChestBlock));
        OLD_CURSED_CHEST = Registry.register(Registry.BLOCK_ENTITY, new Identifier("expandedstorage", "old_cursed_chest"), new CustomBlockEntityType<>(
                OldChestBlockEntity::new, (b) -> b instanceof OldChestBlock));
        SLAB_CHEST = Registry.register(Registry.BLOCK_ENTITY, new Identifier("expandedstorage", "slab_chest"), new CustomBlockEntityType<>(
                SlabChestBlockEntity::new, (b) -> b instanceof SlabChestBlock));
        OLD_SLAB_CHEST = Registry.register(Registry.BLOCK_ENTITY, new Identifier("expandedstorage", "old_slab_chest"), new CustomBlockEntityType<>(
                OldSlabChestBlockEntity::new, (b) -> b instanceof OldSlabChestBlock));
    }

    private ExpandedStorageAPI() { }
}
