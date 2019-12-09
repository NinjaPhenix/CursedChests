package ninjaphenix.expandedstorage.api.block.entity;

import net.minecraft.item.ItemStack;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Identifier;
import ninjaphenix.expandedstorage.ExpandedStorage;
import ninjaphenix.expandedstorage.api.ExpandedStorageAPI;
import ninjaphenix.expandedstorage.api.Registries;
import ninjaphenix.expandedstorage.api.block.base.entity.AbstractChestBlockEntity;

public class OldChestBlockEntity extends AbstractChestBlockEntity
{

    public OldChestBlockEntity() { this(ExpandedStorage.getId("null")); }

    public OldChestBlockEntity(Identifier block) { super(ExpandedStorageAPI.OLD_CURSED_CHEST, block); }

    @Override
    protected void initialize(Identifier block)
    {
        this.block = block;
        defaultContainerName = Registries.OLD.get(block).getContainerName();
        inventorySize = Registries.OLD.get(block).getSlotCount();
        inventory = DefaultedList.ofSize(inventorySize, ItemStack.EMPTY);
        SLOTS = new int[inventorySize];
        for (int i = 0; i < inventorySize; i++) SLOTS[i] = i;
    }
}
