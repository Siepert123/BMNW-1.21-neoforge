package nl.melonstudios.bmnw.item.custom;

import nl.melonstudios.bmnw.category.BombCategory;
import nl.melonstudios.bmnw.interfaces.IBombBlock;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;

import java.util.List;

public class BombBlockItem extends BlockItem {
    public <T extends Block & IBombBlock> BombBlockItem(T block, Properties properties, BombCategory category) {
        super(block, properties);
        radius = block.radius();
        this.category = category;
    }
    public <T extends Block & IBombBlock> BombBlockItem(T block, Properties properties) {
        this(block, properties, BombCategory.of("uncategorized"));
    }
    private final BombCategory category;
    private final int radius;

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        tooltipComponents.add(category.asTooltip());
        tooltipComponents.add(Component.translatable("tooltip.bmnw.radius", radius).withColor(0x888888));
    }
}
