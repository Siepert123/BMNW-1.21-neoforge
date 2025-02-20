package nl.melonstudios.bmnw.block.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import nl.melonstudios.bmnw.init.BMNWBlocks;
import nl.melonstudios.bmnw.block.HatchBlock;
import nl.melonstudios.bmnw.init.BMNWBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Quaternionf;

public class HatchBlockEntity extends BlockEntity {
    public HatchBlockEntity(BlockPos pos, BlockState blockState) {
        super(BMNWBlockEntities.HATCH.get(), pos, blockState);
        open = blockState.getValue(HatchBlock.OPEN);
    }

    public boolean open;
    public int ticks = 0;

    public final int openAnimTime = 20;
    public final int openScrewTime = 20;

    public void rotateScrew(PoseStack poseStack, float partialTicks) {
        rotateLid(poseStack, partialTicks);
    }
    public void rotateLid(PoseStack poseStack, float partialTicks) {
        if (this.ticks < openScrewTime) return;
        float ticks = this.ticks - partialTicks - this.openScrewTime;
        float angle = open ? (float) Math.toRadians(ticks / 20 * 90 - 90) : (float) Math.toRadians(-ticks / 20 * 90);
        switch (getFacing()) {
            case SOUTH -> poseStack.rotateAround(new Quaternionf().rotateAxis(angle, 1, 0, 0), 0, 0, 0);
            case NORTH -> {
                poseStack.translate(0, 0, 1);
                poseStack.mulPose(new Quaternionf().rotateAxis(-angle, 1, 0, 0));
                poseStack.translate(0, 0, -1);
            }
            case WEST -> {
                poseStack.translate(1, 0, 0);
                poseStack.mulPose(new Quaternionf().rotateAxis(angle, 0, 0, 1));
                poseStack.translate(-1, 0, 0);
            }
            case EAST -> poseStack.rotateAround(new Quaternionf().rotateAxis(-angle, 0, 0, 1), 0, 0, 0);
        }
    }

    public void tick() {
        if (ticks > 0) ticks--;
    }
    public void resetAnimTicks() {
        this.ticks = openAnimTime;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);

        tag.putInt("ticks", this.ticks);
        tag.putBoolean("open", this.open);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        this.ticks = tag.getInt("ticks");
        this.open = tag.getBoolean("open");
    }

    public Direction getFacing() {
        return getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
    }
    public boolean isOpen() {
        return open;
    }

    public boolean toggleable() {
        return ticks <= 0;
    }

    public BlockState getOtherPart() {
        return BMNWBlocks.CONCRETE_ENCAPSULATED_LADDER.get().defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, getFacing());
    }
}
