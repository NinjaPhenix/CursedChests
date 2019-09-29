package ninjaphenix.expandedstorage.api.block;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.BlockView;
import ninjaphenix.expandedstorage.api.Registries;
import ninjaphenix.expandedstorage.api.block.entity.OldChestBlockEntity;

public class OldChestBlock extends AbstractChestBlock
{
    public OldChestBlock(Settings settings) { super(settings); }

    @Override
    public BlockEntity createBlockEntity(BlockView var1)
    {
        Identifier blockId = Registry.BLOCK.getId(this);
        return new OldChestBlockEntity(new Identifier(blockId.getNamespace(), blockId.getPath().substring(4)));
    }

    @Override
    public SimpleRegistry<Registries.TierData> getDataRegistry() { return Registries.OLD; }
}