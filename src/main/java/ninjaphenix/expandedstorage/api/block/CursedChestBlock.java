package ninjaphenix.expandedstorage.api.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EntityContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import ninjaphenix.expandedstorage.api.Registries;
import ninjaphenix.expandedstorage.api.Registries.ChestTierData;
import ninjaphenix.expandedstorage.api.block.base.FluidLoggableChestBlock;
import ninjaphenix.expandedstorage.api.block.entity.CursedChestBlockEntity;
import ninjaphenix.expandedstorage.api.block.enums.CursedChestType;

import static ninjaphenix.expandedstorage.api.block.enums.CursedChestType.*;

@SuppressWarnings("deprecation")
public class CursedChestBlock extends FluidLoggableChestBlock
{
    private static final VoxelShape SINGLE_SHAPE = Block.createCuboidShape(1, 0, 1, 15, 14, 15);
    private static final VoxelShape TOP_SHAPE = Block.createCuboidShape(1, 0, 1, 15, 14, 15);
    private static final VoxelShape BOTTOM_SHAPE = Block.createCuboidShape(1, 0, 1, 15, 16, 15);
    private static final VoxelShape A = Block.createCuboidShape(1, 0, 1, 16, 14, 15);
    private static final VoxelShape B = Block.createCuboidShape(0, 0, 1, 15, 14, 15);
    private static final VoxelShape C = Block.createCuboidShape(1, 0, 0, 15, 14, 15);
    private static final VoxelShape D = Block.createCuboidShape(1, 0, 1, 15, 14, 16);

    public CursedChestBlock(Settings settings) { super(settings); }

    @Override
    public BlockEntity createBlockEntity(BlockView view) { return new CursedChestBlockEntity(Registry.BLOCK.getId(this)); }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, EntityContext context)
    {
        CursedChestType type = state.get(TYPE);
        if (type == SINGLE) return SINGLE_SHAPE;
        if (type == TOP) return TOP_SHAPE;
        if (type == BOTTOM) return BOTTOM_SHAPE;
        switch (type)
        {
            case BACK:
                switch (state.get(FACING))
                {
                    case NORTH: return C;
                    case SOUTH: return D;
                    case WEST: return B;
                    case EAST: return A;
                }
                break;
            case RIGHT:
                switch (state.get(FACING))
                {
                    case NORTH: return A;
                    case SOUTH: return B;
                    case WEST: return C;
                    case EAST: return D;
                }
                break;
            case FRONT:
                switch (state.get(FACING))
                {
                    case NORTH: return D;
                    case SOUTH: return C;
                    case WEST: return A;
                    case EAST: return B;
                }
                break;
            case LEFT:
                switch (state.get(FACING))
                {
                    case NORTH: return B;
                    case SOUTH: return A;
                    case WEST: return D;
                    case EAST: return C;
                }
                break;
        }
        return SINGLE_SHAPE;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) { return BlockRenderType.ENTITYBLOCK_ANIMATED; }

    @Override
    @SuppressWarnings("unchecked")
    public SimpleRegistry<ChestTierData> getDataRegistry() { return Registries.MODELED; }
}
