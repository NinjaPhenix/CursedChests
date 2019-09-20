package ninjaphenix.cursedchests.api;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import ninjaphenix.cursedchests.api.block.CursedChestType;
import ninjaphenix.cursedchests.api.block.entity.CursedChestBlockEntity;
import ninjaphenix.cursedchests.mixins.ItemDynamicRenderer;

import java.util.HashMap;

/**
 * @author NinjaPhenix
 * @version 1.0.5
 * @since 1.0.5
 */
public class CursedChestRegistry
{
    private static HashMap<Identifier, CursedChest> blockdataMap = new HashMap<>();

    // Prevents old worlds from crashing ( just in case anyone does update with existing chests )
    static
    {
        Identifier id = new Identifier("null", "null");
        blockdataMap.put(id, new CursedChest(0, new TextComponent("Error"), id, id, id, id));
    }

    /**
     * Registers a new chest block. Currently only used by the ItemRenderer Mixin to correctly render chest blocks in inventories, otherwise they will look like
     * vanilla chests.
     *
     * @param block The identifier for the vertical chest block.
     * @param rows The amount of slot rows the chest should have.
     * @param containerName The name of the chest inside of the container.
     * @param singleTexture The texture for when the chest is single style.
     * @param vanillaTexture The texture for when the chest is vanilla style.
     * @param tallTexture The texture for when the chest is tall style.
     * @param longTexture The texture for when the chest is long style.
     * @throws AssertionError Thrown when block is null or already registered.
     * @since 1.0.5
     */
    public static void registerChest(Identifier block, int rows, TranslatableComponent containerName, Identifier singleTexture, Identifier vanillaTexture,
            Identifier tallTexture, Identifier longTexture)
    {
        assert block != null && blockdataMap.containsKey(block);
        CursedChest data = new CursedChest(9 * rows, containerName, singleTexture, vanillaTexture, tallTexture, longTexture);
        blockdataMap.put(block, data);
    }

    /**
     * Gets a new instance of a registered chest block's block entity. Used in {@link ItemDynamicRenderer} to render the chests properly in inventories.
     *
     * @param block The identifier for the vertical chest block.
     * @return An instance of that block's block entity.
     * @since 1.0.5
     */
    public static CursedChestBlockEntity getChestBlockEntity(Identifier block)
    {
        CursedChestBlockEntity be = (CursedChestBlockEntity) Registry.BLOCK_ENTITY.get(new Identifier("cursedchests", "cursed_chest")).instantiate();
        be.setBlock(block);
        return be;
    }

    /**
     * Gets the identifier for the texture of the given chest.
     *
     * @param block The identifier for the vertical chest block.
     * @param type
     * @since 1.2.17
     */
    public static Identifier getChestTexture(Identifier block, CursedChestType type)
    {
        assert block != null && blockdataMap.containsKey(block);
        switch (type)
        {
            case BOTTOM:
                return blockdataMap.get(block).tallTexture;
            case LEFT:
                return blockdataMap.get(block).vanillaTexure;
            case FRONT:
                return blockdataMap.get(block).longTexture;
            default:
                return blockdataMap.get(block).singleTexture;
        }
    }

    public static int getSlots(Identifier block)
    {
        assert block != null && blockdataMap.containsKey(block);
        return blockdataMap.get(block).slots;
    }

    public static Component getDefaultContainerName(Identifier block)
    {
        assert block != null && blockdataMap.containsKey(block);
        return blockdataMap.get(block).containerName;
    }

    static class CursedChest
    {
        private final int slots;
        private final Identifier singleTexture;
        private final Identifier vanillaTexure;
        private final Identifier tallTexture;
        private final Identifier longTexture;
        private final Component containerName;

        CursedChest(int slots, Component containerName, Identifier singleTexture, Identifier vanillaTexture, Identifier tallTexture, Identifier longTexture)
        {
            this.slots = slots;
            this.singleTexture = singleTexture;
            this.vanillaTexure = vanillaTexture;
            this.tallTexture = tallTexture;
            this.longTexture = longTexture;
            this.containerName = containerName;
        }
    }
}
