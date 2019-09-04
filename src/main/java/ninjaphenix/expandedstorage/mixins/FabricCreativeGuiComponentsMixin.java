package ninjaphenix.expandedstorage.mixins;

import net.fabricmc.fabric.impl.itemgroup.CreativeGuiExtensions;
import net.fabricmc.fabric.impl.itemgroup.FabricCreativeGuiComponents;
import ninjaphenix.expandedstorage.client.CreativeButtonMover;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FabricCreativeGuiComponents.ItemGroupButtonWidget.class)
public class FabricCreativeGuiComponentsMixin
{
    @Inject(method = "<init>", at = @At("RETURN"))
    public void init(int x, int y, FabricCreativeGuiComponents.Type type, CreativeGuiExtensions extensions, CallbackInfo ci)
    {
        FabricCreativeGuiComponents.ItemGroupButtonWidget widget = (FabricCreativeGuiComponents.ItemGroupButtonWidget) (Object) this;
        widget.x = x - 170 + CreativeButtonMover.x;
        widget.y = y - 4 + CreativeButtonMover.y;
    }
}