package ninjaphenix.expandedstorage.api.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.network.IContainerFactory;
import ninjaphenix.expandedstorage.ExpandedStorage;
import ninjaphenix.expandedstorage.ModContent;

import java.lang.reflect.Field;
import java.util.Arrays;

public class ScrollableContainer extends Container
{
	private final ITextComponent containerName;
	private final IInventory inventory;
	private final int rows;
	private final int realRows;
	// sideonly client
	private String searchTerm = "";
	// sideonly client
	private Integer[] unsortedToSortedSlotMap;

	public ScrollableContainer(int windowId, PlayerInventory playerInventory, IInventory inventory, ITextComponent containerName)
	{
		super(ModContent.SCROLLABLE_CONTAINER_TYPE, windowId);
		this.inventory = inventory;
		this.containerName = containerName;
		realRows = inventory.getSizeInventory() / 9;
		rows = Math.min(realRows, 6);
		if (FMLLoader.getDist() == Dist.CLIENT) unsortedToSortedSlotMap = new Integer[realRows * 9];
		int int_3 = (rows - 4) * 18;
		inventory.openInventory(playerInventory.player);
		for (int y = 0; y < realRows; ++y)
		{
			int yPos = y < rows ? 18 + y * 18 : -2000;
			for (int x = 0; x < 9; ++x)
			{
				int slot = x + 9 * y;
				if (FMLLoader.getDist() == Dist.CLIENT) unsortedToSortedSlotMap[slot] = slot;
				addSlot(new Slot(inventory, slot, 8 + x * 18, yPos));
			}
		}
		for (int y = 0; y < 3; ++y) for (int x = 0; x < 9; ++x) addSlot(new Slot(playerInventory, x + y * 9 + 9, 8 + x * 18, 103 + y * 18 + int_3));
		for (int x = 0; x < 9; ++x) addSlot(new Slot(playerInventory, x, 8 + x * 18, 161 + int_3));
	}

	public ScrollableContainer(int windowId, PlayerInventory playerInventory)
	{
		this(windowId, playerInventory, new Inventory(0), new TranslationTextComponent("error"));
	}

	public IInventory getInv() { return inventory; }

	@OnlyIn(Dist.CLIENT)
	public int getRows() { return realRows; }

	@OnlyIn(Dist.CLIENT)
	public ITextComponent getDisplayName() { return containerName; }

	@Override
	public boolean canInteractWith(PlayerEntity player) { return inventory.isUsableByPlayer(player); }

	@Override
	public void onContainerClosed(PlayerEntity player)
	{
		super.onContainerClosed(player);
		inventory.closeInventory(player);
	}

	@OnlyIn(Dist.CLIENT)
	public void setSearchTerm(String term)
	{
		searchTerm = term.toLowerCase();
		updateSlotPositions(0, true);
	}

	@OnlyIn(Dist.CLIENT)
	public void updateSlotPositions(int offset, boolean termChanged)
	{
		int index = 0;
		if (termChanged && !searchTerm.equals("")) Arrays.sort(unsortedToSortedSlotMap, this::compare);
		else if (termChanged) Arrays.sort(unsortedToSortedSlotMap);
		try
		{
			Field xField = Slot.class.getField("field_75223_e");
			Field yField = Slot.class.getField("field_75221_f");

			for (Integer slotID : unsortedToSortedSlotMap)
			{
				Slot slot = inventorySlots.get(slotID);
				int y = (index / 9) - offset;
				if (!xField.isAccessible()) xField.setAccessible(true);
				if (!yField.isAccessible()) yField.setAccessible(true);
				xField.set(slot, 8 + 18 * (index % 9));
				yField.set(slot, (y >= rows || y < 0) ? -2000 : 18 + 18 * y);
				index++;
			}
		}
		catch (NoSuchFieldException | IllegalAccessException ignored)
		{
			ExpandedStorage.LOGGER
					.error("[Expanded Storage] Could not find slot fields, scrolling will not work. Please report to NinjaPhenix on curseforge or github.");
		}

	}

	private int compare(Integer a, Integer b)
	{
		if (a == null || b == null) return 0;
		final ItemStack stack_a = inventorySlots.get(a).getStack();
		final ItemStack stack_b = inventorySlots.get(b).getStack();
		if (stack_a.isEmpty() && !stack_b.isEmpty()) return 1;
		if (!stack_a.isEmpty() && stack_b.isEmpty()) return -1;
		if (stack_a.isEmpty()) return 0; // && stack_b.isEmpty() -- unneeded
		final boolean stack_a_matches = stack_a.getDisplayName().getString().toLowerCase().contains(searchTerm);
		final boolean stack_b_matches = stack_b.getDisplayName().getString().toLowerCase().contains(searchTerm);
		return stack_a_matches && stack_b_matches ? 0 : stack_b_matches ? 1 : -1;
	}

	@Override
	public ItemStack transferStackInSlot(PlayerEntity player, int slotIndex)
	{
		ItemStack stack = ItemStack.EMPTY;
		Slot slot = inventorySlots.get(slotIndex);
		if (slot != null && slot.getHasStack())
		{
			ItemStack slotStack = slot.getStack();
			stack = slotStack.copy();
			if (slotIndex < inventory.getSizeInventory())
			{
				if (!mergeItemStack(slotStack, inventory.getSizeInventory(), inventorySlots.size(), true)) return ItemStack.EMPTY;
			}
			else if (!mergeItemStack(slotStack, 0, inventory.getSizeInventory(), false))
			{
				return ItemStack.EMPTY;
			}
			if (slotStack.isEmpty()) slot.putStack(ItemStack.EMPTY);
			else slot.onSlotChanged();
		}
		return stack;
	}

	public static class Factory implements IContainerFactory<ScrollableContainer>
	{

		@Override
		public ScrollableContainer create(int windowId, PlayerInventory inv, PacketBuffer data)
		{
			int invSize = data.readInt();
			ITextComponent containerName = data.readTextComponent();
			return new ScrollableContainer(windowId, inv, new Inventory(invSize), containerName);
		}
	}
}