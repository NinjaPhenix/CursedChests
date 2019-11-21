package ninjaphenix.expandedstorage.mixins;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4722;
import net.minecraft.class_4730;
import ninjaphenix.expandedstorage.client.ExpandedStorageClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(class_4722.class)
@Environment(EnvType.CLIENT)
public class Class_4722Mixin
{
    @Inject(at = @At("HEAD"), method = "method_24066(Ljava/util/function/Consumer;)V")
    private static void method(Consumer<class_4730> consumer, CallbackInfo ci)
    {
        ExpandedStorageClient.makeAtlases(consumer);
    }
}
