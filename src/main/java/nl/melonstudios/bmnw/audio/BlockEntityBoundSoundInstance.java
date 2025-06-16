package nl.melonstudios.bmnw.audio;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BlockEntityBoundSoundInstance extends AbstractTickableSoundInstance {
    protected final BlockEntity be;
    public BlockEntityBoundSoundInstance(SoundEvent soundEvent, SoundSource source, float volume, float pitch, BlockEntity be, long seed) {
        super(soundEvent, source, RandomSource.create(seed));
        this.volume = volume;
        this.pitch = pitch;
        this.be = be;
        this.x = be.getBlockPos().getX();
        this.y = be.getBlockPos().getY();
        this.z = be.getBlockPos().getZ();
    }

    public BlockEntityBoundSoundInstance(SoundEvent soundEvent, SoundSource source, float volume, float pitch, BlockEntity be) {
        this(soundEvent, source, volume, pitch, be, be.getBlockPos().asLong()); 
    }

    @Override
    public void tick() {
        if (this.be.isRemoved()) {
            this.stop();
        }
    }
}
