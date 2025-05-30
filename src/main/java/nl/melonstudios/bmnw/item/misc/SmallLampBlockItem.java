package nl.melonstudios.bmnw.item.misc;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.ItemLike;
import nl.melonstudios.bmnw.block.decoration.BaseSmallLampBlock;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SmallLampBlockItem extends BlockItem {
    public SmallLampBlockItem(BaseSmallLampBlock block, Properties properties) {
        super(block, properties);

        ALL.add(this);
    }

    @Override
    public BaseSmallLampBlock getBlock() {
        return (BaseSmallLampBlock) super.getBlock();
    }

    public static final ArrayList<ItemLike> ALL = new ArrayList<>();
}
