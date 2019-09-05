package ninjaphenix.expandedstorage.api.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;

public class DoubleSidedInventory implements SidedInventory
{
    private final SidedInventory first;
    private final SidedInventory second;

    public DoubleSidedInventory(SidedInventory firstInventory, SidedInventory secondInventory)
    {
        if (firstInventory == null) firstInventory = secondInventory;
        if (secondInventory == null) secondInventory = firstInventory;
        first = firstInventory;
        second = secondInventory;
    }

    // maybe look for a better implementation?
    @Override
    public int[] getInvAvailableSlots(Direction direction)
    {
        int[] firstSlots = first.getInvAvailableSlots(direction);
        int[] secondSlots = second.getInvAvailableSlots(direction);
        int[] combined = new int[firstSlots.length + secondSlots.length];
        int index = 0;
        for (int slot : firstSlots) combined[index++] = slot;
        for (int slot : secondSlots) combined[index++] = slot + first.getInvSize();
        return combined;
    }

    @Override
    public boolean canInsertInvStack(int slot, ItemStack stack, Direction direction)
    {
        if (slot >= first.getInvSize()) return second.canInsertInvStack(slot - first.getInvSize(), stack, direction);
        return first.canInsertInvStack(slot, stack, direction);
    }

    @Override
    public boolean canExtractInvStack(int slot, ItemStack stack, Direction direction)
    {
        if (slot >= first.getInvSize()) return second.canExtractInvStack(slot - first.getInvSize(), stack, direction);
        return first.canExtractInvStack(slot, stack, direction);
    }

    @Override
    public int getInvSize() { return first.getInvSize() + second.getInvSize(); }

    @Override
    public boolean isInvEmpty() { return first.isInvEmpty() && second.isInvEmpty(); }

    @Override
    public boolean canPlayerUseInv(PlayerEntity player) { return first.canPlayerUseInv(player) && second.canPlayerUseInv(player); }

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
    public void onInvOpen(PlayerEntity player)
    {
        first.onInvOpen(player);
        second.onInvOpen(player);
    }

    @Override
    public void onInvClose(PlayerEntity player)
    {
        first.onInvClose(player);
        second.onInvClose(player);
    }

    public boolean isPart(SidedInventory inventory) { return first == inventory || second == inventory; }

    public int getInvMaxStackAmount() { return first.getInvMaxStackAmount(); }

    @Override
    public ItemStack getInvStack(int slot)
    {
        if (slot >= first.getInvSize()) return second.getInvStack(slot - first.getInvSize());
        return first.getInvStack(slot);
    }

    @Override
    public ItemStack takeInvStack(int slot, int amount)
    {
        if (slot >= first.getInvSize()) return second.takeInvStack(slot - first.getInvSize(), amount);
        return first.takeInvStack(slot, amount);
    }

    @Override
    public ItemStack removeInvStack(int slot)
    {
        if (slot >= first.getInvSize()) return second.removeInvStack(slot - first.getInvSize());
        return first.removeInvStack(slot);
    }

    @Override
    public void setInvStack(int slot, ItemStack stack)
    {
        if (slot >= first.getInvSize()) second.setInvStack(slot - first.getInvSize(), stack);
        else first.setInvStack(slot, stack);
    }

    @Override
    public boolean isValidInvStack(int slot, ItemStack stack)
    {
        if (slot >= first.getInvSize()) return second.isValidInvStack(slot - first.getInvSize(), stack);
        return first.isValidInvStack(slot, stack);
    }
}
