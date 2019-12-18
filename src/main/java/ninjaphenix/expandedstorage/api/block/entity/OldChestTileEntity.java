package ninjaphenix.expandedstorage.api.block.entity;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import ninjaphenix.expandedstorage.ExpandedStorage;
import ninjaphenix.expandedstorage.ModContent;
import ninjaphenix.expandedstorage.api.Registries;

public class OldChestTileEntity extends AbstractChestTileEntity
{

	public OldChestTileEntity()
	{
		this(ExpandedStorage.getRl("null"));
	}

	public OldChestTileEntity(ResourceLocation block) { super(ModContent.OLD_CHEST_TE, block); }

	@Override
	protected void initialize(ResourceLocation block)
	{
		this.block = block;
		Registries.TierData data = Registries.OLD.getValue(block).get();
		defaultContainerName = data.getContainerName();
		inventorySize = data.getSlotCount();
		inventory = NonNullList.withSize(inventorySize, ItemStack.EMPTY);
		SLOTS = new int[inventorySize];
		for (int i = 0; i < inventorySize; i++) SLOTS[i] = i;
	}
}