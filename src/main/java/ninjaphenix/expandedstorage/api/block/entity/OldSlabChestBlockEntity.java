package ninjaphenix.expandedstorage.api.block.entity;

import net.minecraft.block.enums.SlabType;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import ninjaphenix.expandedstorage.ExpandedStorage;
import ninjaphenix.expandedstorage.api.ExpandedStorageAPI;
import ninjaphenix.expandedstorage.api.Registries;
import ninjaphenix.expandedstorage.api.block.base.entity.AbstractChestBlockEntity;

public class OldSlabChestBlockEntity extends AbstractChestBlockEntity
{
    public OldSlabChestBlockEntity() { this(ExpandedStorage.getId("null")); }

    public OldSlabChestBlockEntity(Identifier block) { super(ExpandedStorageAPI.OLD_SLAB_CHEST, block); }

    @Override
    protected void initialize(Identifier block)
    {
        this.block = block;
        defaultContainerName = Registries.OLD_SLAB.get(block).getContainerName();
        inventorySize = Registries.OLD_SLAB.get(block).getSlotCount();
        inventory = DefaultedList.ofSize(inventorySize, ItemStack.EMPTY);
        SLOTS = new int[inventorySize];
        for (int i = 0; i < inventorySize; i++) SLOTS[i] = i;
    }

    @Override
    public Text getDisplayName()
    {
        if (!hasCustomName())
            if (getCachedState().get(Properties.SLAB_TYPE) != SlabType.DOUBLE)
                return new TranslatableText("container.expandedstorage.generic_slab", getName());
        return getName();
    }

    @Override
    public int[] getInvAvailableSlots(Direction side)
    {
        validateInventorySize();
        return super.getInvAvailableSlots(side);
    }

    @Override
    public int getInvSize()
    {
        validateInventorySize();
        return super.getInvSize();
    }

    private void validateInventorySize()
    {
        final boolean half = getCachedState().get(Properties.SLAB_TYPE) != SlabType.DOUBLE;
        final int invSize = Registries.OLD_SLAB.get(block).getSlotCount();
        if (inventorySize == invSize && half) inventorySize = (int) (invSize / 2.0D);
        else if (inventorySize != invSize && !half) inventorySize = invSize;
        else return;
        inventory = DefaultedList.ofSize(inventorySize, ItemStack.EMPTY);
        SLOTS = new int[inventorySize];
        for (int i = 0; i < inventorySize; i++) SLOTS[i] = i;
    }
}
