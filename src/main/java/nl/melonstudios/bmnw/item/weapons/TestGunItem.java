package nl.melonstudios.bmnw.item.weapons;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import nl.melonstudios.bmnw.entity.BulletEntity;
import nl.melonstudios.bmnw.entity.SimpleBulletEntity;

public class TestGunItem extends Item {
    public TestGunItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        Vec3 look = player.getLookAngle();
        BulletEntity bullet = new SimpleBulletEntity(level, player.getX(), player.getEyeY(), player.getZ(),
                look.multiply(3, 3, 3), 5.0F);
        level.addFreshEntity(bullet);
        return InteractionResultHolder.success(player.getItemInHand(usedHand));
    }
}
