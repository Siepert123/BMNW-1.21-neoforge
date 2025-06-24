package nl.melonstudios.bmnw.audio;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.function.Predicate;

public class BEBoundPredicateSoundInstance<T extends BlockEntity> extends AbstractTickableSoundInstance {
    private final T be;
    private final Predicate<T> mustStop;

    public BEBoundPredicateSoundInstance(T be, SoundEvent sound, SoundSource source, float v, float p,
                                            Predicate<T> mustStop, long seed) {
        super(sound, source, RandomSource.create(seed));
        this.volume = v;
        this.pitch = p;
        this.x = be.getBlockPos().getX() + 0.5;
        this.y = be.getBlockPos().getY() + 0.5;
        this.z = be.getBlockPos().getZ() + 0.5;

        this.be = be;
        this.mustStop = mustStop;

        this.looping = true;
    }

    @Override
    public void tick() {
        if (this.be.isRemoved() || this.mustStop.test(this.be)) this.stop();
    }
}
