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
import pepjebs.pulverizermod.PulverizerMod;
import pepjebs.pulverizermod.block.entity.PulverizerBlockEntity;
import pepjebs.pulverizermod.config.PulverizerModConfig;

import java.util.Map;

public class PulverizerRecipe implements Recipe<PulverizerBlockEntity> {

    public final Identifier id;
    public final Item ingredient;
    public final ItemStack result;
    public final int pulverizeTime;
    public final String category;

    public static final RecipeSerializer<PulverizerRecipe> SERIALIZER = new PulverizerRecipeSerializer();
    public static final Codec<PulverizerRecipe> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("id").forGetter(PulverizerRecipe::getId),
            Registry.ITEM.fieldOf("ingredient").forGetter(PulverizerRecipe::getIngredient),
            ItemStack.CODEC.fieldOf("result").forGetter(PulverizerRecipe::getOutput),
            Codec.INT.fieldOf("pulverizetime").forGetter(PulverizerRecipe::getPulverizeTime),
            Codec.STRING.fieldOf("category").forGetter(PulverizerRecipe::getCategory)
    ).apply(instance, PulverizerRecipe::new));

    public PulverizerRecipe(Identifier id, Item ingredient, ItemStack result, int pulverizeTime, String category) {
        this.id = id;
        this.ingredient = ingredient;
        this.result = result;
        this.pulverizeTime = pulverizeTime;
        this.category = category;
    }

    @Override
    public boolean matches(PulverizerBlockEntity inv, World world) {
        if (!getConfigEntryValueForCategory(this.category)) return false;
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

    public String getCategory() {return this.getCategory();}

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

    private boolean getConfigEntryValueForCategory(String category) {
        // Only return "false" if the feature is truly, explicitly disabled
        if (PulverizerMod.CONFIG == null) return true;
        switch (category) {
            case "bonus":
                return PulverizerMod.CONFIG.enableBonusRecipes;
            case "erosion":
                return PulverizerMod.CONFIG.enableErosionRecipes;
            case "dye":
                return PulverizerMod.CONFIG.enableDyeRecipes;
            case "recycle":
                return PulverizerMod.CONFIG.enableRecycleRecipes;
            case "ore":
                return PulverizerMod.CONFIG.enableOreRecipes;
        }
        return true;
    }
}
