package ninjaphenix.expandedstorage.api;

import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.SimpleRegistry;
import ninjaphenix.expandedstorage.ExpandedStorage;
import ninjaphenix.expandedstorage.api.block.enums.CursedChestType;

public class Registries
{
    // Make these defaulted registries to nullId?
    // For vanilla styled non-full cube chests
    public static final SimpleRegistry<ModeledTierData> MODELED = new SimpleRegistry<>();
    // For full cube chests
    public static final SimpleRegistry<TierData> OLD = new SimpleRegistry<>();

    static
    {
        Identifier nullId = ExpandedStorage.getId("null");
        MODELED.add(nullId, new ModeledTierData(0, new TranslatableText("container.expandedstorage.error"), nullId, nullId, nullId, nullId, nullId));
        OLD.add(nullId, new TierData(0, new TranslatableText("container.expandedstorage.error"), nullId));
    }

    public static class ModeledTierData extends TierData
    {
        protected final Identifier singleTexture;
        protected final Identifier vanillaTexture;
        protected final Identifier tallTexture;
        protected final Identifier longTexture;

        public ModeledTierData(int slots, Text containerName, Identifier blockId, Identifier singleTexture, Identifier vanillaTexture,
                Identifier tallTexture, Identifier longTexture)
        {
            super(slots, containerName, blockId);
            this.singleTexture = singleTexture;
            this.vanillaTexture = vanillaTexture;
            this.tallTexture = tallTexture;
            this.longTexture = longTexture;
        }

        public Identifier getChestTexture(CursedChestType type)
        {
            if (type == CursedChestType.BOTTOM) return tallTexture;
            else if (type == CursedChestType.LEFT) return vanillaTexture;
            else if (type == CursedChestType.FRONT) return longTexture;
            return singleTexture;
        }
    }

    public static class TierData
    {
        protected final int slots;
        protected final Text containerName;
        protected final Identifier blockId;

        public TierData(int slots, Text containerName, Identifier blockId)
        {
            this.slots = slots;
            this.containerName = containerName;
            this.blockId = blockId;
        }

        public int getSlotCount() { return slots; }

        public Text getContainerName() { return containerName; }

        public Identifier getBlockId() { return blockId; }
    }
}
