package ninjaphenix.expandedstorage.api.block.entity;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import java.util.Iterator;

@SuppressWarnings({ "WeakerAccess", "NullableProblems" })
public abstract class AbstractChestTileEntity extends LockableLootTileEntity implements ISidedInventory
{
	protected ITextComponent defaultContainerName;
	protected int inventorySize;
	protected NonNullList<ItemStack> inventory;

	@Override
	protected Container createMenu(int id, PlayerInventory player) { return null; }

	protected int[] SLOTS;

	// May be "null:null"
	protected ResourceLocation block;

	public AbstractChestTileEntity(TileEntityType type, ResourceLocation block)
	{
		super(type);
		this.initialize(block);
	}

	@Override
	protected ITextComponent getDefaultName() { return defaultContainerName; }

	protected void initialize(ResourceLocation block) { }

	public ResourceLocation getBlock() { return block; }

	public void setBlock(ResourceLocation block) { this.block = block; }

	@Override
	protected NonNullList<ItemStack> getItems() { return inventory; }

	@Override
	public void setItems(NonNullList<ItemStack> inventory) { this.inventory = inventory; }

	@Override
	public int[] getSlotsForFace(Direction direction) { return SLOTS; }

	@Override
	public boolean canInsertItem(int slot, ItemStack stack, Direction direction) { return this.isItemValidForSlot(slot, stack); }

	@Override
	public boolean canExtractItem(int slot, ItemStack stack, Direction direction) { return true; }

	@Override
	public int getSizeInventory() { return inventorySize; }

	@Override
	public boolean isEmpty()
	{
		Iterator<ItemStack> inventoryIterator = inventory.iterator();
		ItemStack stack;
		do
		{
			if (!inventoryIterator.hasNext()) return true;
			stack = inventoryIterator.next();
		} while (stack.isEmpty());
		return false;
	}

	@Override
	public void read(CompoundNBT tag)
	{
		super.read(tag);
		ResourceLocation id = new ResourceLocation(tag.getString("type"));
		this.initialize(id);
		if (!checkLootAndRead(tag)) ItemStackHelper.loadAllItems(tag, inventory);
	}

	@Override
	public CompoundNBT write(CompoundNBT tag)
	{
		super.write(tag);
		tag.putString("type", block.toString());
		if (!checkLootAndWrite(tag)) ItemStackHelper.saveAllItems(tag, inventory);
		return tag;
	}

	@Override
	public CompoundNBT getUpdateTag()
	{
		CompoundNBT tag = this.write(new CompoundNBT());
		tag.putString("type", block.toString());
		return tag;
	}
}