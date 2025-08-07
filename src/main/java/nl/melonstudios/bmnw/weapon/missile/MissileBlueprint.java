package nl.melonstudios.bmnw.weapon.missile;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import nl.melonstudios.bmnw.weapon.missile.registry.MissileFins;
import nl.melonstudios.bmnw.weapon.missile.registry.MissileFuselage;
import nl.melonstudios.bmnw.weapon.missile.registry.MissileThruster;
import nl.melonstudios.bmnw.weapon.missile.registry.MissileWarhead;

import javax.annotation.ParametersAreNonnullByDefault;

import static java.util.Objects.requireNonNull;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class MissileBlueprint {
    private static final Registry<MissileThruster> THRUSTER_REGISTRY = MissileSystem.registry_MissileThruster();
    private static final Registry<MissileFins> FINS_REGISTRY = MissileSystem.registry_MissileFins();
    private static final Registry<MissileFuselage> FUSELAGE_REGISTRY = MissileSystem.registry_MissileFuselage();
    private static final Registry<MissileWarhead> WARHEAD_REGISTRY = MissileSystem.registry_MissileWarhead();

    private MissileBlueprint() {}
    public static MissileBlueprint create() {
        return new MissileBlueprint();
    }
    public static MissileBlueprint copy(MissileBlueprint other) {
        MissileBlueprint blueprint = create();
        blueprint.validationFlag = other.validationFlag;
        blueprint.thruster = other.thruster;
        blueprint.fins = other.fins;
        blueprint.fuselage = other.fuselage;
        blueprint.warhead = other.warhead;
        return blueprint;
    }
    public static MissileBlueprint fromProperties(
            MissileThruster thruster,
            MissileFins fins,
            MissileFuselage fuselage,
            MissileWarhead warhead
    ) {
        MissileBlueprint blueprint = create();
        blueprint.validationFlag = true;
        blueprint.thruster = thruster;
        blueprint.fins = fins;
        blueprint.fuselage = fuselage;
        blueprint.warhead = warhead;
        return blueprint;
    }
    private static MissileBlueprint fromCodec(
            ResourceLocation thruster,
            ResourceLocation fins,
            ResourceLocation fuselage,
            ResourceLocation warhead
    ) {
        return fromProperties(
                requireNonNull(THRUSTER_REGISTRY.get(thruster)),
                requireNonNull(FINS_REGISTRY.get(fins)),
                requireNonNull(FUSELAGE_REGISTRY.get(fuselage)),
                requireNonNull(WARHEAD_REGISTRY.get(warhead))
        );
    }

    public boolean validationFlag = false;
    public MissileThruster thruster;
    public MissileFins fins;
    public MissileFuselage fuselage;
    public MissileWarhead warhead;

    public int calculateHealth() {
        if (!this.validationFlag) return 69;
        return this.thruster.health + this.fins.health + this.fuselage.health + this.warhead.health;
    }
    public float calculateHalfHeight() {
        if (!this.validationFlag) return 0.0F;
        return (this.thruster.getHeight() + this.fuselage.getHeight() + this.warhead.getHeight()) * 0.5F;
    }
    public boolean isValidStructure() {
        if (this.thruster.getSize() != this.fins.getSize()) return false;
        if (this.thruster.getSize() != this.fuselage.getBottomSize()) return false;
        if (this.warhead.getSize() != this.fuselage.getTopSize()) return false;
        if (this.thruster.getFuelType() != this.fuselage.getFuelType()) return false;
        return this.thruster.getMaxPayload() >= this.warhead.getWeight();
    }

    public void takeInspiration(MissileBlueprint other) {
        this.validationFlag = other.validationFlag;
        this.thruster = other.thruster;
        this.fins = other.fins;
        this.fuselage = other.fuselage;
        this.warhead = other.warhead;
    }

    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();

        nbt.putString("thruster", requireNonNull(THRUSTER_REGISTRY.getKey(this.thruster)).toString());
        nbt.putString("fins", requireNonNull(FINS_REGISTRY.getKey(this.fins)).toString());
        nbt.putString("fuselage", requireNonNull(FUSELAGE_REGISTRY.getKey(this.fuselage)).toString());
        nbt.putString("warhead", requireNonNull(WARHEAD_REGISTRY.getKey(this.warhead)).toString());

        return nbt;
    }
    public void deserializeNBT(CompoundTag nbt) {
        this.thruster = requireNonNull(THRUSTER_REGISTRY.get(ResourceLocation.parse(nbt.getString("thruster"))));
        this.fins = requireNonNull(FINS_REGISTRY.get(ResourceLocation.parse(nbt.getString("fins"))));
        this.fuselage = requireNonNull(FUSELAGE_REGISTRY.get(ResourceLocation.parse(nbt.getString("fuselage"))));
        this.warhead = requireNonNull(WARHEAD_REGISTRY.get(ResourceLocation.parse(nbt.getString("warhead"))));
        this.validationFlag = true;
    }

    public void writeBuf(ByteBuf buf) {
        buf.writeInt(THRUSTER_REGISTRY.getId(this.thruster));
        buf.writeInt(FINS_REGISTRY.getId(this.fins));
        buf.writeInt(FUSELAGE_REGISTRY.getId(this.fuselage));
        buf.writeInt(WARHEAD_REGISTRY.getId(this.warhead));
    }
    public void readBuf(ByteBuf buf) {
        this.thruster = requireNonNull(THRUSTER_REGISTRY.byId(buf.readInt()));
        this.fins = requireNonNull(FINS_REGISTRY.byId(buf.readInt()));
        this.fuselage = requireNonNull(FUSELAGE_REGISTRY.byId(buf.readInt()));
        this.warhead = requireNonNull(WARHEAD_REGISTRY.byId(buf.readInt()));
        this.validationFlag = true;
    }

    public static Codec<MissileBlueprint> CODEC = RecordCodecBuilder.create(inst ->
            inst.group(
                    ResourceLocation.CODEC.fieldOf("thruster")
                            .forGetter((bp) -> THRUSTER_REGISTRY.getKey(bp.thruster)),
                    ResourceLocation.CODEC.fieldOf("fins")
                            .forGetter((bp) -> FINS_REGISTRY.getKey(bp.fins)),
                    ResourceLocation.CODEC.fieldOf("fuselage")
                            .forGetter((bp) -> FUSELAGE_REGISTRY.getKey(bp.fuselage)),
                    ResourceLocation.CODEC.fieldOf("warhead")
                            .forGetter((bp) -> WARHEAD_REGISTRY.getKey(bp.warhead))
            ).apply(inst, MissileBlueprint::fromCodec)
    );
    public static StreamCodec<ByteBuf, MissileBlueprint> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public MissileBlueprint decode(ByteBuf buf) {
            MissileBlueprint blueprint = create();
            blueprint.readBuf(buf);
            return blueprint;
        }

        @Override
        public void encode(ByteBuf buf, MissileBlueprint value) {
            value.writeBuf(buf);
        }
    };
}
