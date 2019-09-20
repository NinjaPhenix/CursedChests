package ninjaphenix.cursedchests.api.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvironmentInterface;
import net.fabricmc.api.EnvironmentInterfaces;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.client.block.ChestAnimationProgress;
import net.minecraft.container.Container;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Identifier;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.*;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import ninjaphenix.cursedchests.api.CursedChestRegistry;
import ninjaphenix.cursedchests.api.block.CursedChestBlock;
import ninjaphenix.cursedchests.api.block.CursedChestType;
import ninjaphenix.cursedchests.api.container.ScrollableContainer;
import ninjaphenix.cursedchests.api.inventory.DoubleSidedInventory;

import java.util.Iterator;
import java.util.List;

@EnvironmentInterfaces({ @EnvironmentInterface(value = EnvType.CLIENT, itf = ChestAnimationProgress.class) })
public class CursedChestBlockEntity extends LootableContainerBlockEntity implements ChestAnimationProgress, Tickable, SidedInventory
{
    private Component defaultContainerName;
    private int inventorySize;
    private DefaultedList<ItemStack> inventory;
    private float animationAngle;
    private float lastAnimationAngle;
    private int viewerCount;
    private int ticksOpen;
    private int[] SLOTS;

    // May be Identifier("null", "null")
    private Identifier block;

    public CursedChestBlockEntity()
    {
        this(Registry.BLOCK_ENTITY.get(new Identifier("cursedchests", "cursed_chest")), new Identifier("null", "null"));
    }

    public CursedChestBlockEntity(Identifier block)
    {
        this(Registry.BLOCK_ENTITY.get(new Identifier("cursedchests", "cursed_chest")), block);
    }

    public CursedChestBlockEntity(BlockEntityType type, Identifier block)
    {
        super(type);
        this.initialize(block);
    }

    private static int tickViewerCount(World world, CursedChestBlockEntity instance, int ticksOpen, int x, int y, int z, int viewCount)
    {
        if (!world.isClient && viewCount != 0 && (ticksOpen + x + y + z) % 200 == 0) { return countViewers(world, instance, x, y, z); }
        return viewCount;
    }

    private static int countViewers(World world, CursedChestBlockEntity instance, int x, int y, int z)
    {
        int viewers = 0;
        List<PlayerEntity> playersInRange = world.getEntities(PlayerEntity.class, new BoundingBox(x - 5, y - 5, z - 5, x + 6, y + 6, z + 6));
        Iterator<PlayerEntity> playerIterator = playersInRange.iterator();
        while (true)
        {
            SidedInventory inventory;
            do
            {
                PlayerEntity player;
                do
                {
                    if (!playerIterator.hasNext()) { return viewers; }
                    player = playerIterator.next();
                } while (!(player.container instanceof ScrollableContainer));
                inventory = ((ScrollableContainer) player.container).getInventory();
            } while (inventory != instance && (!(inventory instanceof DoubleSidedInventory) || !((DoubleSidedInventory) inventory).isPart(instance)));
            viewers++;
        }
    }

    private void initialize(Identifier block)
    {
        this.block = block;
        defaultContainerName = CursedChestRegistry.getDefaultContainerName(block);
        inventorySize = CursedChestRegistry.getSlots(block);
        inventory = DefaultedList.create(inventorySize, ItemStack.EMPTY);
        SLOTS = new int[inventorySize];
        for (int i = 0; i < inventorySize; i++) SLOTS[i] = i;
    }

    @Override
    public boolean onBlockAction(int actionId, int value)
    {
        if (actionId == 1)
        {
            viewerCount = value;
            return true;
        }
        else { return super.onBlockAction(actionId, value); }
    }

    public Identifier getBlock() { return block; }

    public void setBlock(Identifier verticalChestBlock) { block = verticalChestBlock; }

    @Override
    protected DefaultedList<ItemStack> getInvStackList() { return inventory; }

    @Override
    public void setInvStackList(DefaultedList<ItemStack> defaultedList_1) { inventory = defaultedList_1; }

    @Environment(EnvType.CLIENT)
    @Override
    public float getAnimationProgress(float float_1) { return MathHelper.lerp(float_1, lastAnimationAngle, animationAngle); }

    @Override
    protected Container createContainer(int int_1, PlayerInventory playerInventory) { return null; }

    @Override
    public int[] getInvAvailableSlots(Direction direction) { return SLOTS; }

    @Override
    public boolean canInsertInvStack(int slot, ItemStack stack, Direction direction) { return this.isValidInvStack(slot, stack); }

    @Override
    public boolean canExtractInvStack(int slot, ItemStack stack, Direction direction) { return true; }

    @Override
    public int getInvSize() { return inventorySize; }

    @Override
    protected Component getContainerName() { return defaultContainerName; }

    @Override
    public boolean isInvEmpty()
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
    public void fromTag(CompoundTag tag)
    {
        super.fromTag(tag);
        Identifier id = new Identifier(tag.getString("type"));
        this.initialize(id);
        if (!deserializeLootTable(tag)) Inventories.fromTag(tag, inventory);
    }

    @Override
    public CompoundTag toTag(CompoundTag tag)
    {
        super.toTag(tag);
        tag.putString("type", block.toString());
        if (!serializeLootTable(tag)) Inventories.toTag(tag, inventory);
        return tag;
    }

    @Override
    public CompoundTag toInitialChunkDataTag()
    {
        CompoundTag initialChunkTag = super.toTag(new CompoundTag());
        initialChunkTag.putString("type", block.toString());
        return initialChunkTag;
    }

    @Override
    public void tick()
    {
        viewerCount = tickViewerCount(world, this, ++ticksOpen, pos.getX(), pos.getY(), pos.getZ(), viewerCount);
        lastAnimationAngle = animationAngle;
        if (viewerCount > 0 && animationAngle == 0.0F) playSound(SoundEvents.BLOCK_CHEST_OPEN);
        if (viewerCount == 0 && animationAngle > 0.0F || viewerCount > 0 && animationAngle < 1.0F)
        {
            float float_2 = animationAngle;
            if (viewerCount > 0) animationAngle += 0.1F;
            else animationAngle -= 0.1F;
            animationAngle = MathHelper.clamp(animationAngle, 0, 1);
            if (animationAngle < 0.5F && float_2 >= 0.5F) playSound(SoundEvents.BLOCK_CHEST_CLOSE);
        }
    }

    private void playSound(SoundEvent soundEvent)
    {
        CursedChestType chestType = getCachedState().get(CursedChestBlock.TYPE);
        if (!chestType.isRenderedType()) return;
        double zOffset = 0.5;
        if (chestType == CursedChestType.BOTTOM) zOffset = 1;
        BlockPos otherPos = CursedChestBlock.getPairedPos(world, pos);
        Vec3d center = new Vec3d(pos).add(new Vec3d(otherPos == null ? pos : otherPos));
        world.playSound(null, center.getX() / 2 + 0.5D, center.getY() / 2 + 0.5D, center.getZ() / 2 + zOffset, soundEvent, SoundCategory.BLOCKS, 0.5F,
                world.random.nextFloat() * 0.1F + 0.9F);
    }

    @Override
    public void onInvOpen(PlayerEntity player)
    {
        if (player.isSpectator()) return;
        if (viewerCount < 0) viewerCount = 0;
        ++viewerCount;
        onInvOpenOrClose();
    }

    @Override
    public void onInvClose(PlayerEntity player)
    {
        if (player.isSpectator()) return;
        --viewerCount;
        onInvOpenOrClose();
    }

    private void onInvOpenOrClose()
    {
        Block block = getCachedState().getBlock();
        if (block instanceof CursedChestBlock)
        {
            world.addBlockAction(pos, block, 1, viewerCount);
            world.updateNeighborsAlways(pos, block);
        }
    }
}
