package nl.melonstudios.bmnw.hardcoded.recipe;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import nl.melonstudios.bmnw.init.BMNWItems;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public enum StampType implements StringRepresentable {
    BLANK(tag("bmnw:molds/blank"), "empty"),
    PLATE(tag("bmnw:molds/plate"), "plate"),
    WIRE(tag("bmnw:molds/wire"), "wire");

    private final String name;
    private final TagKey<Item> itemTag;
    private final Ingredient asIngredient;
    StampType(TagKey<Item> itemTag, String name) {
        this.itemTag = itemTag;
        this.asIngredient = Ingredient.of(this.itemTag);
        this.id = this.ordinal();
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    private final int id;

    public static final StampType[] VALUES = {
            BLANK, PLATE, WIRE
    };
    public static StampType byName(String name) {
        for (StampType type : VALUES) if (type.name == name) return type;
        return StampType.BLANK;
    }

    public boolean isItemThis(ItemStack stack) {
        return stack.is(itemTag);
    }

    @Nullable
    public static StampType getMoldType(ItemStack item) {
        if (item.isEmpty()) return null;
        for (StampType type : values()) {
            if (type.isItemThis(item)) return type;
        }
        return null;
    }

    public ItemStack getDefaultItem() {
        return switch (this) {
            case BLANK -> BMNWItems.BLANK_IRON_STAMP.toStack();
            case PLATE -> BMNWItems.IRON_PLATE_STAMP.toStack();
            case WIRE -> BMNWItems.IRON_WIRE_STAMP.toStack();
        };
    }
    public List<ItemStack> asStackList() {
        return Arrays.asList(this.asIngredient.getItems());
    }
    public Ingredient asIngredient() {
        return this.asIngredient;
    }
    private static TagKey<Item> tag(String id) {
        return TagKey.create(Registries.ITEM, ResourceLocation.parse(id));
    }

    public static final Codec<StampType> CODEC = StringRepresentable.fromEnum(() -> StampType.VALUES);
    public static final StreamCodec<ByteBuf, StampType> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public StampType decode(ByteBuf buffer) {
            return VALUES[buffer.readByte()];
        }

        @Override
        public void encode(ByteBuf buffer, StampType value) {
            buffer.writeByte(value.id);
        }
    };
}
