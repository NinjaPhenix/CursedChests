package ninjaphenix.expandedstorage.mixins;

import ninjaphenix.expandedstorage.api.block.base.AbstractSlabChestBlock;
import ninjaphenix.expandedstorage.api.block.base.FluidLoggableChestBlock;
import org.spongepowered.asm.mixin.Mixin;
import virtuoel.towelette.api.Fluidloggable;

@Mixin({ FluidLoggableChestBlock.class, AbstractSlabChestBlock.class })
public class ToweletteSupportMixin implements Fluidloggable {}
