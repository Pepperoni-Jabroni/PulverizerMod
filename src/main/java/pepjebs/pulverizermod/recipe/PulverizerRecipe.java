package pepjebs.pulverizermod.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import pepjebs.pulverizermod.block.entity.PulverizerBlockEntity;

public class PulverizerRecipe implements Recipe<PulverizerBlockEntity> {

    public final Identifier id;
    public final Item ingredient;
    public final ItemStack result;
    public final int pulverizeTime;

    public static final RecipeSerializer<PulverizerRecipe> SERIALIZER = new PulverizerRecipeSerializer();
    public static final Codec<PulverizerRecipe> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("id").forGetter(PulverizerRecipe::getId),
            Registry.ITEM.fieldOf("ingredient").forGetter(PulverizerRecipe::getIngredient),
            ItemStack.CODEC.fieldOf("result").forGetter(PulverizerRecipe::getOutput),
            Codec.INT.fieldOf("pulverizetime").forGetter(PulverizerRecipe::getPulverizeTime)
    ).apply(instance, PulverizerRecipe::new));

    public PulverizerRecipe(Identifier id, Item ingredient, ItemStack result, int pulverizeTime) {
        this.id = id;
        this.ingredient = ingredient;
        this.result = result;
        this.pulverizeTime = pulverizeTime;
    }

    @Override
    public boolean matches(PulverizerBlockEntity inv, World world) {
        return this.ingredient.equals(inv.getStack(0).getItem()) && !inv.getStack(0).isDamaged();
    }

    // Ignorable
    @Override
    public ItemStack craft(PulverizerBlockEntity inv) {
        return ItemStack.EMPTY;
    }

    // Ignorable
    @Override
    public boolean fits(int width, int height) {
        return false;
    }

    public Item getIngredient() {
        return this.ingredient;
    }

    public int getPulverizeTime() {
        return this.pulverizeTime;
    }

    // ...Return the output
    @Override
    public ItemStack getOutput() {
        return this.result;
    }

    @Override
    public DefaultedList<ItemStack> getRemainingStacks(PulverizerBlockEntity inventory) {
        return null;
    }

    @Override
    public DefaultedList<Ingredient> getPreviewInputs() {
        return null;
    }

    @Override
    public boolean isIgnoredInRecipeBook() {
        return true;
    }

    @Override
    public String getGroup() {
        return "pulverizer";
    }

    @Override
    public ItemStack getRecipeKindIcon() {
        return null;
    }

    // Simply return ID
    @Override
    public Identifier getId() {
        return this.id;
    }

    // Return registered serializer
    @Override
    public RecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }

    // Return registered recipe type
    @Override
    public RecipeType<?> getType() {
        return PulverizerRecipes.PULVERIZER_RECIPE_TYPE;
    }
}
