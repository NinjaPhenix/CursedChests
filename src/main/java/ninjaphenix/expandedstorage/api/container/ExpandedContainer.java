package ninjaphenix.expandedstorage.api.container;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.container.Container;
import net.minecraft.container.Slot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Nameable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;


public class ExpandedContainer extends Container implements Nameable
{
    private final Text containerName;
    private final SidedInventory inventory;
    private int originX, originY, width, height, slotStartIndex;
    @Environment(EnvType.CLIENT) private String searchTerm = "";

    public ExpandedContainer(int syncId, PlayerInventory playerInventory, SidedInventory inventory, Text containerName)
    {
        super(null, syncId);
        width = 9;
        height = (int) Math.ceil((double)inventory.getInvSize() / width);
        this.inventory = inventory;
        this.containerName = containerName;
        inventory.onInvOpen(playerInventory.player);
        int sX = 0, sY = 0;
        for (int i = 0; i < inventory.getInvSize(); i++)
        {
            addSlot(new Slot(inventory, i, 8 + sX * 18, 19 + sY * 18));

            sX = (sX + 1) % width; if (sX == 0) sY++;
        }
        int left = 8 + (int) (((double) width/2) * 18) - 81;
        int top = height * 18 + 19 + 14;
        for (int y = 0; y < 3; ++y) for (int x = 0; x < 9; ++x) addSlot(new Slot(playerInventory, x + y * 9 + 9, left + 18*x, y * 18 + top));
        top += 58;
        for (int x = 0; x < 9; ++x) addSlot(new Slot(playerInventory, x, left + 18*x, top));
    }

    @Override
    public boolean canUse(PlayerEntity player) { return inventory.canPlayerUseInv(player); }

    @Override
    public void close(PlayerEntity player)
    {
        super.close(player);
        inventory.onInvClose(player);
    }

    @Override
    public ItemStack transferSlot(PlayerEntity player, int slotIndex)
    {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = slotList.get(slotIndex);
        if (slot != null && slot.hasStack())
        {
            ItemStack slotStack = slot.getStack();
            stack = slotStack.copy();
            if (slotIndex < inventory.getInvSize()) { if (!insertItem(slotStack, inventory.getInvSize(), slotList.size(), true)) return ItemStack.EMPTY; }
            else if (!insertItem(slotStack, 0, inventory.getInvSize(), false)) return ItemStack.EMPTY;
            if (slotStack.isEmpty()) slot.setStack(ItemStack.EMPTY);
            else slot.markDirty();
        }
        return stack;
    }

    @Override
    public Text getName() { return containerName; }

    public SidedInventory getInventory() { return inventory; }

    public int getWidth() { return width; }

    public int getHeight() { return height; }
}
