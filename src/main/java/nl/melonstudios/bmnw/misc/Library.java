package nl.melonstudios.bmnw.misc;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.*;

import javax.annotation.Nullable;
import java.lang.Math;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.List;
import java.util.function.Predicate;

public class Library {
    public static float getKiloRedstoneWattHour(int rfTick) {
        return rfTick / 3_600_000.0f;
    }

    public static final int PACKED_LIGHT = 0xf000f0;
    public static final Direction[] DIRECTIONS_WITH_NULL = {
            Direction.DOWN,
            Direction.UP,
            Direction.NORTH,
            Direction.SOUTH,
            Direction.WEST,
            Direction.EAST,
            null
    };

    public static void dropItem(Level level, BlockPos pos, ItemStack stack) {
        ItemEntity entity = new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stack);
        level.addFreshEntity(entity);
    }

    public static final float[] BRIGHTNESS_BY_AXIS = {
            0.85F, /* X */ 1.0F, /* Y */ 0.92F, /* Z */
    };
    public static float brightnessByDirection(Direction d) {
        return BRIGHTNESS_BY_AXIS[d.getAxis().ordinal()];
    }

    public static final float PX = 0.0625f;

    public static boolean isObstructed(Level level, double x, double y, double z, double a, double b, double c) {
        return isObstructed(level, new Vec3(x, y, z), new Vec3(a, b, c));
    }
    public static boolean isObstructed(Level level, Vec3 a, Vec3 b) {
        BlockHitResult result = level.clip(new ClipContext(a, b, ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY, CollisionContext.empty()));
        return result.getType() == HitResult.Type.MISS;
    }

    public static final Predicate<Object> ALWAYS_TRUE = (obj) -> true;
    public static final Predicate<Object> ALWAYS_FALSE = (obj) -> false;

    public static final List<Direction> SURROUND_X =
            ImmutableList.of(Direction.UP, Direction.DOWN, Direction.NORTH, Direction.SOUTH);
    public static final List<Direction> SURROUND_Y =
            ImmutableList.of(Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST);
    public static final List<Direction> SURROUND_Z =
            ImmutableList.of(Direction.UP, Direction.DOWN, Direction.EAST, Direction.WEST);
    public static List<Direction> getSurrounding(Direction.Axis axis) {
        return switch (axis) {
            case X -> SURROUND_X;
            case Y -> SURROUND_Y;
            case Z -> SURROUND_Z;
        };
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public static <T extends BlockEntity> T getBlockEntityOfType(Level level, BlockPos pos, Class<T> clazz) {
        BlockEntity be = level.getBlockEntity(pos);
        if (clazz.isInstance(be)) return (T) be;
        return null;
    }

    public static Direction.Axis toggleAxis(Direction.Axis toggle, Direction.Axis around) {
        return switch (around) {
            case X -> switch (toggle) {
                case X -> Direction.Axis.X;
                case Y -> Direction.Axis.Z;
                case Z -> Direction.Axis.Y;
            };
            case Y -> switch (toggle) {
                case X -> Direction.Axis.Z;
                case Y -> Direction.Axis.Y;
                case Z -> Direction.Axis.X;
            };
            case Z -> switch (toggle) {
                case X -> Direction.Axis.Y;
                case Y -> Direction.Axis.X;
                case Z -> Direction.Axis.Z;
            };
        };
    }

    public static Direction determineBlockPartition3D(UseOnContext context) {
        return determineBlockPartition3D(context.getClickedPos(), context.getClickLocation());
    }
    public static Direction determineBlockPartition3D(BlockPos pos, Vec3 click) {
        return determineBlockPartition3D(click.subtract(pos.getCenter()));
    }
    public static Direction determineBlockPartition3D(Vec3 clickLocation) {
        return Direction.getNearest(clickLocation);
    }
    public static Direction determineBlockPartition2D(UseOnContext context) {
        return determineBlockPartition2D(context.getClickedPos(), context.getClickLocation());
    }
    public static Direction determineBlockPartition2D(BlockPos pos, Vec3 click) {
        return determineBlockPartition2D(click.subtract(pos.getCenter()));
    }
    public static Direction determineBlockPartition2D(Vec3 clickLocation) {
        Direction d = Direction.getNearest(clickLocation.multiply(1, 0, 1));
        if (d.getAxis() == Direction.Axis.Y) return null;
        return d;
    }

    public static final int A_GIGABYTE_I = 1073741824;
    public static final long A_GIGABYTE_L = 1073741824L;
    public static final float A_GIGABYTE_F = 1073741824.0F;
    public static final double A_GIGABYTE_D = 1073741824.0D;
    public static double toGB(long bytes, int precision) {
        if (precision == -1) return bytes / A_GIGABYTE_D;
        if (precision == 0) return bytes / A_GIGABYTE_L;
        double thing = bytes / A_GIGABYTE_D;
        int mul = (int)Math.pow(10, precision);
        return (double)((int)(thing*mul))/mul;
    }

    public static double limitPrecision(double value, int precision) {
        if (precision == 0) return (long)value;
        int mul = (int)Math.pow(10, precision);
        return (double)((int)(value*mul))/mul;
    }

    public static void renderLeash(
            Vec3 from, Vec3 to, PoseStack poseStack, MultiBufferSource bufferSource,
            int fromSky, int fromBlock, int toSky, int toBlock
    ) {
        renderLeash(from, to, 0.5F, 0.4F, 0.3F, poseStack, bufferSource, fromSky, fromBlock, toSky, toBlock);
    }

    public static void renderLeash(
            Vec3 from, Vec3 to, float r, float g, float b,
            PoseStack poseStack, MultiBufferSource bufferSource,
            int fromSky, int fromBlock, int toSky, int toBlock
    ) {
        renderLeash(from, to, r, g, b, poseStack, bufferSource, fromSky, fromBlock, toSky, toBlock, false, 24);
    }

    @OnlyIn(Dist.CLIENT)
    public static void renderLeash(
            Vec3 from, Vec3 to, float r, float g, float b,
            PoseStack poseStack, MultiBufferSource bufferSource,
            int fromSky, int fromBlock, int toSky, int toBlock,
            boolean disableSegmentation, int segments
    ) {
        poseStack.pushPose();
        float dx = (float) (to.x - from.x);
        float dy = (float) (to.y - from.y);
        float dz = (float) (to.z - from.z);

        VertexConsumer consumer = bufferSource.getBuffer(RenderType.LEASH);
        Matrix4f matrix = poseStack.last().pose();

        float x2 = Mth.invSqrt(dx * dx + dz * dz) * 0.025F / 2.0F;
        float y2 = dz * x2;
        float z2 = dx * x2;

        for (int i = 0; i <= segments; i++) {
            addVertexPair_leash(
                    consumer, matrix, dx, dy, dz,
                    fromBlock, toBlock, fromSky, toSky,
                    0.025F, 0.025F, y2, z2, i, false,
                    r, g, b, disableSegmentation, segments
            );
        }
        for (int i = segments; i >= 0; i--) {
            addVertexPair_leash(
                    consumer, matrix, dx, dy, dz,
                    fromBlock, toBlock, fromSky, toSky,
                    0.025F, 0, y2, z2, i, true,
                    r, g, b, disableSegmentation, segments
            );
        }

        poseStack.popPose();
    }

    @OnlyIn(Dist.CLIENT)
    public static void renderLeash(
            Vec3 from, Vec3 to, float r, float g, float b,
            PoseStack poseStack, MultiBufferSource bufferSource,
            int fromSky, int fromBlock, int toSky, int toBlock,
            boolean disableSegmentation, int segments, Boolean limit
    ) {
        if (limit == null) {
            renderLeash(from, to, r, g, b, poseStack, bufferSource, fromSky, fromBlock, toSky, toBlock, disableSegmentation, segments);
            return;
        }
        poseStack.pushPose();
        float dx = (float) (to.x - from.x);
        float dy = (float) (to.y - from.y);
        float dz = (float) (to.z - from.z);

        VertexConsumer consumer = bufferSource.getBuffer(RenderType.LEASH);
        Matrix4f matrix = poseStack.last().pose();

        float x2 = Mth.invSqrt(dx * dx + dz * dz) * 0.025F / 2.0F;
        float y2 = dz * x2;
        float z2 = dx * x2;

        if (limit == Boolean.FALSE) {
            for (int i = 0; i <= segments; i++) {
                addVertexPair_leash(
                        consumer, matrix, dx, dy, dz,
                        fromBlock, toBlock, fromSky, toSky,
                        0.025F, 0.025F, y2, z2, i, false,
                        r, g, b, disableSegmentation, segments
                );
            }
        }
        if (limit == Boolean.TRUE) {
            for (int i = segments; i >= 0; i--) {
                addVertexPair_leash(
                        consumer, matrix, dx, dy, dz,
                        fromBlock, toBlock, fromSky, toSky,
                        0.025F, 0, y2, z2, i, true,
                        r, g, b, disableSegmentation, segments
                );
            }
        }

        poseStack.popPose();
    }

    @OnlyIn(Dist.CLIENT)
    private static void addVertexPair_leash(
            VertexConsumer buffer,
            Matrix4f pose,
            float startX,
            float startY,
            float startZ,
            int entityBlockLight,
            int holderBlockLight,
            int entitySkyLight,
            int holderSkyLight,
            float yOffset,
            float dy,
            float dx,
            float dz,
            int index,
            boolean reverse,
            float r,
            float g,
            float b,
            boolean disableSegmentation,
            int segments
    ) {
        float f = (float)index / segments;
        int i = (int) Mth.lerp(f, (float)entityBlockLight, (float)holderBlockLight);
        int j = (int)Mth.lerp(f, (float)entitySkyLight, (float)holderSkyLight);
        int k = LightTexture.pack(i, j);
        float brightness = disableSegmentation ? 1.0F : (index % 2 == (reverse ? 1 : 0) ? 0.7F : 1.0F);
        float br = r * brightness;
        float bg = g * brightness;
        float bb = b * brightness;
        float f5 = startX * f;
        float f6 = startY > 0.0F ? startY * f * f : startY - startY * (1.0F - f) * (1.0F - f);
        float f7 = startZ * f;
        buffer.addVertex(pose, f5 - dx, f6 + dy, f7 + dz).setColor(br, bg, bb, 1.0F).setLight(k);
        buffer.addVertex(pose, f5 + dx, f6 + yOffset - dy, f7 - dz).setColor(br, bg, bb, 1.0F).setLight(k);
    }
}
