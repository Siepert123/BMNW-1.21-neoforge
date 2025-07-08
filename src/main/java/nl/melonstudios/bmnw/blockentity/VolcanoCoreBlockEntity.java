package nl.melonstudios.bmnw.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import nl.melonstudios.bmnw.block.misc.VolcanoCoreBlock;
import nl.melonstudios.bmnw.cfg.BMNWServerConfig;
import nl.melonstudios.bmnw.entity.LavaEjectionEntity;
import nl.melonstudios.bmnw.init.BMNWBiomes;
import nl.melonstudios.bmnw.init.BMNWBlockEntities;
import nl.melonstudios.bmnw.init.BMNWBlocks;
import nl.melonstudios.bmnw.init.BMNWParticleTypes;
import nl.melonstudios.bmnw.interfaces.ITickable;
import nl.melonstudios.bmnw.misc.Library;

import java.util.Random;
import java.util.Set;

public class VolcanoCoreBlockEntity extends BlockEntity implements ITickable {
    private static final int GROWTH_TIMER = 256;
    private static final int EXTINGUISH_TIMER = 72000;
    private static final Random volcanoRnd = new Random();
    public VolcanoCoreBlockEntity(BlockPos pos, BlockState blockState) {
        super(BMNWBlockEntities.VOLCANO_CORE.get(), pos, blockState);
    }

    public VolcanoCoreBlock getBlock() {
        return (VolcanoCoreBlock) this.getBlockState().getBlock();
    }

    public int volcanoTimer = 10;
    public int updateTimer = 0;
    private int age = 0;
    private int biomeReplacementCountdown = 1200;
    private int biomeExtent = 0;
    @Override
    public void update() {
        if (!this.level.isClientSide) {
            this.updateTimer++;
            this.age++;
            if (this.volcanoTimer-- <= 0) {
                this.volcanoTimer = 10;
                VolcanoCoreBlock block = this.getBlock();
                this.level.addFreshEntity(new LavaEjectionEntity(this.level, this.worldPosition, block.type));
                for (int i = 0; i < volcanoRnd.nextInt(4); i++) {
                    this.level.addFreshEntity(new LavaEjectionEntity(this.level, this.worldPosition, block.type));
                }
                this.raiseChannel();
                this.ensureLavaBath();
                this.freeTheWay();
                for (ServerPlayer player : ((ServerLevel)this.level).getPlayers(Library.ALWAYS_TRUE)) {
                    ((ServerLevel)this.level).sendParticles(player, BMNWParticleTypes.VOLCANO_SMOKE.get(), true,
                            this.worldPosition.getX() + volcanoRnd.nextDouble()*4-1.5,
                            this.worldPosition.getY() + 0.5,
                            this.worldPosition.getZ() + volcanoRnd.nextDouble()*4-1.5,
                            1, 0, 0, 0, 0);
                }
                if (BMNWServerConfig.forceLoadVolcanoChunks() && this.level instanceof ServerLevel serverLevel) {
                    int x = SectionPos.blockToSectionCoord(this.worldPosition.getX());
                    int z = SectionPos.blockToSectionCoord(this.worldPosition.getZ());
                    for (int i = -2; i <= 2; i++) {
                        for (int j = -2; j <= 2; j++) {
                            serverLevel.setChunkForced(x+i, z+j, true);
                        }
                    }
                }
            }
            if (this.shouldGrow() && this.updateTimer >= GROWTH_TIMER) {
                this.updateTimer = 0;
                this.grow();
            } else if (this.shouldExtinguish() && this.updateTimer >= EXTINGUISH_TIMER) {
                this.level.setBlock(this.worldPosition, this.getBlock().type.state, 3);
            }
            if (this.biomeExtent < 3) {
                if (this.biomeReplacementCountdown-- <= 0) {
                    this.biomeExtent++;
                    this.biomeReplacementCountdown = 1200*(this.biomeExtent+1);
                    if (this.level instanceof ServerLevel level) {
                        int range = this.biomeExtent * 16;
                        BMNWBiomes.fillBiomeCylindrical(level,
                                this.worldPosition.offset(0, -range/2, 0),
                                range/2+range*2,
                                range,
                                BMNWBiomes.volcano_wastes(level)
                        );
                        if (this.biomeExtent == 3) {
                            BMNWBiomes.fillBiome(level,
                                    this.worldPosition.offset(-16, -16, -16),
                                    this.worldPosition.offset(16, 64, 16),
                                    BMNWBiomes.volcano_wastes_severe(level)
                            );
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onLoad() {
        if (BMNWServerConfig.forceLoadVolcanoChunks() && this.level instanceof ServerLevel serverLevel) {
            int x = SectionPos.blockToSectionCoord(this.worldPosition.getX());
            int z = SectionPos.blockToSectionCoord(this.worldPosition.getZ());
            for (int i = -2; i <= 2; i++) {
                for (int j = -2; j <= 2; j++) {
                    serverLevel.setChunkForced(x+i, z+j, true);
                }
            }
        }
    }

    private void raiseChannel() {
        BlockPos pos = this.worldPosition.offset(
                volcanoRnd.nextInt(21) - 10,
                volcanoRnd.nextInt(11),
                volcanoRnd.nextInt(21) - 10
        );

        if (this.level.getBlockState(pos).canBeReplaced() &&
                this.level.getBlockState(pos.below()).is(this.getBlock().type.state.getBlock())) {
            this.level.setBlock(pos, this.getBlock().type.state, 3);
        }
    }
    private void ensureLavaBath() {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    if (x != 0 || y != 0 || z != 0) {
                        pos.setWithOffset(this.worldPosition, x, y, z);
                        this.level.setBlock(pos, this.getBlock().type.state, 3);
                    }
                }
            }
        }
    }
    private void freeTheWay() {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                for (int y = 2; y <= 16; y++) {
                    pos.setWithOffset(this.worldPosition, x, y, z);
                    if (this.basalts.contains(this.level.getBlockState(pos).getBlock())) {
                        this.level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                    }
                }
            }
        }
    }
    private void grow() {
        this.level.setBlock(this.worldPosition.above(), this.getBlockState(), 3);
        this.level.setBlock(this.worldPosition, this.getBlock().type.state, 3);
    }

    private boolean shouldGrow() {
        return this.getBlock().grows && this.worldPosition.getY() < BMNWServerConfig.maxVolcanoHeight();
    }
    private boolean shouldExtinguish() {
        return this.getBlock().extinguishes;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("volcanoTimer", this.volcanoTimer);
        tag.putInt("updateTimer", this.updateTimer);
        tag.putInt("age", this.age);
        tag.putInt("biomeTimer", this.biomeReplacementCountdown);
        tag.putInt("biomeExtent", this.biomeExtent);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.volcanoTimer = tag.getInt("volcanoTimer");
        this.updateTimer = tag.getInt("updateTimer");
        this.age = tag.getInt("age");
        this.biomeReplacementCountdown = tag.getInt("biomeTimer");
        this.biomeExtent = tag.getInt("biomeExtent");
    }

    private final Set<Block> basalts = Set.of(
            Blocks.BASALT,
            BMNWBlocks.BASALT_IRON_ORE.get(),
            BMNWBlocks.BASALT_BAUXITE_ORE.get()
    );
}
