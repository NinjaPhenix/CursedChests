package gd.rf.ninjaphenix.cursedchests.block;

import gd.rf.ninjaphenix.cursedchests.api.block.VerticalChestBlock;
import gd.rf.ninjaphenix.cursedchests.block.entity.GoldVerticalChestBlockEntity;
import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.BlockView;

public class GoldVerticalChestBlock extends VerticalChestBlock
{
    GoldVerticalChestBlock() { super(FabricBlockSettings.copy(Blocks.GOLD_BLOCK).build()); }

    @Override
    public BlockEntity createBlockEntity(BlockView view) { return new GoldVerticalChestBlockEntity(); }

    @Override
    public String getName() { return "gold_chest"; }
}
