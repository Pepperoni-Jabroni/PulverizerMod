package pepjebs.pulverizermod.block.entity;

import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.Tickable;
import net.minecraft.item.ItemStack;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import pepjebs.pulverizermod.PulverizerMod;
import pepjebs.pulverizermod.block.screen.PulverizerScreenHandler;
import pepjebs.pulverizermod.recipe.PulverizerRecipe;
import pepjebs.pulverizermod.recipe.PulverizerRecipes;

import java.util.Iterator;

public class PulverizerBlockEntity extends LockableContainerBlockEntity implements Tickable, SidedInventory, ExtendedScreenHandlerFactory {

    public DefaultedList<ItemStack> inventory;

    private static final int[] TOP_SLOTS = new int[]{0};
    private static final int[] BOTTOM_SLOTS = new int[]{3};
    private static final int[] SIDE_SLOTS = new int[]{2};
    private static final int[] LEFT_SLOTS = new int[]{1};

    private int burnTime; // Fuel source burn time, goes down
    private int fuelTime; // Amount of time the given fuel will burn, const
    private int pulverizeTime; // Amount of cooking taken place, goes up
    private int pulverizeTimeTotal; // Total amount of time to cook, const
    private int grindTime;

    public final PropertyDelegate propertyDelegate = new PropertyDelegate() {
        public int get(int prop_idx) {
            switch (prop_idx) {
                case 0:
                    return PulverizerBlockEntity.this.burnTime;
                case 1:
                    return PulverizerBlockEntity.this.fuelTime;
                case 2:
                    return PulverizerBlockEntity.this.pulverizeTime;
                case 3:
                    return PulverizerBlockEntity.this.pulverizeTimeTotal;
                case 4:
                    return PulverizerBlockEntity.this.grindTime;
                default:
                    return 0;
            }
        }

        public void set(int prop_idx, int new_val) {
            switch (prop_idx) {
                case 0:
                    PulverizerBlockEntity.this.burnTime = new_val;
                    break;
                case 1:
                    PulverizerBlockEntity.this.fuelTime = new_val;
                    break;
                case 2:
                    PulverizerBlockEntity.this.pulverizeTime = new_val;
                    break;
                case 3:
                    PulverizerBlockEntity.this.pulverizeTimeTotal = new_val;
                    break;
                case 4:
                    PulverizerBlockEntity.this.grindTime = new_val;
                    break;
            }

        }

        public int size() {
            return 5;
        }
    };

    public PulverizerBlockEntity() {
        super(PulverizerBlockEntityType.PULVERIZER_BLOCK_ENTITY);
        this.inventory = DefaultedList.ofSize(4, ItemStack.EMPTY);
        this.pulverizeTime = 0;
        this.pulverizeTimeTotal = 0;
        this.fuelTime = 0;
        this.burnTime = 0;
        this.grindTime = 0;
    }

    public PulverizerBlockEntity(ItemStack stack) {
        super(PulverizerBlockEntityType.PULVERIZER_BLOCK_ENTITY);
        this.inventory = DefaultedList.ofSize(4, ItemStack.EMPTY);
        this.inventory.set(0, stack);
        this.pulverizeTime = 0;
        this.pulverizeTimeTotal = 0;
        this.fuelTime = 0;
        this.burnTime = 0;
        this.grindTime = 0;
    }

    @Override
    public Text getContainerName() {
        return new TranslatableText("container.pulverizer_mod.pulverizer", null);
    }

    @Override
    public ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return new PulverizerScreenHandler(syncId, playerInventory, this);
    }

    @Override
    public void tick() {
        boolean is_burning = this.isBurning();
        boolean is_starting_burn = false;
        if (this.isBurning()) {
            this.burnTime--;
        }

        if (this.world != null && !this.world.isClient) {
            ItemStack fuelStack = this.inventory.get(2);
            if (!this.isBurning() && (fuelStack.isEmpty() || (this.inventory.get(0)).isEmpty())) {
                this.pulverizeTime = MathHelper.clamp(this.pulverizeTime - 2, 0, this.pulverizeTimeTotal);
            } else {
                PulverizerRecipe recipe = this.world.getRecipeManager().getFirstMatch(PulverizerRecipes.PULVERIZER_RECIPE_TYPE, this, this.world).orElse(null);

                ItemStack grindStack = this.inventory.get(1);
                if (!grindStack.isEmpty() && this.grindTime == 0) {
                    Item item2 = grindStack.getItem();
                    this.grindTime = PulverizerBlockEntity.loadPulverizeMaximumFromConfig();
                    grindStack.decrement(1);
                    if (grindStack.isEmpty()) {
                        Item item3 = item2.getRecipeRemainder();
                        this.inventory.set(1, item3 == null ? ItemStack.EMPTY : new ItemStack(item3));
                    }
                }

                if (!this.isBurning() && this.canAcceptRecipeOutput(recipe) && this.grindTime > 0) {
                    this.burnTime = this.getFuelTime(fuelStack);
                    this.fuelTime = this.burnTime;

                    if (this.isBurning()) {
                        is_starting_burn = true;
                        this.pulverizeTimeTotal = this.getPulverizeTimeTotal();
                        if (!fuelStack.isEmpty()) {
                            Item item = fuelStack.getItem();
                            fuelStack.decrement(1);
                            if (fuelStack.isEmpty()) {
                                Item item2 = item.getRecipeRemainder();
                                this.inventory.set(2, item2 == null ? ItemStack.EMPTY : new ItemStack(item2));
                            }
                        }
                    }
                }

                if (this.isBurning() && this.canAcceptRecipeOutput(recipe) && this.grindTime > 0) {
                    ++this.pulverizeTime;
                    if (this.pulverizeTime == this.pulverizeTimeTotal) {
                        this.pulverizeTime = 0;
                        this.pulverizeTimeTotal = this.getPulverizeTimeTotal();
                        this.craftRecipe(recipe);
                        is_starting_burn = true;
                        this.grindTime--;
                    }
                } else {
                    this.pulverizeTime = 0;
                }
            }

            if (is_burning != this.isBurning()) {
                is_starting_burn = true;
                this.world.setBlockState(this.pos, this.world.getBlockState(this.pos).with(AbstractFurnaceBlock.LIT, this.isBurning()), 3);
            }
        }

        if (is_starting_burn) {
            this.markDirty();
        }
    }

    private int getPulverizeTimeTotal() {
        PulverizerRecipe recipe = this.world.getRecipeManager().getFirstMatch(PulverizerRecipes.PULVERIZER_RECIPE_TYPE, this, this.world).orElse(null);
        return recipe.getPulverizeTime();
    }

    private int getFuelTime(ItemStack stack) {
        if (FuelRegistry.INSTANCE == null) return 0;
        if (stack == null || stack.isEmpty()) return 0;
        try {
            return FuelRegistry.INSTANCE.get(stack.getItem());
        } catch (NullPointerException e) {
            return 0;
        }
    }

    private boolean canAcceptRecipeOutput(@Nullable PulverizerRecipe recipe) {
        if (!(this.inventory.get(0)).isEmpty() && recipe != null) {
            ItemStack recipeOutput = recipe.getOutput();
            int outputAmount = recipeOutput.getCount();
            if (recipeOutput.isEmpty()) {
                return false;
            } else {
                ItemStack blockOutput = this.inventory.get(3);
                if (blockOutput.isEmpty()) {
                    return true;
                } else if (!blockOutput.isItemEqualIgnoreDamage(recipeOutput)) {
                    return false;
                } else if (blockOutput.getCount() + outputAmount <= this.getMaxCountPerStack() && blockOutput.getCount() + outputAmount <= blockOutput.getMaxCount()) {
                    return true;
                } else {
                    return blockOutput.getCount() + outputAmount <= recipeOutput.getMaxCount();
                }
            }
        } else {
            return false;
        }
    }

    private void craftRecipe(@Nullable PulverizerRecipe recipe) {
        if (recipe != null && this.canAcceptRecipeOutput(recipe)) {
            ItemStack inputStack = this.inventory.get(0);
            ItemStack recipeOutputStack = recipe.getOutput();
            ItemStack outputStack = this.inventory.get(3);
            if (outputStack.isEmpty()) {
                this.inventory.set(3, recipeOutputStack.copy());
            } else if (outputStack.getItem() == recipeOutputStack.getItem()) {
                outputStack.increment(recipe.result.getCount());
            }

            inputStack.decrement(1);
        }
    }

    @Override
    public int size() {
        return this.inventory.size();
    }

    @Override
    public boolean isEmpty() {
        Iterator invIterator = this.inventory.iterator();

        ItemStack stack;
        do {
            if (!invIterator.hasNext()) {
                return true;
            }

            stack = (ItemStack) invIterator.next();
        } while (stack.isEmpty());

        return false;
    }

    @Override
    public ItemStack getStack(int index) {
        return this.inventory.get(index);
    }


    @Override
    public ItemStack removeStack(int index, int amount) {
        return Inventories.splitStack(this.inventory, index, amount);
    }


    @Override
    public ItemStack removeStack(int index) {
        return Inventories.removeStack(this.inventory, index);
    }

    @Override
    public void setStack(int index, ItemStack stack) {
        this.inventory.set(index, stack);
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        if (this.world != null && this.world.getBlockEntity(this.pos) != this) {
            return false;
        } else {
            return player.squaredDistanceTo((double) this.pos.getX() + 0.5D, (double) this.pos.getY() + 0.5D, (double) this.pos.getZ() + 0.5D) <= 64.0D;
        }
    }

    @Override
    public boolean isValid(int index, ItemStack stack) {
        if (index == 0) {
            return true;
        } else if (index == 1) {
            return stack.isItemEqualIgnoreDamage(new ItemStack(Items.DIAMOND));
        } else if (index == 2) {
            return FuelRegistry.INSTANCE.get(stack.getItem()) != null;
        } else {
            return false;
        }
    }

    @Override
    public void clear() {
        this.inventory.clear();
    }

    @Override
    public int[] getAvailableSlots(Direction direction) {
        if (direction == Direction.DOWN) {
            return BOTTOM_SLOTS;
        } else if (direction == Direction.UP) {
            return TOP_SLOTS;
        }
        if (this.world.getBlockState(this.pos).get(HorizontalFacingBlock.FACING) == Direction.NORTH) {
            if (direction == Direction.EAST) {
                return LEFT_SLOTS;
            } else {
                return SIDE_SLOTS;
            }
        } else if (this.world.getBlockState(this.pos).get(HorizontalFacingBlock.FACING) == Direction.EAST) {
            if (direction == Direction.SOUTH) {
                return LEFT_SLOTS;
            } else {
                return SIDE_SLOTS;
            }
        } else if (this.world.getBlockState(this.pos).get(HorizontalFacingBlock.FACING) == Direction.SOUTH) {
            if (direction == Direction.WEST) {
                return LEFT_SLOTS;
            } else {
                return SIDE_SLOTS;
            }
        } else if (this.world.getBlockState(this.pos).get(HorizontalFacingBlock.FACING) == Direction.WEST) {
            if (direction == Direction.NORTH) {
                return LEFT_SLOTS;
            } else {
                return SIDE_SLOTS;
            }
        }
        return SIDE_SLOTS;
    }

    @Override
    public boolean canInsert(int index, ItemStack stack, Direction direction) {
        return this.isValid(index, stack);
    }

    @Override
    public boolean canExtract(int index, ItemStack stack, Direction direction) {
        return index == 3;
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);
        this.burnTime = tag.getInt("burn_time");
        this.fuelTime = tag.getInt("fuel_time");
        this.pulverizeTime = tag.getInt("pulverize_time");
        this.pulverizeTimeTotal = tag.getInt("pulverize_time_total");
        this.grindTime = tag.getInt("grind_time");
        Inventories.fromTag(tag, this.inventory);
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        tag.putInt("burn_time", this.burnTime);
        tag.putInt("fuel_time", this.fuelTime);
        tag.putInt("pulverize_time", this.pulverizeTime);
        tag.putInt("pulverize_time_total", this.pulverizeTimeTotal);
        tag.putInt("grind_time", this.grindTime);
        Inventories.toTag(tag, this.inventory);
        return tag;
    }

    public boolean isBurning() {
        return this.burnTime > 0;
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity serverPlayerEntity, PacketByteBuf packetByteBuf) {
        packetByteBuf.writeItemStack(this.inventory.get(0));
        packetByteBuf.writeItemStack(this.inventory.get(1));
        packetByteBuf.writeItemStack(this.inventory.get(2));
        packetByteBuf.writeItemStack(this.inventory.get(3));
    }

    public static int loadPulverizeMaximumFromConfig() {
        if (PulverizerMod.CONFIG != null) return PulverizerMod.CONFIG.pulverizerDiamondDurability;
        return 1561;
    }
}
