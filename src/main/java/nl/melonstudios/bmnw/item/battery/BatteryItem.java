package nl.melonstudios.bmnw.item.battery;

import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import nl.melonstudios.bmnw.init.BMNWDataComponents;
import nl.melonstudios.bmnw.interfaces.IBatteryItem;

import java.util.List;

public class BatteryItem extends Item implements IBatteryItem {
    public BatteryItem(Properties properties, int capacity, int maxTransfer) {
        super(properties.stacksTo(1));
        this.capacity = capacity;
        this.maxTransfer = maxTransfer;
    }

    public static String formatNicely(int rf) {
        if (rf > 1000000000) {
            return Mth.quantize((rf / 1000000000.0F) * 100.0F, 1) / 100.0F + "GRF";
        }
        if (rf > 1000000) {
            return Mth.quantize((rf / 1000000.0F) * 100.0F, 1) / 100.0F + "MRF";
        }
        if (rf > 1000) {
            return Mth.quantize((rf / 1000.0F) * 100.0F, 1) / 100.0F + "kRF";
        }
        return rf + "RF";
    }

    private static final boolean b = true;
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        final int giga = 1000000000;
        final int mega = 1000000;
        final int kilo = 1000;
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        final int nrg = getStoredEnergy(stack);
        final int maxNRG = getMaxStoredEnergy();
        if (b) {
            tooltipComponents.add(Component.literal(String.format("%s/%s", formatNicely(nrg), formatNicely(maxNRG))).withColor(0xaaaaaa));
        } else {
            tooltipComponents.add(Component.literal(String.format("%s/%s", nrg, maxNRG)).withColor(0xaaaaaa));
        }
        if (b) {
            if (getMaxEnergyTransfer() >= giga) {
                tooltipComponents.add(Component.translatable("tooltip.bmnw.discharge_rate_giga", formatNicely(getMaxEnergyTransfer(), getMaxEnergyTransfer())).withColor(0xaaaaaa));
            } else if (getMaxEnergyTransfer() >= mega) {
                tooltipComponents.add(Component.translatable("tooltip.bmnw.discharge_rate_mega", formatNicely(getMaxEnergyTransfer(), getMaxEnergyTransfer())).withColor(0xaaaaaa));
            } else if (getMaxEnergyTransfer() >= kilo * 5) {
                tooltipComponents.add(Component.translatable("tooltip.bmnw.discharge_rate_kilo", formatNicely(getMaxEnergyTransfer(), getMaxEnergyTransfer())).withColor(0xaaaaaa));
            } else {
                tooltipComponents.add(Component.translatable("tooltip.bmnw.discharge_rate", getMaxEnergyTransfer()).withColor(0xaaaaaa));
            }
        } else {
            tooltipComponents.add(Component.translatable("tooltip.bmnw.discharge_rate", getMaxEnergyTransfer()).withColor(0xaaaaaa));
        }

        if (getStoredEnergy(stack) > getMaxStoredEnergy()) {
            tooltipComponents.add(Component.translatable("tooltip.bmnw.battery_overcharge").withColor(0xff0000));
        } else if (!isBarVisible(stack)) {
            tooltipComponents.add(Component.translatable("tooltip.bmnw.fully_charged").withColor(0x00ff00));
        }
    }
    private float formatNicely(int nrg, int maxNRG) {
        final float giga = 1000000000;
        final float mega = 1000000;
        final float kilo = 1000;
        if (maxNRG >= giga) {
            return Mth.quantize(nrg / (giga) * 100f, 1) / 100f;
        }
        if (maxNRG >= mega) {
            return Mth.quantize(nrg / (mega) * 100f, 1) / 100f;
        }
        if (maxNRG >= kilo) {
            return Mth.quantize(nrg / (kilo) * 100f, 1) / 100f;
        }
        return nrg;
    }

    @Override
    public void verifyComponentsAfterLoad(ItemStack stack) {
        requireNonNullComponent(stack);
    }

    public boolean isBarVisible(ItemStack stack) {
        return getStoredEnergy(stack) != getMaxStoredEnergy();
    }

    public int getBarWidth(ItemStack stack) {
        return Math.round(13.0F - (float)(getMaxStoredEnergy() - getStoredEnergy(stack)) * 13.0F / (float)this.getMaxStoredEnergy());
    }

    @Override
    public int getBarColor(ItemStack stack) {
        int i = getMaxStoredEnergy() - getStoredEnergy(stack);
        float maxNRG = getMaxStoredEnergy();
        float f = Math.max(0.0F, (maxNRG - (float)i) / maxNRG);
        return Mth.hsvToRgb(f / 3.0F, 1.0F, 1.0F);
    }

    private final int capacity, maxTransfer;

    @Override
    public int getMaxStoredEnergy() {
        return capacity;
    }

    @Override
    public int getStoredEnergy(ItemStack stack) {
        requireNonNullComponent(stack);
        // noinspection all
        return stack.get(BMNWDataComponents.STORED_BATTERY_RF.get());
    }

    @Override
    public int getMaxEnergyTransfer() {
        return maxTransfer;
    }

    @Override
    public void setStoredEnergy(ItemStack stack, int amount) {
        stack.set(BMNWDataComponents.STORED_BATTERY_RF.get(), amount);
    }

    @Override
    public void addStoredEnergy(ItemStack stack, int amount) {
        requireNonNullComponent(stack);
        int nrg = getStoredEnergy(stack);
        stack.set(BMNWDataComponents.STORED_BATTERY_RF.get(), nrg + amount);
    }

    @Override
    public void removeStoredEnergy(ItemStack stack, int amount) {
        requireNonNullComponent(stack);
        int nrg = getStoredEnergy(stack);
        stack.set(BMNWDataComponents.STORED_BATTERY_RF.get(), Math.max(0, nrg - amount));
    }

    @Override
    public int drainStoredEnergy(ItemStack stack) {
        requireNonNullComponent(stack);
        int nrg = getStoredEnergy(stack);
        setStoredEnergy(stack, 0);
        return nrg;
    }

    @Override
    public void requireNonNullComponent(ItemStack stack) {
        if (!stack.has(BMNWDataComponents.STORED_BATTERY_RF.get())) {
            stack.set(BMNWDataComponents.STORED_BATTERY_RF.get(), 0);
        }
    }

    private boolean large = false;
    public BatteryItem setLarge() {
        this.large = true;
        return this;
    }

    @Override
    public boolean isLarge() {
        return this.large;
    }
}
