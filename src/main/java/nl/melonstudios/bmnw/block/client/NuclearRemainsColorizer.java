package nl.melonstudios.bmnw.block.client;

import net.minecraft.client.color.block.BlockColor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import nl.melonstudios.bmnw.block.state.BMNWStateProperties;
import org.jetbrains.annotations.Nullable;

@OnlyIn(Dist.CLIENT)
public class NuclearRemainsColorizer implements BlockColor {
    @Override
    public int getColor(BlockState state, @Nullable BlockAndTintGetter level, @Nullable BlockPos pos, int tintIndex) {
        if (tintIndex != 0) return -1;
        int darkness = state.getValue(BMNWStateProperties.DARKNESS);
        if (darkness == 0) return 0xFFFFFFFF;
        if (darkness == 1) return 0xFFDDDDDD;
        if (darkness == 2) return 0xFFBBBBBB;
        if (darkness == 3) return 0xFF999999;
        return -1;
    }
}
