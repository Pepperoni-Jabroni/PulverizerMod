package pepjebs.pulverizermod.block.screen;

import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.world.World;
import pepjebs.pulverizermod.block.entity.PulverizerBlockEntity;
import pepjebs.pulverizermod.recipe.PulverizerRecipes;

public class PulverizerScreenHandler extends ScreenHandler {

    private final PulverizerBlockEntity pulverizer;
    protected final World world;

    public PulverizerScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        this(syncId, playerInventory, new PulverizerBlockEntity());
        this.pulverizer.inventory.set(0, buf.readItemStack());
        this.pulverizer.inventory.set(1, buf.readItemStack());
        this.pulverizer.inventory.set(2, buf.readItemStack());
        this.pulverizer.inventory.set(3, buf.readItemStack());
    }

    public PulverizerScreenHandler(int syncId, PlayerInventory playerInventory, PulverizerBlockEntity pulverizer) {
        super(PulverizerScreenHandlers.PULVERIZER_SCREEN_HANDLER, syncId);
        this.pulverizer = pulverizer;
        this.addProperties(this.pulverizer.propertyDelegate);
        this.world = playerInventory.player.world;

        // I love magic numbers
        this.addSlot(new Slot(pulverizer, 0, 56, 17));
        this.addSlot(new Slot(pulverizer, 1, 11, 35));
        this.addSlot(new Slot(pulverizer, 2, 56, 53));
        this.addSlot(new Slot(pulverizer, 3, 116, 35));

        // Shamelessly stolen from Fabric Wiki tutorial
        for (int m = 0; m < 3; m++) {
            for (int l = 0; l < 9; l++) {
                this.addSlot(new Slot(playerInventory, l + m * 9 + 9, 8 + l * 18, 84 + m * 18));
            }
        }
        for (int m = 0; m < 9; m++) {
            this.addSlot(new Slot(playerInventory, m, 8 + m * 18, 142));
        }
    }

    @Override
    public ItemStack transferSlot(PlayerEntity player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasStack()) {
            ItemStack itemStack2 = slot.getStack();
            itemStack = itemStack2.copy();
            if (index == 3) {
                if (!this.insertItem(itemStack2, 4, 40, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onStackChanged(itemStack2, itemStack);
            } else if (index != 0 && index != 1 && index != 2) {
                if (this.isPulverizable(itemStack2)) {
                    if (!this.insertItem(itemStack2, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (itemStack2.isItemEqualIgnoreDamage(new ItemStack(Items.DIAMOND))) {
                    if (!this.insertItem(itemStack2, 1, 2, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (this.isFuel(itemStack2)) {
                    if (!this.insertItem(itemStack2, 2, 3, false)) {
                        return ItemStack.EMPTY;
                    }
                }else if (index < 30) {
                    if (!this.insertItem(itemStack2, 30, 40, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index < 39 && !this.insertItem(itemStack2, 3, 30, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(itemStack2, 4, 40, false)) {
                return ItemStack.EMPTY;
            }

            if (itemStack2.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }

            if (itemStack2.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTakeItem(player, itemStack2);
        }

        return itemStack;
    }

    private boolean isPulverizable(ItemStack itemStack) {
        return this.world.getRecipeManager().getFirstMatch(PulverizerRecipes.PULVERIZER_RECIPE_TYPE, new PulverizerBlockEntity(itemStack), this.world).isPresent();
    }

    private boolean isFuel(ItemStack stack) {
        return FuelRegistry.INSTANCE.get(stack.getItem()) != null;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.pulverizer.canPlayerUse(player);
    }

    public boolean isBurning() {
        return this.pulverizer.isBurning();
    }

    public int getFuelProgress() {
        return this.pulverizer.propertyDelegate.get(0);
    }

    public int getFuelTimeTotal() {
        return this.pulverizer.propertyDelegate.get(1);
    }

    public int getPulverizeTime() {
        return this.pulverizer.propertyDelegate.get(2);
    }

    public int getPulverizeTimeTotal() {
        return this.pulverizer.propertyDelegate.get(3);
    }

    public int getGrindTime() {
        return this.pulverizer.propertyDelegate.get(4);
    }
}
