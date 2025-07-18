package nl.melonstudios.bmnw.block.entity;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.ItemStackHandler;
import nl.melonstudios.bmnw.audio.LargeShredderLoopSoundInstance;
import nl.melonstudios.bmnw.block.state.BMNWStateProperties;
import nl.melonstudios.bmnw.hardcoded.recipe.WrappedSingletonRecipeInput;
import nl.melonstudios.bmnw.init.*;
import nl.melonstudios.bmnw.interfaces.IExtendedEnergyStorage;
import nl.melonstudios.bmnw.interfaces.ITickable;
import nl.melonstudios.bmnw.misc.BrokenConsumer;
import nl.melonstudios.bmnw.misc.DistrictHolder;
import nl.melonstudios.bmnw.misc.ExtendedEnergyStorage;
import nl.melonstudios.bmnw.misc.StackMover;
import nl.melonstudios.bmnw.softcoded.recipe.ShreddingRecipe;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public class LargeShredderBlockEntity extends BlockEntity implements ITickable {
    public LargeShredderBlockEntity(BlockPos pos, BlockState blockState) {
        super(BMNWBlockEntities.LARGE_SHREDDER.get(), pos, blockState);
        this.multiblockSlave = blockState.getValue(BMNWStateProperties.MULTIBLOCK_SLAVE);
        this.inventory = new ItemStackHandler(2) {
                @Override
                protected void onContentsChanged(int slot) {
                    setChanged();
                }
                @Override
                public boolean isItemValid(int slot, ItemStack stack) {
                        return false;
                    }
        };
        this.energy = new ExtendedEnergyStorage(100000);
        this.itemSearchPos = pos.above(2);
        this.itemSearchArea = new AABB(pos.above());
        this.recipeInput = new WrappedSingletonRecipeInput(this.inventory, 0);
    }

    @Override
    public void onLoad() {
        if (this.level instanceof ServerLevel level) {
            forSlave((pos1) -> {
                if (level.hasNeighborSignal(pos1)) {
                    this.powered = true;
                    return true;
                }
                return false;
            });
            this.setChanged();
            this.sendData();
        }
    }

    public final boolean multiblockSlave;
    public boolean running = false;
    public boolean oldRunning = false;
    public boolean powered = false;

    public final ItemStackHandler inventory;
    public final IExtendedEnergyStorage energy;
    public final WrappedSingletonRecipeInput recipeInput;

    @Nullable
    public IExtendedEnergyStorage getEnergy(@Nullable Direction d) {
        if (d == null) return this.energy;
        return d == this.getFacing().getCounterClockWise() ? this.energy : null;
    }

    public Direction getFacing() {
        return this.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);

        if (!this.multiblockSlave) {
            tag.putBoolean("running", this.running || (this.startDelay > -1));

            tag.put("Inventory", this.inventory.serializeNBT(registries));
            tag.putInt("energy", this.energy.getEnergyStored());
            tag.putInt("shredProgress", this.shredProgress);
            tag.putBoolean("powered", this.powered);
        }
    }
    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        if (!this.multiblockSlave) {
            boolean wasRunning = this.running; //??? why is this inverted for some reason...
            this.running = tag.getBoolean("running");

            this.inventory.deserializeNBT(registries, tag.getCompound("Inventory"));
            this.energy.setEnergyStored(tag.getInt("energy"));
            this.shredProgress = tag.getInt("shredProgress");
            this.powered = tag.getBoolean("powered");

            if ((!wasRunning || this.firstNbtLoad) && this.running) {
                this.firstNbtLoad = false;
                DistrictHolder.clientOnly(() -> this::startLoop);
            }
        }
    }

    private boolean firstNbtLoad = true;

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return this.multiblockSlave ? null : ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag nbt = new CompoundTag();
        this.saveAdditional(nbt, registries);
        return nbt;
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider lookupProvider) {
        CompoundTag tag = pkt.getTag();
        this.loadAdditional(tag, lookupProvider);
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        this.loadAdditional(tag, lookupProvider);
    }

    public void sendData() {
        if (this.level instanceof ServerLevel serverLevel)
            serverLevel.getChunkSource().blockChanged(this.worldPosition);
    }

    public void setRunning(boolean running) {
        this.running = running;
        this.setChanged();
        this.sendData();
    }

    private final BlockPos itemSearchPos;
    private final AABB itemSearchArea;

    private int shredProgress = 0;

    private int startDelay = -1;

    @Override
    public void update() {
        if (this.multiblockSlave) return;

        this.oldRunning = this.running;

        assert this.level != null;
        if (!this.level.isClientSide && this.running && !this.canRun()) {
            this.setRunning(false);
            level.playSound(null, this.worldPosition, BMNWSounds.LARGE_SHREDDER_STOP.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
        }

        if (this.startDelay > -1) {
            this.startDelay--;
            if (this.startDelay <= 0) {
                this.startDelay = -1;
                this.running = true;
                this.setChanged();
                this.sendData();
            }
        }

        if (this.running) {
            if (this.level instanceof ServerLevel level) {
                this.energy.extractEnergy(50, false);
                if (this.inventory.getStackInSlot(0).isEmpty()) {
                    this.shredProgress = 0;
                    BlockState state = level.getBlockState(this.itemSearchPos);
                    Container container = StackMover.getBlockContainer(level, this.itemSearchPos, state);
                    if (container != null) {
                        for (int i = 0; i < container.getContainerSize(); i++) {
                            ItemStack stack = container.getItem(i);
                            if (!stack.isEmpty()) {
                                this.inventory.setStackInSlot(0, stack.copy());
                                container.setItem(i, ItemStack.EMPTY);
                                this.setChanged();
                                break;
                            }
                        }
                    } else if (!state.isFaceSturdy(level, this.itemSearchPos, Direction.DOWN)) {
                        List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, this.itemSearchArea);
                        for (ItemEntity entity : items) {
                            if (!entity.isRemoved()) {
                                ItemStack stack = entity.getItem();
                                this.inventory.setStackInSlot(0, stack.copy());
                                entity.setRemoved(Entity.RemovalReason.DISCARDED);
                                this.setChanged();
                                break;
                            }
                        }
                    }
                } else {
                    ItemStack input = this.inventory.getStackInSlot(0);
                    RecipeHolder<ShreddingRecipe> recipe = this.getRecipe().orElse(null);
                    boolean b = !input.isEmpty() && recipe != null;
                    ItemStack result = b ? recipe.value().result().copy() : new ItemStack(BMNWItems.DUST.get());
                    if ((this.inventory.getStackInSlot(1).isEmpty() || StackMover.canMergeItems(this.inventory.getStackInSlot(1), result))) {
                        this.setChanged();
                        if (++this.shredProgress >= 4) {
                            this.shredProgress = 0;
                            input.shrink(1);
                            if (this.inventory.getStackInSlot(1).isEmpty()) this.inventory.setStackInSlot(1, result);
                            else this.inventory.getStackInSlot(1).grow(result.getCount());
                        }
                    }
                }

                List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, this.itemSearchArea);
                for (LivingEntity entity : entities) {
                    entity.hurt(BMNWDamageSources.shredded(level), 10);
                }
            }
        }

        if (this.level instanceof ServerLevel level) {
            BlockPos energyExtractPos = this.worldPosition.relative(this.getFacing().getCounterClockWise());
            IEnergyStorage storage = level.getCapability(Capabilities.EnergyStorage.BLOCK, energyExtractPos, this.getFacing().getClockWise());
            if (storage != null && storage.canExtract()) {
                int max = this.energy.getMaxEnergyStored() - this.energy.getEnergyStored();
                int cap = storage.extractEnergy(max, true);
                this.energy.receiveEnergy(storage.extractEnergy(cap, false), false);
            }
            ItemStack out = this.inventory.getStackInSlot(1);
            if (!out.isEmpty()) {
                BlockPos outPos = this.worldPosition.relative(this.getFacing().getClockWise()).below();
                BlockState outState = level.getBlockState(outPos);
                Container container = StackMover.getBlockContainer(level, outPos, outState);
                if (container != null) {
                    for (int i = 0; i < container.getContainerSize(); i++) {
                        ItemStack stack = container.getItem(i);
                        if (stack.getCount() >= container.getMaxStackSize(stack)) continue;
                        if (stack.isEmpty()) {
                            container.setItem(i, out.copy());
                            out.setCount(0);
                            break;
                        }
                        if (ItemStack.isSameItemSameComponents(stack, out)) {
                            int add = container.getMaxStackSize() - stack.getCount();
                            int actualAdd = Math.min(out.getCount(), add);
                            container.getItem(i).grow(actualAdd);
                            out.shrink(actualAdd);
                            if (out.isEmpty()) break;
                        }
                    }
                }
            }

            boolean wasPowered = this.powered;

            this.powered = false;
            forSlave((pos1) -> {
                if (level.hasNeighborSignal(pos1)) {
                    this.powered = true;
                    return true;
                }
                return false;
            });
            this.setChanged();

            if (wasPowered != this.powered) {
                if (this.powered) {
                    if (this.running) {
                        this.setRunning(false);
                        level.playSound(null, this.worldPosition, BMNWSounds.LARGE_SHREDDER_STOP.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
                    } else {
                        if (this.canRun()) {
                            this.startDelay = 40;
                            level.playSound(null, this.worldPosition, BMNWSounds.LARGE_SHREDDER_START.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
                        } else {
                            level.playSound(null, this.worldPosition, BMNWSounds.LARGE_SHREDDER_FAIL.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
                        }
                    }
                }
            }
        }
    }

    public boolean canRun() {
        return this.energy.getEnergyStored() >= 50;
    }
    private void startLoop() {
        startLoop0(this);
    }

    @OnlyIn(Dist.CLIENT)
    private static void startLoop0(LargeShredderBlockEntity be) {
        Minecraft.getInstance().getSoundManager().play(new LargeShredderLoopSoundInstance(be));
    }

    public List<ItemStack> drops() {
        return ImmutableList.of(this.inventory.getStackInSlot(0), this.inventory.getStackInSlot(1));
    }

    public void forSlave(BrokenConsumer<BlockPos> action) {
        assert !this.multiblockSlave;
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        BlockPos pos = this.getBlockPos();
        Direction facing = this.getFacing();

        mutable.setWithOffset(pos, Direction.UP);
        if (action.accept(mutable)) return;
        mutable.setWithOffset(pos, Direction.DOWN);
        if (action.accept(mutable)) return;

        mutable.setWithOffset(pos, facing);
        if (action.accept(mutable)) return;

        mutable.setWithOffset(pos, facing.getOpposite());
        if (action.accept(mutable)) return;

        mutable.setWithOffset(pos.above(), facing);
        if (action.accept(mutable)) return;

        mutable.setWithOffset(pos.below(), facing);
        if (action.accept(mutable)) return;

        mutable.setWithOffset(pos.above(), facing.getOpposite());
        if (action.accept(mutable)) return;

        mutable.setWithOffset(pos.below(), facing.getOpposite());
        action.accept(mutable);
    }

    public boolean slaves() {
        assert this.level != null;
        assert !this.multiblockSlave;

        Block comp = BMNWBlocks.LARGE_SHREDDER.get();
        AtomicBoolean pass = new AtomicBoolean(true);
        this.forSlave((pos) -> {
            if (!this.level.getBlockState(pos).is(comp)) {
                pass.set(false);
                return true;
            }
            return false;
        });

        return pass.get();
    }
    public void check() {
        assert this.level != null;
        assert !this.multiblockSlave;
        if (!this.slaves()) {
            this.forSlave((pos) -> this.level.destroyBlock(pos, false));
            this.level.destroyBlock(this.worldPosition, true);
        }
    }

    public AABB renderBB = null;

    private Optional<RecipeHolder<ShreddingRecipe>> getRecipe() {
        return this.level.getRecipeManager()
                .getRecipeFor(BMNWRecipes.SHREDDING_TYPE.get(),
                        this.recipeInput, this.level);
    }
}
