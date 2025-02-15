package nl.melonstudios.bmnw.hardcoded.structure.coded;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import nl.melonstudios.bmnw.block.BMNWBlocks;
import nl.melonstudios.bmnw.hardcoded.lootpool.LootPools;
import nl.melonstudios.bmnw.hardcoded.structure.Structure;
import nl.melonstudios.bmnw.hardcoded.structure.StructureBlockModifier;

import java.util.List;
import java.util.Random;

public class StructureAncientMuseum extends Structure {
    private final Vec3i[] offsetValues = {
            new Vec3i(2, 1, 2),
            new Vec3i(5, 1, 2),
            new Vec3i(8, 1, 2),
            new Vec3i(2, 1, 8),
            new Vec3i(5, 1, 8),
            new Vec3i(8, 1, 8),
    };
    @Override
    public boolean place(LevelAccessor level, ChunkPos pos, Random random, List<StructureBlockModifier> modifiers) {
        BlockPos origin = pos.getBlockAt(0, -60, 0);

        fillBlocks(level, origin, origin.offset(10, 4, 10), Blocks.TUFF_BRICKS.defaultBlockState());
        fillBlocks(level, origin.above(2), origin.offset(10, 2, 10), Blocks.CHISELED_TUFF_BRICKS.defaultBlockState());
        fillBlocks(level, origin.offset(1, 1, 1), origin.offset(9, 4, 9), Blocks.AIR.defaultBlockState());
        fillBlocks(level, origin.offset(0, 5, 0), origin.offset(10, 5, 10), BMNWBlocks.FOUNDATION_CONCRETE.get().defaultBlockState());
        fillBlocks(level, origin.offset(1, 0, 4), origin.offset(9, 0, 4), Blocks.CHISELED_TUFF_BRICKS.defaultBlockState());
        fillBlocks(level, origin.offset(1, 0, 6), origin.offset(9, 0, 6), Blocks.CHISELED_TUFF_BRICKS.defaultBlockState());

        for (Vec3i offset : offsetValues) {
            BlockPos bp = origin.offset(offset);

            placeBlock(level, bp, Blocks.QUARTZ_PILLAR.defaultBlockState());
            placeBlock(level, bp.above(), LootPools.STATE_ANCIENT_MUSEUM.get(random));
            placeBlock(level, bp.above(3), Blocks.GLOWSTONE.defaultBlockState());
        }
        return true;
    }
}
