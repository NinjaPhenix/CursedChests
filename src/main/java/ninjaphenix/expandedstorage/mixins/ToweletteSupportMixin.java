package ninjaphenix.expandedstorage.mixins;

import ninjaphenix.expandedstorage.api.block.FluidLoggableChestBlock;
import org.spongepowered.asm.mixin.Mixin;
import virtuoel.towelette.api.Fluidloggable;

@Mixin(FluidLoggableChestBlock.class)
public class ToweletteSupportMixin implements Fluidloggable {}