package ninjaphenix.expandedstorage.mixins;

import net.minecraft.util.Identifier;
import ninjaphenix.expandedstorage.ExpandedStorage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Identifier.class)
public final class IdentifierMixin
{
    @Mutable @Shadow @Final protected String namespace;

    @Inject(method = "<init>([Ljava/lang/String;)V", at = @At("RETURN"))
    private void init(String[] strings_1, CallbackInfo ci) { if (namespace.equals("cursedchests")) namespace = ExpandedStorage.MOD_ID; }
}
