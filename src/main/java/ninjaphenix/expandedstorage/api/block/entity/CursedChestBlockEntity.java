package ninjaphenix.expandedstorage.api.block.entity;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import ninjaphenix.expandedstorage.ExpandedStorage;
import ninjaphenix.expandedstorage.api.ExpandedStorageAPI;
import ninjaphenix.expandedstorage.api.Registries;
import ninjaphenix.expandedstorage.api.block.CursedChestBlock;
import ninjaphenix.expandedstorage.api.block.base.entity.OpenableChestBlockEntity;
import ninjaphenix.expandedstorage.api.block.enums.CursedChestType;

public class CursedChestBlockEntity extends OpenableChestBlockEntity
{
    public CursedChestBlockEntity() { this(ExpandedStorage.getId("null")); }

    public CursedChestBlockEntity(Identifier block) { super(ExpandedStorageAPI.CURSED_CHEST, block); }

    @Override
    protected void initialize(Identifier block)
    {
        this.block = block;
        defaultContainerName = Registries.CHEST.get(block).getContainerName();
        inventorySize = Registries.CHEST.get(block).getSlotCount();
        inventory = DefaultedList.ofSize(inventorySize, ItemStack.EMPTY);
        SLOTS = new int[inventorySize];
        for (int i = 0; i < inventorySize; i++) SLOTS[i] = i;
    }

    @Override
    protected void playSound(SoundEvent sound)
    {
        CursedChestType chestType = getCachedState().get(CursedChestBlock.TYPE);
        if (!chestType.isRenderedType()) return;
        double zOffset = 0.5;
        if (chestType == CursedChestType.BOTTOM) zOffset = 1;
        BlockPos otherPos = CursedChestBlock.getPairedPos(world, pos);
        Vec3d center = new Vec3d(pos).add(new Vec3d(otherPos == null ? pos : otherPos));
        world.playSound(null, center.getX() / 2 + 0.5D, center.getY() / 2 + 0.5D, center.getZ() / 2 + zOffset, sound, SoundCategory.BLOCKS, 0.5F,
                world.random.nextFloat() * 0.1F + 0.9F);
    }

    @Override
    protected boolean isValidBlock(Block b) { return b instanceof CursedChestBlock; }
}
