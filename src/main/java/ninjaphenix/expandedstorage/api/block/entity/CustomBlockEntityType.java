package ninjaphenix.expandedstorage.api.block.entity;

import com.mojang.datafixers.types.Type;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;

import java.util.function.Predicate;
import java.util.function.Supplier;

public class CustomBlockEntityType<T extends BlockEntity> extends BlockEntityType<T>
{
    Predicate<Block> predicate;

    public CustomBlockEntityType(Supplier<? extends T> supplier, Type<?> type, Predicate<Block> supportPredicate)
    {
        super(supplier, null, type);
        predicate = supportPredicate;
    }

    @Override
    public boolean supports(Block block)
    {
        return predicate.test(block);
    }
}