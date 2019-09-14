package ninjaphenix.expandedstorage.api.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvironmentInterface;
import net.fabricmc.api.EnvironmentInterfaces;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.block.ChestAnimationProgress;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Identifier;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import ninjaphenix.expandedstorage.ExpandedStorage;
import ninjaphenix.expandedstorage.api.Registries;
import ninjaphenix.expandedstorage.api.block.CursedChestBlock;
import ninjaphenix.expandedstorage.api.block.enums.CursedChestType;
import ninjaphenix.expandedstorage.api.container.ScrollableContainer;
import ninjaphenix.expandedstorage.api.inventory.DoubleSidedInventory;

import java.util.Iterator;
import java.util.List;

@EnvironmentInterfaces({ @EnvironmentInterface(value = EnvType.CLIENT, itf = ChestAnimationProgress.class) })
public class CursedChestBlockEntity extends AbstractChestBlockEntity implements ChestAnimationProgress, Tickable
{
    private float animationAngle;
    private float lastAnimationAngle;
    private int viewerCount;
    private int ticksOpen;

    public CursedChestBlockEntity() { this(Registry.BLOCK_ENTITY.get(ExpandedStorage.getId("cursed_chest")), ExpandedStorage.getId("null")); }

    public CursedChestBlockEntity(Identifier block) { this(Registry.BLOCK_ENTITY.get(ExpandedStorage.getId("cursed_chest")), block); }

    public CursedChestBlockEntity(BlockEntityType type, Identifier block)
    {
        super(type, block);
    }

    private static int tickViewerCount(World world, CursedChestBlockEntity instance, int ticksOpen, int x, int y, int z, int viewCount)
    {
        if (!world.isClient && viewCount != 0 && (ticksOpen + x + y + z) % 200 == 0) { return countViewers(world, instance, x, y, z); }
        return viewCount;
    }

    private static int countViewers(World world, CursedChestBlockEntity instance, int x, int y, int z)
    {
        int viewers = 0;
        List<PlayerEntity> playersInRange = world.getNonSpectatingEntities(PlayerEntity.class, new Box(x - 5, y - 5, z - 5, x + 6, y + 6, z + 6));
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

    @Override
    protected void initialize(Identifier block)
    {
        this.block = block;
        defaultContainerName = Registries.MODELED.get(block).getContainerName();
        inventorySize = Registries.MODELED.get(block).getSlotCount();
        inventory = DefaultedList.ofSize(inventorySize, ItemStack.EMPTY);
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

    @Environment(EnvType.CLIENT)
    @Override
    public float getAnimationProgress(float float_1) { return MathHelper.lerp(float_1, lastAnimationAngle, animationAngle); }

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
