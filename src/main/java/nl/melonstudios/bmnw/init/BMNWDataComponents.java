package nl.melonstudios.bmnw.init;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@SuppressWarnings("removal")
public class BMNWDataComponents {
    private static final DeferredRegister.DataComponents COMPONENTS = DeferredRegister.createDataComponents("bmnw");

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<BlockPos>> TARGET = COMPONENTS.registerComponentType(
            "target",
            builder -> builder
                    .persistent(BlockPos.CODEC)
                    .networkSynchronized(BlockPos.STREAM_CODEC)
    );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> STORED_BATTERY_RF = COMPONENTS.registerComponentType(
            "stored_battery_rf",
            builder -> builder
                    .persistent(ExtraCodecs.NON_NEGATIVE_INT)
                    .networkSynchronized(ByteBufCodecs.VAR_INT)
    );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> AMMUNITION = COMPONENTS.registerComponentType(
            "ammunition",
            builder -> builder
                    .persistent(ExtraCodecs.NON_NEGATIVE_INT)
                    .networkSynchronized(ByteBufCodecs.VAR_INT)
    );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> IN_USE = COMPONENTS.registerComponentType(
            "in_use",
            builder -> builder
                    .persistent(Codec.BOOL)
                    .networkSynchronized(ByteBufCodecs.BOOL)
    );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<SimpleFluidContent>> STORED_FLUID = COMPONENTS.registerComponentType(
            "stored_fluid",
            builder -> builder
                    .persistent(SimpleFluidContent.CODEC)
                    .networkSynchronized(SimpleFluidContent.STREAM_CODEC)
    );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<String>> FLUID_TYPE = COMPONENTS.registerComponentType(
            "fluid_type",
            builder -> builder
                    .persistent(Codec.STRING)
                    .networkSynchronized(ByteBufCodecs.STRING_UTF8)
    );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ResourceLocation>> A_RESOURCE_LOCATION = COMPONENTS.registerComponentType(
            "a_resource_location",
            builder -> builder
                    .persistent(ResourceLocation.CODEC)
                    .networkSynchronized(ResourceLocation.STREAM_CODEC)
    );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<CompoundTag>> FLOPPY_DISK_DATA =
            COMPONENTS.registerComponentType(
                    "floppy_disk_data",
                    builder -> builder
                            .persistent(CompoundTag.CODEC)
                            .networkSynchronized(ByteBufCodecs.COMPOUND_TAG));

    public static void register(IEventBus eventBus) {
        COMPONENTS.register(eventBus);
    }
}
