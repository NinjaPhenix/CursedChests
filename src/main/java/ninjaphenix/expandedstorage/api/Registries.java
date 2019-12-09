package ninjaphenix.expandedstorage.api;

import net.minecraft.block.enums.SlabType;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.SimpleRegistry;
import ninjaphenix.expandedstorage.ExpandedStorage;
import ninjaphenix.expandedstorage.api.block.enums.CursedChestType;

/**
 * This class provides data registries for adding new chests to already defined chest types. This will likely be refactored in the future as new features and
 * chest types are added.
 *
 * @author NinjaPhenix
 * @since 3.4.26
 */
public class Registries
{
    /**
     * This registry for CursedChestBlock data storage.
     */
    public static final SimpleRegistry<ChestTierData> CHEST = new SimpleRegistry<>();

    /**
     * This registry is for OldChestBlock data storage.
     */
    public static final SimpleRegistry<TierData> OLD_CHEST = new SimpleRegistry<>();

    /**
     * This registry is for SlabChestBlock data storage.
     */
    public static final SimpleRegistry<SlabTierData> SLAB = new SimpleRegistry<>();

    /**
     * This registry is for OldSlabChestBlock data storage.
     */
    public static final SimpleRegistry<TierData> OLD_SLAB = new SimpleRegistry<>();

    // Use Registries.CHEST instead
    @Deprecated
    public static final SimpleRegistry<ChestTierData> MODELED = CHEST;

    // Use Registries.OLD_CHEST instead
    @Deprecated
    public static final SimpleRegistry<TierData> OLD = OLD_CHEST;

    static
    {
        // Populates registries with null ids incase anything goes wrong. Idealy these should never present themselves.
        final Identifier nullId = ExpandedStorage.getId("null");
        final Text containerName = new TranslatableText("container.expandedstorage.error");
        CHEST.add(nullId, new ChestTierData(0, containerName, nullId, nullId, nullId, nullId, nullId));
        OLD_CHEST.add(nullId, new TierData(0, containerName, nullId));
        SLAB.add(nullId, new SlabTierData(0, containerName, nullId, nullId, nullId));
        OLD_SLAB.add(nullId, new TierData(0, containerName, nullId));
    }

    public static class SlabTierData extends TierData
    {
        private final Identifier halfTexture;
        private final Identifier doubleTexture;

        /**
         * Data representing a slab chest block.
         *
         * @param slots The amount of itemstacks this chest tier can hold.
         * @param containerName The default container name for this chest tier.
         * @param blockId The block id that represents this data.
         * @param halfTexture The blocks half texture.
         * @param doubleTexture The blocks double texture.
         */
        public SlabTierData(int slots, Text containerName, Identifier blockId, Identifier halfTexture, Identifier doubleTexture)
        {
            super(slots, containerName, blockId);
            this.halfTexture = halfTexture;
            this.doubleTexture = doubleTexture;
        }

        public Identifier getChestTexture(SlabType type)
        {
            if (type == SlabType.DOUBLE) return doubleTexture;
            return halfTexture;
        }
    }

    public static class ChestTierData extends TierData
    {
        private final Identifier singleTexture;
        private final Identifier vanillaTexture;
        private final Identifier tallTexture;
        private final Identifier longTexture;

        /**
         * Data representing a regular chest block.
         *
         * @param slots The amount of itemstacks this chest tier can hold.
         * @param containerName The default container name for this chest tier.
         * @param blockId The block id that represents this data.
         * @param singleTexture The blocks single texture.
         * @param vanillaTexture The blocks vanilla texture ( Vanilla double chests ).
         * @param tallTexture The blocks tall texture.
         * @param longTexture The blocks long texture.
         */
        public ChestTierData(int slots, Text containerName, Identifier blockId, Identifier singleTexture, Identifier vanillaTexture,
                Identifier tallTexture, Identifier longTexture)
        {
            super(slots, containerName, blockId);
            this.singleTexture = singleTexture;
            this.vanillaTexture = vanillaTexture;
            this.tallTexture = tallTexture;
            this.longTexture = longTexture;
        }

        /**
         * @param type The chest type to receive the texture for.
         * @return The texture relevant to the type.
         */
        public Identifier getChestTexture(CursedChestType type)
        {
            if (type == CursedChestType.BOTTOM || type == CursedChestType.TOP) return tallTexture;
            else if (type == CursedChestType.LEFT || type == CursedChestType.RIGHT) return vanillaTexture;
            else if (type == CursedChestType.FRONT || type == CursedChestType.BACK) return longTexture;
            return singleTexture;
        }
    }

    public static class TierData
    {
        private final int slots;
        private final Text containerName;
        private final Identifier blockId;

        /**
         * Data representing minimalist chest blocks such as OldChestBlock's.
         *
         * @param slots The amount of itemstacks this chest tier can hold.
         * @param containerName The default container name for this chest tier.
         * @param blockId The block id that represents this data.
         */
        public TierData(int slots, Text containerName, Identifier blockId)
        {
            this.slots = slots;
            this.containerName = containerName;
            this.blockId = blockId;
        }

        /**
         * @return The amount of slots the chest tier contains.
         */
        public int getSlotCount() { return slots; }

        /**
         * @return The default container name for the chest tier.
         */
        public Text getContainerName() { return containerName; }

        /**
         * @return The block that represents this data instance.
         */
        public Identifier getBlockId() { return blockId; }
    }
}
