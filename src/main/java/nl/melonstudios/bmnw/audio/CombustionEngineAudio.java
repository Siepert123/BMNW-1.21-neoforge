package nl.melonstudios.bmnw.audio;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import nl.melonstudios.bmnw.block.entity.CombustionEngineBlockEntity;
import nl.melonstudios.bmnw.init.BMNWSounds;

public class CombustionEngineAudio extends AbstractTickableSoundInstance {
    private final CombustionEngineBlockEntity be;
    public CombustionEngineAudio(CombustionEngineBlockEntity be) {
        super(BMNWSounds.BOILER.get(), SoundSource.BLOCKS, RandomSource.create(be.getBlockPos().asLong()));
        this.be = be;

        this.volume = 0.25F;
        this.looping = true;

        Vec3 vec = be.getBlockPos().getCenter();
        this.x = vec.x;
        this.y = vec.y;
        this.z = vec.z;
    }

    @Override
    public void tick() {
        if (this.be.isRemoved() || this.be.fluid.isEmpty() || this.be.maxBurnTime == 0) this.stop();
    }
}
