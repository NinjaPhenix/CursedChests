package ninjaphenix.expandedstorage.api.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;

public class IDoubleSidedInventory implements ISidedInventory
{
	private final ISidedInventory first;
	private final ISidedInventory second;

	public IDoubleSidedInventory(ISidedInventory firstInventory, ISidedInventory secondInventory)
	{
		if (firstInventory == null) { firstInventory = secondInventory; }
		if (secondInventory == null) { secondInventory = firstInventory; }
		first = firstInventory;
		second = secondInventory;
	}

	@Override
	public int[] getSlotsForFace(final Direction direction)
	{
		final int[] firstSlots = first.getSlotsForFace(direction);
		final int[] secondSlots = second.getSlotsForFace(direction);
		final int[] combined = new int[firstSlots.length + secondSlots.length];
		int index = 0;
		for (int slot : firstSlots) { combined[index++] = slot; }
		for (int slot : secondSlots) { combined[index++] = slot + first.getSizeInventory(); }
		return combined;
	}

	@Override
	public boolean canInsertItem(int slot, final ItemStack stack, final Direction direction)
	{
		if (slot >= first.getSizeInventory()) { return second.canInsertItem(slot - first.getSizeInventory(), stack, direction); }
		return first.canInsertItem(slot, stack, direction);
	}

	@Override
	public boolean canExtractItem(int slot, final ItemStack stack, final Direction direction)
	{
		if (slot >= first.getSizeInventory()) { return second.canExtractItem(slot - first.getSizeInventory(), stack, direction); }
		return first.canExtractItem(slot, stack, direction);
	}

	@Override
	public int getSizeInventory() { return first.getSizeInventory() + second.getSizeInventory(); }

	@Override
	public boolean isEmpty() { return first.isEmpty() && second.isEmpty(); }

	@Override
	public boolean isUsableByPlayer(final PlayerEntity player) { return first.isUsableByPlayer(player) && second.isUsableByPlayer(player); }

	@Override
	public void clear()
	{
		first.clear();
		second.clear();
	}

	@Override
	public void markDirty()
	{
		first.markDirty();
		second.markDirty();
	}

	@Override
	public void openInventory(final PlayerEntity player)
	{
		first.openInventory(player);
		second.openInventory(player);
	}

	@Override
	public void closeInventory(final PlayerEntity player)
	{
		first.closeInventory(player);
		second.closeInventory(player);
	}

	public boolean isPart(final ISidedInventory inventory) { return first == inventory || second == inventory; }

	@Override
	public int getInventoryStackLimit() { return first.getInventoryStackLimit(); }

	@Override
	public ItemStack getStackInSlot(int slot)
	{
		if (slot >= first.getSizeInventory()) { return second.getStackInSlot(slot - first.getSizeInventory()); }
		return first.getStackInSlot(slot);
	}

	@Override
	public ItemStack decrStackSize(int slot, int amount)
	{
		if (slot >= first.getSizeInventory()) { return second.decrStackSize(slot - first.getSizeInventory(), amount); }
		return first.decrStackSize(slot, amount);
	}

	@Override
	public ItemStack removeStackFromSlot(int slot)
	{
		if (slot >= first.getSizeInventory()) { return second.removeStackFromSlot(slot - first.getSizeInventory()); }
		return first.removeStackFromSlot(slot);
	}

	@Override
	public void setInventorySlotContents(int slot, final ItemStack stack)
	{
		if (slot >= first.getSizeInventory()) { second.setInventorySlotContents(slot - first.getSizeInventory(), stack); }
		else { first.setInventorySlotContents(slot, stack); }
	}

	@Override
	public boolean isItemValidForSlot(int slot, final ItemStack stack)
	{
		if (slot >= first.getSizeInventory()) { return second.isItemValidForSlot(slot - first.getSizeInventory(), stack); }
		return first.isItemValidForSlot(slot, stack);
	}
}
