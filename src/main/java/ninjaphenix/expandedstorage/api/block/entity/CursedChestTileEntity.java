package ninjaphenix.expandedstorage.api.block.entity;

import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.IChestLid;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import ninjaphenix.expandedstorage.ExpandedStorage;
import ninjaphenix.expandedstorage.ModContent;
import ninjaphenix.expandedstorage.api.Registries;
import ninjaphenix.expandedstorage.api.block.CursedChestBlock;
import ninjaphenix.expandedstorage.api.block.enums.CursedChestType;
import ninjaphenix.expandedstorage.api.container.ScrollableContainer;
import ninjaphenix.expandedstorage.api.inventory.IDoubleSidedInventory;

import java.util.Iterator;
import java.util.List;

@OnlyIn(
		value = Dist.CLIENT,
		_interface = IChestLid.class
)
public class CursedChestTileEntity extends AbstractChestTileEntity implements IChestLid, ITickableTileEntity
{
	private float animationAngle;
	private float lastAnimationAngle;
	private int viewerCount;
	private int ticksOpen;

	public CursedChestTileEntity() { this(ExpandedStorage.getRl("null")); }

	public CursedChestTileEntity(ResourceLocation block) { super(ModContent.CURSED_CHEST_TE, block); }

	private static int tickViewerCount(World world, CursedChestTileEntity instance, int ticksOpen, int x, int y, int z, int viewCount)
	{
		if (!world.isRemote && viewCount != 0 && (ticksOpen + x + y + z) % 200 == 0) { return countViewers(world, instance, x, y, z); }
		return viewCount;
	}

	private static int countViewers(World world, CursedChestTileEntity instance, int x, int y, int z)
	{
		int viewers = 0;
		List<PlayerEntity> playersInRange = world.getEntitiesWithinAABB(PlayerEntity.class, new AxisAlignedBB(x - 5, y - 5, z - 5, x + 6, y + 6, z + 6));
		Iterator<PlayerEntity> playerIterator = playersInRange.iterator();
		while (true)
		{
			IInventory inventory;
			do
			{
				PlayerEntity player;
				do
				{
					if (!playerIterator.hasNext()) { return viewers; }
					player = playerIterator.next();
				} while (!(player.openContainer instanceof ScrollableContainer));
				inventory = ((ScrollableContainer) player.openContainer).getInv();
			} while (inventory != instance && (!(inventory instanceof IDoubleSidedInventory) || !((IDoubleSidedInventory) inventory).isPart(instance)));
			viewers++;
		}
	}

	@Override
	protected void initialize(ResourceLocation block)
	{
		this.block = block;
		Registries.ModeledTierData data = Registries.MODELED.getValue(block).get();
		defaultContainerName = data.getContainerName();
		inventorySize = data.getSlotCount();
		inventory = NonNullList.withSize(inventorySize, ItemStack.EMPTY);
		SLOTS = new int[inventorySize];
		for (int i = 0; i < inventorySize; i++) SLOTS[i] = i;
	}


	@Override
	public boolean receiveClientEvent(int actionId, int value)
	{
		if (actionId == 1)
		{
			viewerCount = value;
			return true;
		}
		else { return super.receiveClientEvent(actionId, value); }
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public float getLidAngle(float float_1) { return MathHelper.lerp(float_1, lastAnimationAngle, animationAngle); }

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
		CursedChestType chestType = getBlockState().get(CursedChestBlock.TYPE);
		if (!chestType.isRenderedType()) return;
		double zOffset = 0.5;
		if (chestType == CursedChestType.BOTTOM) zOffset = 1;
		BlockPos otherPos = CursedChestBlock.getPairedPos(world, pos);
		Vec3d center = new Vec3d(pos).add(new Vec3d(otherPos == null ? pos : otherPos));
		world.playSound(null, center.getX() / 2 + 0.5D, center.getY() / 2 + 0.5D, center.getZ() / 2 + zOffset, soundEvent, SoundCategory.BLOCKS, 0.5F,
				world.rand.nextFloat() * 0.1F + 0.9F);
	}

	@Override
	public void openInventory(PlayerEntity player)
	{
		if (player.isSpectator()) return;
		if (viewerCount < 0) viewerCount = 0;
		++viewerCount;
		onInvOpenOrClose();
	}

	@Override
	public void closeInventory(PlayerEntity player)
	{
		if (player.isSpectator()) return;
		--viewerCount;
		onInvOpenOrClose();
	}

	private void onInvOpenOrClose()
	{
		Block block = getBlockState().getBlock();
		if (block instanceof CursedChestBlock)
		{
			world.addBlockEvent(pos, block, 1, viewerCount);
			world.notifyNeighbors(pos, block);
		}
	}
}