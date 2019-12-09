package ninjaphenix.expandedstorage.api.block.base.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvironmentInterface;
import net.fabricmc.api.EnvironmentInterfaces;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.block.ChestAnimationProgress;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import ninjaphenix.expandedstorage.api.container.ScrollableContainer;
import ninjaphenix.expandedstorage.api.inventory.DoubleSidedInventory;

import java.util.Iterator;
import java.util.List;

@EnvironmentInterfaces({ @EnvironmentInterface(value = EnvType.CLIENT, itf = ChestAnimationProgress.class) })
public abstract class OpenableChestBlockEntity extends AbstractChestBlockEntity implements ChestAnimationProgress, Tickable
{
    private float animationAngle;
    private float lastAnimationAngle;
    private int viewerCount;
    private int ticksOpen;

    protected OpenableChestBlockEntity(BlockEntityType type, Identifier block)
    {
        super(type, block);
    }

    @Environment(EnvType.CLIENT)
    @Override
    public float getAnimationProgress(float f) { return MathHelper.lerp(f, lastAnimationAngle, animationAngle); }

    @Override
    public void tick()
    {
        viewerCount = countViewers(world, this, ++ticksOpen, pos.getX(), pos.getY(), pos.getZ(), viewerCount);
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

    private int countViewers(World world, OpenableChestBlockEntity instance, int ticksOpen, int x, int y, int z, int viewCount)
    {
        if (!world.isClient && viewCount != 0 && (ticksOpen + x + y + z) % 200 == 0)
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
        return viewCount;
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

    protected abstract void playSound(SoundEvent sound);

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
        if (isValidBlock(block))
        {
            world.addBlockAction(pos, block, 1, viewerCount);
            world.updateNeighborsAlways(pos, block);
        }
    }

    protected abstract boolean isValidBlock(Block b);
}
