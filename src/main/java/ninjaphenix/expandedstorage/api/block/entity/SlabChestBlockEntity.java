package ninjaphenix.expandedstorage.api.block.entity;

import net.minecraft.block.Block;
import net.minecraft.block.enums.SlabType;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import ninjaphenix.expandedstorage.ExpandedStorage;
import ninjaphenix.expandedstorage.api.ExpandedStorageAPI;
import ninjaphenix.expandedstorage.api.Registries;
import ninjaphenix.expandedstorage.api.block.SlabChestBlock;
import ninjaphenix.expandedstorage.api.block.base.entity.OpenableChestBlockEntity;

public class SlabChestBlockEntity extends OpenableChestBlockEntity
{
	public SlabChestBlockEntity() { this(ExpandedStorage.getId("null")); }

	public SlabChestBlockEntity(Identifier block) { super(ExpandedStorageAPI.SLAB_CHEST, block); }

	@Override
	protected void initialize(Identifier block)
	{
		this.block = block;
		defaultContainerName = Registries.SLAB.get(block).getContainerName();
		inventorySize = Registries.SLAB.get(block).getSlotCount();
		inventory = DefaultedList.ofSize(inventorySize, ItemStack.EMPTY);
		SLOTS = new int[inventorySize];
		for (int i = 0; i < inventorySize; i++) { SLOTS[i] = i; }
	}

	@Override
	public Text getDisplayName()
	{
		if (!hasCustomName())
		{
			if (getCachedState().get(Properties.SLAB_TYPE) != SlabType.DOUBLE)
			{ return new TranslatableText("container.expandedstorage.generic_slab", getName()); }
		}
		return getName();
	}

	@Override
	protected void playSound(SoundEvent sound)
	{
		SlabType type = getCachedState().get(Properties.SLAB_TYPE);
		double zOffset = 0.5;
		if (type == SlabType.BOTTOM) { zOffset -= 0.25; }
		else if (type == SlabType.TOP) { zOffset += 0.25; }
		world.playSound(null, getPos().getX() + 0.5D, getPos().getY() + 0.5D, getPos().getZ() + zOffset, sound, SoundCategory.BLOCKS, 0.5F,
				world.random.nextFloat() * 0.1F + 0.9F);
	}

	@Override
	protected boolean isValidBlock(Block b) { return b instanceof SlabChestBlock; }

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
		final int invSize = Registries.SLAB.get(block).getSlotCount();
		if (inventorySize == invSize && half) { inventorySize = (int) (invSize / 2.0D); }
		else if (inventorySize != invSize && !half) { inventorySize = invSize; }
		else { return; }
		inventory = DefaultedList.ofSize(inventorySize, ItemStack.EMPTY);
		SLOTS = new int[inventorySize];
		for (int i = 0; i < inventorySize; i++) { SLOTS[i] = i; }
	}
}
