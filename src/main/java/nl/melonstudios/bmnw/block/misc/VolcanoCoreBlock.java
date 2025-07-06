package nl.melonstudios.bmnw.block.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import nl.melonstudios.bmnw.blockentity.VolcanoCoreBlockEntity;
import nl.melonstudios.bmnw.entity.LavaEjectionEntity;
import nl.melonstudios.bmnw.init.BMNWParticleTypes;
import nl.melonstudios.bmnw.misc.Library;
import org.jetbrains.annotations.Nullable;

public class VolcanoCoreBlock extends TickingEntityBlock {
    private static final BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
    public final LavaEjectionEntity.Type type;
    public final boolean extinguishes, grows;

    public VolcanoCoreBlock(Properties properties, LavaEjectionEntity.Type type, boolean extinguishes, boolean grows) {
        super(properties);
        this.type = type;
        this.extinguishes = extinguishes;
        this.grows = grows;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new VolcanoCoreBlockEntity(pos, state);
    }
}
