package ninjaphenix.expandedstorage.api.container;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.container.Container;
import net.minecraft.container.Slot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.Arrays;

public class ScrollableContainer extends Container
{
    private final Text containerName;
    private final SidedInventory inventory;
    private final int rows;
    private final int realRows;
    @Environment(EnvType.CLIENT) private String searchTerm = "";
    @Environment(EnvType.CLIENT) private Integer[] unsortedToSortedSlotMap;

    public ScrollableContainer(int syncId, PlayerInventory playerInventory, SidedInventory inventory, Text containerName)
    {
        super(null, syncId);
        this.inventory = inventory;
        this.containerName = containerName;
        realRows = inventory.getInvSize() / 9;
        rows = realRows > 6 ? 6 : realRows;
        // todo eval if fabric loader removes this statement on server side
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) unsortedToSortedSlotMap = new Integer[realRows * 9];
        int int_3 = (rows - 4) * 18;
        inventory.onInvOpen(playerInventory.player);
        for (int y = 0; y < realRows; ++y)
        {
            int yPos = -2000;
            if (y < rows) yPos = 18 + y * 18;
            for (int x = 0; x < 9; ++x)
            {
                int slot = x + 9 * y;
                if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) unsortedToSortedSlotMap[slot] = slot;
                addSlot(new Slot(inventory, slot, 8 + x * 18, yPos));
            }
        }
        for (int y = 0; y < 3; ++y) for (int x = 0; x < 9; ++x) addSlot(new Slot(playerInventory, x + y * 9 + 9, 8 + x * 18, 103 + y * 18 + int_3));
        for (int x = 0; x < 9; ++x) addSlot(new Slot(playerInventory, x, 8 + x * 18, 161 + int_3));
    }

    public SidedInventory getInventory() { return inventory; }

    @Environment(EnvType.CLIENT)
    public int getRows() { return realRows; }

    @Environment(EnvType.CLIENT)
    public Text getDisplayName() { return containerName; }

    @Override
    public boolean canUse(PlayerEntity player) { return inventory.canPlayerUseInv(player); }

    @Override
    public void close(PlayerEntity player)
    {
        super.close(player);
        inventory.onInvClose(player);
    }

    @Environment(EnvType.CLIENT)
    public void setSearchTerm(String term)
    {
        searchTerm = term.toLowerCase();
        updateSlotPositions(0, true);
    }

    @Environment(EnvType.CLIENT)
    public void updateSlotPositions(int offset, boolean termChanged)
    {
        int index = 0;
        if (termChanged && !searchTerm.equals("")) Arrays.sort(unsortedToSortedSlotMap, this::compare);
        else if (termChanged) Arrays.sort(unsortedToSortedSlotMap);
        for (Integer slotID : unsortedToSortedSlotMap)
        {
            Slot slot = slotList.get(slotID);
            int y = (index / 9) - offset;
            slot.xPosition = 8 + 18 * (index % 9);
            slot.yPosition = (y >= rows || y < 0) ? -2000 : 18 + 18 * y;
            index++;
        }
    }

    private int compare(Integer a, Integer b)
    {
        if (a == null || b == null) return 0;
        ItemStack stack_a = slotList.get(a).getStack();
        ItemStack stack_b = slotList.get(b).getStack();
        if (stack_a.isEmpty() && !stack_b.isEmpty()) return 1;
        if (!stack_a.isEmpty() && stack_b.isEmpty()) return -1;
        if (stack_a.isEmpty() && stack_b.isEmpty()) return 0;
        return stack_a.getName().getString().toLowerCase().contains(searchTerm) ? -1 : stack_b.getName().getString().toLowerCase().contains(searchTerm) ? 1 : 0;
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
}
