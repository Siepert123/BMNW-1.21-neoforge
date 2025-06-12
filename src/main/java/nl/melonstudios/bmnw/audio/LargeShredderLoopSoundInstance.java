package nl.melonstudios.bmnw.audio;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import nl.melonstudios.bmnw.block.entity.LargeShredderBlockEntity;
import nl.melonstudios.bmnw.init.BMNWSounds;

public class LargeShredderLoopSoundInstance extends AbstractTickableSoundInstance {
    public final LargeShredderBlockEntity be;
    public LargeShredderLoopSoundInstance(LargeShredderBlockEntity be) {
        super(BMNWSounds.LARGE_SHREDDER_LOOP.get(), SoundSource.BLOCKS, RandomSource.create());
        this.be = be;

        BlockPos pos = be.getBlockPos();
        this.x = pos.getX() + 0.5;
        this.y = pos.getY() + 0.5;
        this.z = pos.getZ() + 0.5;
    }

    @Override
    public boolean isLooping() {
        return true;
    }

    @Override
    public void tick() {
        if (this.be.isRemoved() || !this.be.running) this.stop();
    }
}
