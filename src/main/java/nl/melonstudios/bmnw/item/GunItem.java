package nl.melonstudios.bmnw.item;

import nl.melonstudios.bmnw.entity.AbstractBulletEntity;
import nl.melonstudios.bmnw.init.BMNWItems;
import nl.melonstudios.bmnw.init.BMNWDataComponents;
import nl.melonstudios.bmnw.misc.GunOptions;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

@SuppressWarnings("all")
public class GunItem extends ProjectileWeaponItem {
    private final GunOptions gunOptions;
    public GunOptions getGunOptions() {
        return gunOptions;
    }

    private final int defaultRange;
    private final int maxAmmo;
    public GunItem(Properties properties, GunOptions gunOptions) {
        super(properties.stacksTo(1));
        this.defaultRange = gunOptions.getRange();
        this.maxAmmo = gunOptions.getMaxAmmo();
        this.allowedAmmo = gunOptions.getAllowedAmmo();
        this.gunOptions = gunOptions;
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return 0x00ff00;
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return getAmmunition(stack) > 0;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return Math.round((float)getAmmunition(stack) * 13.0F / (float)getMaxAmmunition(stack));
    }

    public int getAmmunition(ItemStack stack) {
        if (!stack.has(BMNWDataComponents.AMMUNITION)) return 0;
        return stack.get(BMNWDataComponents.AMMUNITION);
    }
    public int getMaxAmmunition(ItemStack stack) {
        return maxAmmo;
    }

    private final Predicate<ItemStack> allowedAmmo;
    @Override
    public Predicate<ItemStack> getAllSupportedProjectiles() {
        return allowedAmmo;
    }

    @Override
    public int getDefaultProjectileRange() {
        return defaultRange;
    }

    @Override
    protected void shootProjectile(LivingEntity shooter, Projectile projectile, int index, float velocity, float inaccuracy, float angle, @Nullable LivingEntity target) {
        projectile.shootFromRotation(shooter, shooter.getXRot(), shooter.getYRot() + angle, 0, velocity, inaccuracy);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.NONE;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        tooltipComponents.add(Component.translatable("tooltip.bmnw.ammo").append(": ").append(String.valueOf(getAmmunition(stack)))
                .append("/").append(String.valueOf(getMaxAmmunition(stack))).withStyle(ChatFormatting.GRAY));
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 20;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        if (player.isUsingItem()) return InteractionResultHolder.pass(stack);
        if (!level.isClientSide()) {
            if (getAmmunition(stack) == 0) {
                stack.set(BMNWDataComponents.AMMUNITION, getMaxAmmunition(stack));
            } else {
                stack.set(BMNWDataComponents.AMMUNITION, getAmmunition(stack) - 1);
                shoot((ServerLevel) level, player, usedHand, stack, List.of(new ItemStack(Items.ARROW, 8)),
                        5, 0.05f, true, null);
                player.startUsingItem(usedHand);
            }
        }
        player.awardStat(Stats.ITEM_USED.get(this));
        return InteractionResultHolder.fail(stack);
    }

    @Override
    protected Projectile createProjectile(Level level, LivingEntity shooter, ItemStack weapon, ItemStack ammo, boolean isCrit) {
        BulletItem bulletItem = ammo.getItem() instanceof BulletItem bullet ? bullet : BMNWItems.LEAD_BULLET.get();
        AbstractBulletEntity bullet = bulletItem.asProjectile(level, shooter.getEyePosition(), weapon, shooter.getDirection());

        return bullet;
    }
}
