package nl.melonstudios.bmnw.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import nl.melonstudios.bmnw.hazard.radiation.RadiationTools;
import nl.melonstudios.bmnw.init.BMNWEffects;

public class DecontaminatorBlock extends Block {
    public DecontaminatorBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (entity instanceof LivingEntity living) {
            RadiationTools.addEntityRadiation(living, -1);

            if (living.hasEffect(BMNWEffects.CONTAMINATION)) {
                living.removeEffect(BMNWEffects.CONTAMINATION);
            }
        }
    }
}
