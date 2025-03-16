package nl.melonstudios.bmnw.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.items.ItemStackHandler;
import nl.melonstudios.bmnw.hardcoded.recipe.AlloyingRecipes;
import nl.melonstudios.bmnw.init.BMNWBlockEntities;
import nl.melonstudios.bmnw.init.BMNWTags;
import nl.melonstudios.bmnw.screen.AlloyFurnaceMenu;
import org.jetbrains.annotations.Nullable;

public class AlloyBlastFurnaceBlockEntity extends BlockEntity implements MenuProvider {
    private final RandomSource soundRand = RandomSource.create();
    public AlloyBlastFurnaceBlockEntity(BlockPos pos, BlockState blockState) {
        super(BMNWBlockEntities.ALLOY_BLAST_FURNACE.get(), pos, blockState);
    }

    public final ItemStackHandler inventory = new ItemStackHandler(4);
    public int progress = 0;
    public static final int maxProgress = 100;
    public int fuelTicks = 0;
    public int maxFuelTicks = 0;
    public int infiniteFuel = 0;

    protected final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> progress;
                case 1 -> fuelTicks;
                case 2 -> maxFuelTicks;
                case 3 -> infiniteFuel;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0: progress = value; break;
                case 1: fuelTicks = value; break;
                case 2: maxFuelTicks = value; break;
                case 3: infiniteFuel = value; break;
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    };

    private void tick() {
        BlockState state = this.getBlockState();
        boolean lit = state.getValue(BlockStateProperties.LIT);
        assert level != null;
        if (!this.level.isClientSide()) this.infiniteFuel = 0;
        if (this.fuelTicks <= 0) {
            ItemStack fuel = this.inventory.getStackInSlot(0);
            if (!fuel.isEmpty()) {
                if (fuel.is(BMNWTags.Items.INFINITE_FUEL_SOURCES)) {
                    this.fuelTicks = 1;
                    this.maxFuelTicks = 1;
                    this.infiniteFuel = 1;
                } else {
                    int ticks = fuel.getBurnTime(null) / 4;
                    if (ticks > 0) {
                        this.fuelTicks = ticks;
                        this.maxFuelTicks = ticks;
                        if (!fuel.is(BMNWTags.Items.INFINITE_FUEL_SOURCES)) fuel.shrink(1);
                    }
                }
            }
        }

        boolean shouldReset = true;
        boolean shouldBeLit = false;
        if (this.fuelTicks > 0) {
            ItemStack input1 = this.inventory.getStackInSlot(1);
            ItemStack input2 = this.inventory.getStackInSlot(2);
            ItemStack output = this.inventory.getStackInSlot(3);
            if (output.isEmpty() || output.getCount() < output.getMaxStackSize()) {
                ItemStack result = AlloyingRecipes.instance.getResult(input1, input2);
                if (!result.isEmpty()) {
                    if (output.isEmpty() || (ItemStack.isSameItem(result, output) &&
                            result.getCount() + output.getCount() <= result.getMaxStackSize())) {
                        shouldReset = false;
                        shouldBeLit = true;
                        this.progress++;
                        this.fuelTicks--;
                        if (this.progress >= maxProgress) {
                            this.progress = 0;
                            input1.shrink(1);
                            input2.shrink(1);
                            if (output.isEmpty()) this.inventory.setStackInSlot(3, result.copy());
                            else output.grow(result.getCount());
                            this.level.playSound(
                                    null, this.worldPosition,
                                    SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS,
                                    0.1f, 1f
                            );
                        }
                    }
                }
            }
        }
        if (shouldReset) this.progress = 0;
        else if (this.soundRand.nextFloat() < 0.05) {
            this.level.playSound(
                    null, this.worldPosition,
                    SoundEvents.BLASTFURNACE_FIRE_CRACKLE, SoundSource.BLOCKS,
                    1, 0.9f + this.soundRand.nextFloat() * 0.2f
            );
        }
        if (shouldBeLit != lit) {
            this.level.setBlock(this.worldPosition, state.setValue(BlockStateProperties.LIT, shouldBeLit), 3);
        }
    }

    public void drops() {
        SimpleContainer inv = new SimpleContainer(this.inventory.getSlots());
        for (int i = 0; i < inventory.getSlots(); i++) {
            inv.setItem(i, inventory.getStackInSlot(i));
        }

        Containers.dropContents(this.level, this.worldPosition, inv);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);

        tag.put("Inventory", this.inventory.serializeNBT(registries));

        tag.putInt("progress", this.progress);
        tag.putInt("fuelTicks", this.fuelTicks);
        tag.putInt("maxFuelTicks", this.maxFuelTicks);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        this.inventory.deserializeNBT(registries, tag.getCompound("Inventory"));

        this.progress = tag.getInt("progress");
        this.fuelTicks = tag.getInt("fuelTicks");
        this.maxFuelTicks = tag.getInt("maxFuelTicks");
    }

    public static void tick(Level level, BlockPos pos, BlockState state, BlockEntity be) {
        if (be instanceof AlloyBlastFurnaceBlockEntity furnace) furnace.tick();
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.bmnw.alloy_blast_furnace");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new AlloyFurnaceMenu(containerId, playerInventory, this, this.dataAccess);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }
}
