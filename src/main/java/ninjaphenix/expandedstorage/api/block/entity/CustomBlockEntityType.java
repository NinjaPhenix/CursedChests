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

    public CustomBlockEntityType(Supplier<? extends T> supplier_1, Type<?> type_1, Predicate<Block> supports)
    {
        super(supplier_1, null, type_1);
        predicate = supports;
    }

    @Override
    public boolean supports(Block block)
    {
        return predicate.test(block);
    }
}