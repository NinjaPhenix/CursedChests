package ninjaphenix.expandedstorage.mixins;

import net.minecraft.container.Slot;
import ninjaphenix.expandedstorage.accessors.SlotAccessor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Slot.class)
public class SlotMixin implements SlotAccessor
{

	@Mutable @Shadow @Final public int xPosition;
	@Mutable @Shadow @Final public int yPosition;

	@Override
	public void setX(int x) { xPosition = x; }

	@Override
	public void setY(int y) { yPosition = y; }
}
