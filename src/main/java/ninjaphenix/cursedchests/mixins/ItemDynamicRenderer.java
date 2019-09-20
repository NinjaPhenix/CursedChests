package ninjaphenix.cursedchests.mixins;

import ninjaphenix.cursedchests.api.CursedChestRegistry;
import ninjaphenix.cursedchests.api.block.CursedChestBlock;
import ninjaphenix.cursedchests.api.block.entity.CursedChestBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(net.minecraft.client.render.item.ItemDynamicRenderer.class)
@Environment(EnvType.CLIENT)
public class ItemDynamicRenderer
{
    @Inject(at = @At("HEAD"), method = "render", cancellable = true)
    private void render(ItemStack itemStack, CallbackInfo info)
    {
        Item item = itemStack.getItem();
        if (item instanceof BlockItem)
        {
            Block block = ((BlockItem) item).getBlock();
            if (block instanceof CursedChestBlock)
            {
                CursedChestBlockEntity blockEntity = CursedChestRegistry.getChestBlockEntity(Registry.BLOCK.getId(block));
                if (blockEntity != null)
                {
                    BlockEntityRenderDispatcher.INSTANCE.renderEntity(blockEntity);
                    info.cancel();
                }
            }
        }
    }
}
