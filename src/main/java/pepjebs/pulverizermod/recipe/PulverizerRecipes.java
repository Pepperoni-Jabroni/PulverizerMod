package pepjebs.pulverizermod.recipe;

import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.Level;
import pepjebs.pulverizermod.PulverizerMod;

public class PulverizerRecipes {
    public static final RecipeSerializer<PulverizerRecipe> PULVERIZER_SERIALIZER;
    public static final RecipeType<PulverizerRecipe> PULVERIZER_RECIPE_TYPE;

    static {
        PULVERIZER_SERIALIZER = Registry.register(Registry.RECIPE_SERIALIZER, new Identifier(PulverizerMod.MODID, "pulverizer"), PulverizerRecipe.SERIALIZER);

        PULVERIZER_RECIPE_TYPE = Registry.register(Registry.RECIPE_TYPE, new Identifier(PulverizerMod.MODID, "pulverizer"), new RecipeType<PulverizerRecipe>() {
            public String toString() {
                return PulverizerMod.MODID + ":pulverizer";
            }
        });

    }

    public static void load() {
        PulverizerMod.LOGGER.log(Level.INFO, "Loading Recipes");
    }
}
