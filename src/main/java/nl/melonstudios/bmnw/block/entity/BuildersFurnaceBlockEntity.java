package nl.melonstudios.bmnw.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.items.ItemStackHandler;
import nl.melonstudios.bmnw.init.BMNWBlockEntities;
import nl.melonstudios.bmnw.init.BMNWRecipes;
import nl.melonstudios.bmnw.init.BMNWTags;
import nl.melonstudios.bmnw.interfaces.ITickable;
import nl.melonstudios.bmnw.screen.BuildersFurnaceMenu;
import nl.melonstudios.bmnw.softcoded.recipe.BuildersSmeltingRecipe;
import nl.melonstudios.bmnw.softcoded.recipe.BuildersSmeltingRecipeInput;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class BuildersFurnaceBlockEntity extends SyncedBlockEntity implements MenuProvider, ITickable {
    public static final int SLOT_FUEL = 0;
    public static final int SLOT_INPUT = 1;
    public static final int SLOT_OUTPUT = 2;

    public BuildersFurnaceBlockEntity(BlockPos pos, BlockState blockState) {
        super(BMNWBlockEntities.BUILDERS_FURNACE.get(), pos, blockState);
    }

    public final ItemStackHandler inventory = new ItemStackHandler(3) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            BuildersFurnaceBlockEntity.this.notifyChange();
        }
    };

    public int progress;
    public int maxProgress;
    public int fuelTicks;
    public int maxFuelTicks;

    public final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> progress;
                case 1 -> maxProgress;
                case 2 -> fuelTicks;
                case 3 -> maxFuelTicks;
                default -> throw new IllegalStateException("Unexpected value: " + index);
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> progress = value;
                case 1 -> maxProgress = value;
                case 2 -> fuelTicks = value;
                case 3 -> maxFuelTicks = value;
                default -> throw new IllegalStateException("Unexpected value: " + index);
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    };

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.bmnw.builders_furnace");
    }
    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new BuildersFurnaceMenu(containerId, playerInventory, this, this.data);
    }

    @Override
    protected void save(CompoundTag nbt, HolderLookup.Provider registries, boolean packet) {
        nbt.put("Inventory", this.inventory.serializeNBT(registries));
        if (!packet) {
            nbt.putInt("progress", this.progress);
            nbt.putInt("maxProgress", this.maxProgress);
            nbt.putInt("fuelTicks", this.fuelTicks);
            nbt.putInt("maxFuelTicks", this.maxFuelTicks);
        }
    }
    @Override
    protected void load(CompoundTag nbt, HolderLookup.Provider registries, boolean packet) {
        this.inventory.deserializeNBT(registries, nbt.getCompound("Inventory"));
        if (!packet) {
            this.progress = nbt.getInt("progress");
            this.maxProgress = nbt.getInt("maxProgress");
            this.fuelTicks = nbt.getInt("fuelTicks");
            this.maxFuelTicks = nbt.getInt("maxFuelTicks");
        }
    }

    @Override
    public void update() {
        if (this.level.isClientSide) return;
        if (this.maxFuelTicks <= 0) {
            ItemStack fuel = this.inventory.getStackInSlot(SLOT_FUEL);
            if (!fuel.isEmpty()) {
                if (fuel.is(BMNWTags.Items.INFINITE_FUEL_SOURCES)) {
                    this.fuelTicks = 20;
                    this.maxFuelTicks = 20;
                    this.notifyChange();
                } else {
                    int ticks = fuel.getBurnTime(null);
                    if (ticks > 0) {
                        this.fuelTicks = ticks;
                        this.maxFuelTicks = ticks;
                        if (!fuel.is(Items.LAVA_BUCKET)) fuel.shrink(1);
                        else this.inventory.setStackInSlot(SLOT_FUEL, Items.BUCKET.getDefaultInstance());
                        this.notifyChange();
                    }
                }
            }
        }

        boolean shouldReset = true;
        boolean shouldBeLit = false;

        recipe:
        if (this.fuelTicks > 0) {
            this.setChanged();
            shouldBeLit = true;
            this.fuelTicks--;
            ItemStack input = this.inventory.getStackInSlot(SLOT_INPUT);
            if (input.isEmpty()) break recipe;
            Optional<RecipeHolder<BuildersSmeltingRecipe>> optionalRecipe = this.getRecipe();
            if (optionalRecipe.isEmpty()) break recipe;
            RecipeHolder<BuildersSmeltingRecipe> recipe = optionalRecipe.get();
            if (this.inventory.insertItem(SLOT_OUTPUT, recipe.value().output(), true).isEmpty()) {
                shouldReset = false;
                this.maxProgress = recipe.value().recipeTime();
                if (this.progress++ >= this.maxProgress) {
                    this.progress = 0;
                    input.shrink(1);
                    this.inventory.insertItem(SLOT_OUTPUT, recipe.value().output(), false);
                    this.notifyChange();
                }
            }
        } else this.maxFuelTicks = 0;

        if (shouldReset) {
            this.setChanged();
            this.progress = 0;
            this.maxProgress = 0;
        }

        if (shouldBeLit != this.getBlockState().getValue(BlockStateProperties.LIT)) {
            this.level.setBlock(this.worldPosition, this.getBlockState().setValue(BlockStateProperties.LIT, shouldBeLit), 3);
        }
    }

    private Optional<RecipeHolder<BuildersSmeltingRecipe>> getRecipe() {
        return this.level.getRecipeManager()
                .getRecipeFor(BMNWRecipes.BUILDERS_SMELTING_TYPE.get(),
                        new BuildersSmeltingRecipeInput(this.inventory.getStackInSlot(SLOT_INPUT)), this.level);
    }
}
