package nl.melonstudios.bmnw.block.decoration;

import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import nl.melonstudios.bmnw.init.BMNWPartialModels;
import nl.melonstudios.bmnw.misc.PartialModel;

import javax.annotation.Nonnull;

public class FixtureBlock extends BaseSmallLampBlock {
    public FixtureBlock(Properties properties, boolean inverted, DyeColor color) {
        super(properties, inverted, color);

        if (inverted) FIXTURES_INVERTED[color.getId()] = this;
        else FIXTURES[color.getId()] = this;
    }

    public static final FixtureBlock[] FIXTURES = new FixtureBlock[16];
    public static final FixtureBlock[] FIXTURES_INVERTED = new FixtureBlock[16];

    private static final VoxelShape[] SHAPES = new VoxelShape[6];

    static {
        SHAPES[0] = box(2, 8, 2, 14, 16, 14);
        SHAPES[1] = box(2, 0, 2, 14, 8, 14);
        SHAPES[2] = box(2, 2, 8, 14, 14, 16);
        SHAPES[3] = box(2, 2, 0, 14, 14, 8);
        SHAPES[4] = box(8, 2, 2, 16, 14, 14);
        SHAPES[5] = box(0, 2, 2, 8, 14, 14);
    }

    @Nonnull
    @Override
    protected VoxelShape[] getShapeArray() {
        return SHAPES;
    }

    @Override
    public BlockState applyColor(BlockState old, DyeColor color) {
        BlockState state = this.inverted ? FIXTURES_INVERTED[color.getId()].defaultBlockState() : FIXTURES[color.getId()].defaultBlockState();
        return state
                .setValue(FACING, old.getValue(FACING))
                .setValue(POWERED, old.getValue(POWERED))
                .setValue(DYEABLE, true);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public PartialModel getLampPart() {
        return BMNWPartialModels.FIXTURE_LAMP;
    }
}
