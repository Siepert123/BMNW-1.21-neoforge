package nl.melonstudios.bmnw.hardcoded.structure;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.neoforged.neoforge.registries.DeferredBlock;
import nl.melonstudios.bmnw.hazard.radiation.ChunkRadiationManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

public abstract class Structure {
    protected static final Logger LOGGER = LogManager.getLogger();
    /**
     * @param level
     * @param pos
     * @param random
     * @return True if the structure could be placed.
     */
    public abstract boolean place(LevelAccessor level, ChunkPos pos, Random random, List<StructureBlockModifier> modifiers);

    protected int getHeightInefficient(LevelAccessor level, int x, int z) {
        for (int y = level.getMaxBuildHeight(); y > level.getMinBuildHeight(); y--) {
            if (!level.getBlockState(new BlockPos(x, y, z)).canBeReplaced()) return y+1;
        }
        return level.getMinBuildHeight();
    }
    protected int getHeightBlacklisted(LevelAccessor level, int x, int z, Predicate<BlockState> blacklist) {
        for (int y = level.getMaxBuildHeight(); y > level.getMinBuildHeight(); y--) {
            if (!blacklist.test(level.getBlockState(new BlockPos(x, y, z)))) {
                return y+1;
            }
        }
        return level.getMinBuildHeight();
    }
    protected int getHeightNoPlants(LevelAccessor level, int x, int z) {
        return getHeightBlacklisted(level, x, z, (state -> {
            if (!state.getFluidState().isEmpty()) return false;
            if (state.canBeReplaced()) return true;
            if (state.is(BlockTags.FLOWERS)) return true;
            if (state.is(BlockTags.LEAVES)) return true;
            return state.is(BlockTags.LOGS);
        }));
    }
    protected void placeBlock(LevelAccessor level, BlockPos pos, BlockState state, Random random, List<StructureBlockModifier> modifiers) {
        for (StructureBlockModifier modifier : modifiers) state = modifier.modifyBlock(state, random);
        placeBlock(level, pos, state);
    }
    protected void placeBlock(LevelAccessor level, BlockPos pos, BlockState state) {
        if (!level.isOutsideBuildHeight(pos)) {
            level.getChunk(new ChunkPos(pos).x, new ChunkPos(pos).z, ChunkStatus.EMPTY, true);
            level.setBlock(pos, state, 2);
            ChunkRadiationManager.handler.notifyBlockChange((Level)level, pos);
        }
    }
    protected void placeBlock(LevelAccessor level, BlockPos pos, DeferredBlock<?> block) {
        placeBlock(level, pos, block.get().defaultBlockState());
    }

    protected void fillBlocks(LevelAccessor level, BlockPos start, BlockPos end, BlockState state, Random random, List<StructureBlockModifier> modifiers) {
        Iterable<BlockPos> positions = BlockPos.betweenClosed(start, end);
        for (BlockPos pos : positions) {
            placeBlock(level, pos, state, random, modifiers);
        }
    }
    protected void fillBlocks(LevelAccessor level, BlockPos start, BlockPos end, BlockState state) {
        Iterable<BlockPos> positions = BlockPos.betweenClosed(start, end);
        for (BlockPos pos : positions) {
            placeBlock(level, pos, state);
        }
    }

    protected enum FoundationReplaceType {
        AIR_ONLY {
            @Override
            public boolean canReplace(BlockState state) {
                return state.canBeReplaced();
            }
        },
        PLANTS {
            @Override
            public boolean canReplace(BlockState state) {
                if (state.canBeReplaced()) return true;
                if (state.is(BlockTags.LEAVES)) return true;
                if (state.is(BlockTags.FLOWER_POTS)) return true;
                return state.is(BlockTags.LOGS);
            }
        },
        PLANTS_AND_WATER {
            @Override
            public boolean canReplace(BlockState state) {
                if (state.canBeReplaced()) return true;
                if (state.is(BlockTags.LEAVES)) return true;
                if (state.is(BlockTags.FLOWER_POTS)) return true;
                if (!state.getFluidState().isEmpty()) return true;
                return state.is(BlockTags.LOGS);
            }
        };

        public boolean canReplace(BlockState state) {
            return false;
        }
    }

    protected void createFoundation(LevelAccessor level, int startX, int startY, int startZ, int sizeX, int sizeZ, FoundationReplaceType type, BlockState block) {
        for (int x = startX; x < startX + sizeX; x++) {
            for (int z = startZ; z < startZ + sizeZ; z++) {
                BlockPos pos = new BlockPos(x, startY, z);
                while (type.canReplace(level.getBlockState(pos))) {
                    level.setBlock(pos, block, 2);
                    pos = pos.below();
                    ChunkRadiationManager.handler.notifyBlockChange((Level)level, pos);
                }
            }
        }
    }
    protected void createFoundation(LevelAccessor level, int startX, int startY, int startZ, int sizeX, int sizeZ, BlockState block) {
        createFoundation(level, startX, startY, startZ, sizeX, sizeZ, FoundationReplaceType.PLANTS_AND_WATER, block);
    }

    protected void placeDoor(LevelAccessor level, BlockPos pos, DoorBlock block, Direction facing, DoorHingeSide hingeSide, boolean open) {
        BlockState lower = block.defaultBlockState()
                .setValue(BlockStateProperties.HORIZONTAL_FACING, facing)
                .setValue(BlockStateProperties.OPEN, open)
                .setValue(BlockStateProperties.DOOR_HINGE, hingeSide)
                .setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.LOWER);
        BlockState upper = lower
                .setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.UPPER);

        placeBlock(level, pos, lower);
        placeBlock(level, pos.above(), upper);
    }
}
