package nl.melonstudios.bmnw.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import nl.melonstudios.bmnw.init.BMNWBlocks;
import nl.melonstudios.bmnw.init.BMNWEntityTypes;
import nl.melonstudios.bmnw.init.BMNWParticleTypes;
import org.joml.Quaternionf;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MeteoriteEntity extends Entity {
    private static final Random random = new Random();

    public static final EntityDataAccessor<Long> COURSE_X_DATA = SynchedEntityData.defineId(MeteoriteEntity.class, EntityDataSerializers.LONG);
    public static final EntityDataAccessor<Long> COURSE_Z_DATA = SynchedEntityData.defineId(MeteoriteEntity.class, EntityDataSerializers.LONG);

    public MeteoriteEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
        this.courseX = random.nextDouble() * 0.4 - 0.2;
        this.courseZ = random.nextDouble() * 0.4 - 0.2;
    }
    public MeteoriteEntity(Level level, double x, double z, double courseX, double courseZ) {
        super(BMNWEntityTypes.METEORITE.get(), level);
        setPos(x, 512, z);
        this.courseX = courseX;
        this.courseZ = courseZ;
    }
    public MeteoriteEntity(Level level, double x, double z) {
        this(level, x, z, random.nextDouble() * 0.4 - 0.2, random.nextDouble() * 0.4 - 0.2);
    }

    private int createType(int radius, int type) {
        return (radius & 0xff) | type << 8;
    }

    private int radiusFromType(int type) {
        return type & 0xff;
    }

    private int typeFromType(int type) {
        return (type >> 8) & 0xff;
    }

    private double courseX, courseZ;
    public final Quaternionf rotation = new Quaternionf().rotationAxis(360, random.nextFloat(), random.nextFloat(), random.nextFloat());

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(COURSE_X_DATA, Double.doubleToLongBits(this.courseX));
        builder.define(COURSE_Z_DATA, Double.doubleToLongBits(this.courseZ));
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        this.courseX = compound.getDouble("courseX");
        this.courseZ = compound.getDouble("courseZ");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        compound.putDouble("courseX", this.courseX);
        compound.putDouble("courseZ", this.courseZ);
    }

    private int age = 0;

    @Override
    public void tick() {
        if (level().isClientSide()) {
            this.courseX = Double.longBitsToDouble(entityData.get(COURSE_X_DATA));
            this.courseZ = Double.longBitsToDouble(entityData.get(COURSE_Z_DATA));
        } else {
            entityData.set(COURSE_X_DATA, Double.doubleToLongBits(this.courseX));
            entityData.set(COURSE_Z_DATA, Double.doubleToLongBits(this.courseZ));
        }
        if (this.horizontalCollision || this.verticalCollision) {
            if (!this.level().isClientSide()) {
                BlockPos pos = new BlockPos(
                        (int) this.getX(),
                        (int) this.getY(),
                        (int) this.getZ()
                );
                this.whenLand(pos);
            }
            this.kill();
        } else {
            this.level().addParticle(
                    BMNWParticleTypes.FIRE_TRAIL.get(),
                    getX(), getY(), getZ(), 0, 0, 0
            );
            this.move(MoverType.SELF, new Vec3(courseX, -4, courseZ));
        }
    }

    private int randomRadius() {
        if (random.nextInt(50) == 0) return 4;
        if (random.nextInt(20) == 0) return 3;
        return random.nextBoolean() ? 2 : 1;
    }

    private void whenLand(BlockPos pos) {
        this.level().playSound(null, getX(), getY(), getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.NEUTRAL, 10, 0.8f);
        int type = random.nextInt(3);
        int radius = randomRadius();
        switch (type) {
            case 0 -> this.placeDefault(pos, radius);
            case 1 -> this.placeMarble(pos, radius);
            case 2 -> this.placeMiddleTreasure(pos, radius);
        }
    }

    private void placeDefault(BlockPos pos, int radius) {
        this.placeEmpty(pos, radius);
        this.partialSphere(pos, radius, BMNWBlocks.METEORITE_TREASURE.get().defaultBlockState(), 0.05f);
    }

    private void placeMarble(BlockPos pos, int radius) {
        this.placeEmpty(pos, radius);
        this.level().setBlock(pos, BMNWBlocks.METEORITE_FIRE_MARBLE_ORE.get().defaultBlockState(), 3);
    }

    private void placeEmpty(BlockPos pos, int radius) {
        this.sphere(pos, radius+1, Blocks.AIR.defaultBlockState());
        this.sphere(pos, radius, BMNWBlocks.HOT_METEORITE_COBBLESTONE.get().defaultBlockState());
        this.partialSphere(pos, radius, BMNWBlocks.METEORITE_IRON_ORE.get().defaultBlockState(), 0.1f);
    }

    private void placeMiddleTreasure(BlockPos pos, int radius) {
        this.placeEmpty(pos, radius);
        this.level().setBlock(pos, BMNWBlocks.METEORITE_TREASURE.get().defaultBlockState(), 3);
    }

    private void sphere(BlockPos pPos, int pRadius, BlockState pState) {
        BlockPos.MutableBlockPos mPos = pPos.mutable();
        for (int x = -pRadius; x <= pRadius; x++) {
            for (int y = -pRadius; y <= pRadius; y++) {
                for (int z = -pRadius; z <= pRadius; z++) {
                    if ((int)distanceTo0(x, y, z) <= pRadius) {
                        mPos.set(pPos.getX() + x, pPos.getY() + y, pPos.getZ() + z);
                        this.level().setBlock(mPos, pState, 3);
                    }
                }
            }
        }
    }

    private void partialSphere(BlockPos pPos, int pRadius, BlockState pState, float pFilling) {
        BlockPos.MutableBlockPos mPos = pPos.mutable();
        for (int x = -pRadius; x <= pRadius; x++) {
            for (int y = -pRadius; y <= pRadius; y++) {
                for (int z = -pRadius; z <= pRadius; z++) {
                    if ((int)distanceTo0(x, y, z) <= pRadius && random.nextFloat() < pFilling) {
                        mPos.set(pPos.getX() + x, pPos.getY() + y, pPos.getZ() + z);
                        this.level().setBlock(mPos, pState, 3);
                    }
                }
            }
        }
    }

    private double distanceTo0(int x, int y, int z) {
        return Math.sqrt(x*x + y*y + z*z);
    }

    public static void invalidateDimension(ResourceLocation dimension) {
        invalidDimensions.add(dimension);
    }
    private static final List<ResourceLocation> invalidDimensions = new ArrayList<>();
    public static boolean dimensionInvalid(ResourceLocation dimension) {
        return invalidDimensions.contains(dimension);
    }

    static {
        invalidateDimension(ResourceLocation.withDefaultNamespace("the_nether"));
        invalidateDimension(ResourceLocation.withDefaultNamespace("the_end"));
    }

    public static void spawnIfReady(Player player) {
        if (player.level().isClientSide() || dimensionInvalid(player.level().dimension().location())) return;
        long totalTime = player.level().getGameTime();
        if (totalTime % 10000 == (player.hashCode() % 10000)) {
            spawn(player);
        }
    }
    public static void spawn(Player player) {
        MeteoriteEntity entity = new MeteoriteEntity(player.level(),
                player.getX() + (random.nextDouble() * 32 - 16),
                player.getZ() + (random.nextDouble() * 32 - 16),
                random.nextDouble() * 2 - 1,
                random.nextDouble() * 2 - 1);
        player.level().addFreshEntity(entity);
    }
}
