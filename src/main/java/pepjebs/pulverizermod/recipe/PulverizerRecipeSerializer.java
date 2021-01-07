package pepjebs.pulverizermod.recipe;

import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class PulverizerRecipeSerializer implements RecipeSerializer<PulverizerRecipe> {
    @Override
    public PulverizerRecipe read(Identifier id, JsonObject json) {
        return PulverizerRecipe.CODEC.decode(JsonOps.INSTANCE, json).getOrThrow(false, System.err::println).getFirst();
    }

    @Override
    public PulverizerRecipe read(Identifier id, PacketByteBuf buf) {
        Item ingredient = Registry.ITEM.get(buf.readIdentifier());
        ItemStack itemStack = buf.readItemStack();
        int pulverizeTime = buf.readInt();
        String category = buf.readString();
        return new PulverizerRecipe(id, ingredient, itemStack, pulverizeTime, category);
    }

    @Override
    public void write(PacketByteBuf buf, PulverizerRecipe recipe) {
        buf.writeIdentifier(Registry.ITEM.getId(recipe.ingredient));
        buf.writeItemStack(recipe.result);
        buf.writeInt(recipe.pulverizeTime);
        buf.writeString(recipe.category);
    }
}
