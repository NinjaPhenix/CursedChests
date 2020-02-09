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

@SuppressWarnings({ "WeakerAccess", "NullableProblems" })
public abstract class AbstractChestTileEntity extends LockableLootTileEntity implements ISidedInventory
{
	protected ITextComponent defaultContainerName;
	protected int inventorySize;
	protected NonNullList<ItemStack> inventory;

	@Override
	protected Container createMenu(final int id, final PlayerInventory player) { return null; }

	protected int[] SLOTS;

	// May be "null:null"
	protected ResourceLocation block;

	public AbstractChestTileEntity(final TileEntityType type, final ResourceLocation block)
	{
		super(type);
		this.initialize(block);
	}

	@Override
	protected ITextComponent getDefaultName() { return defaultContainerName; }

	protected void initialize(final ResourceLocation block) { }

	public ResourceLocation getBlock() { return block; }

	public void setBlock(final ResourceLocation block) { this.block = block; }

	@Override
	protected NonNullList<ItemStack> getItems() { return inventory; }

	@Override
	public void setItems(NonNullList<ItemStack> inventory) { this.inventory = inventory; }

	@Override
	public int[] getSlotsForFace(final Direction direction) { return SLOTS; }

	@Override
	public boolean canInsertItem(final int slot, final ItemStack stack, final Direction direction) { return this.isItemValidForSlot(slot, stack); }

	@Override
	public boolean canExtractItem(final int slot, final ItemStack stack, final Direction direction) { return true; }

	@Override
	public int getSizeInventory() { return inventorySize; }

	@Override
	public boolean isEmpty() { return inventory.stream().allMatch(ItemStack::isEmpty); }

	@Override
	public void read(final CompoundNBT tag)
	{
		super.read(tag);
		ResourceLocation id = new ResourceLocation(tag.getString("type"));
		this.initialize(id);
		if (!checkLootAndRead(tag)) { ItemStackHelper.loadAllItems(tag, inventory); }
	}

	@Override
	public CompoundNBT write(final CompoundNBT tag)
	{
		super.write(tag);
		tag.putString("type", block.toString());
		if (!checkLootAndWrite(tag)) { ItemStackHelper.saveAllItems(tag, inventory); }
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