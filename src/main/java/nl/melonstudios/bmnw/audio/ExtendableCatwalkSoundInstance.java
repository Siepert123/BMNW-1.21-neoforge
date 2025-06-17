package nl.melonstudios.bmnw.audio;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import nl.melonstudios.bmnw.block.entity.ExtendableCatwalkBlockEntity;
import nl.melonstudios.bmnw.init.BMNWSounds;

@OnlyIn(Dist.CLIENT)
public class ExtendableCatwalkSoundInstance extends AbstractTickableSoundInstance {
    private final ExtendableCatwalkBlockEntity be;
    private final boolean animation;
    public ExtendableCatwalkSoundInstance(ExtendableCatwalkBlockEntity be, boolean animation) {
        super(/*animation ? BMNWSounds.EXTENDABLE_CATWALK_EXTEND.get() : */BMNWSounds.EXTENDABLE_CATWALK_RETRACT.get(),
                SoundSource.BLOCKS, RandomSource.create());
        this.be = be;
        this.animation = animation;
        this.looping = true;

        Vec3 vec = this.be.getBlockPos().getBottomCenter().add(0, 1, 0);
        this.x = vec.x;
        this.y = vec.y;
        this.z = vec.z;
    }

    @Override
    public boolean canStartSilent() {
        return true;
    }

    @Override
    public void tick() {
        if (!this.be.compareAnimation(this.animation) || this.be.isRemoved()) {
            this.stop();
            Level level = this.be.getLevel();
            if (level != null) {
            }
        }

    }
}
