package pepjebs.pulverizermod.item;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.Level;
import pepjebs.pulverizermod.PulverizerMod;
import pepjebs.pulverizermod.block.PulverizerBlocks;

public class PulverizerItem {

    private static final Item PULVERIZER;

    private static Item registerBlockItem(String name, Block block, ItemGroup itemGroup){
        return Registry.register(Registry.ITEM, new Identifier(PulverizerMod.MODID, name), new BlockItem(block, new Item.Settings().group(itemGroup)));
    }

    static{
        PULVERIZER = registerBlockItem("pulverizer", PulverizerBlocks.PULVERIZER, ItemGroup.REDSTONE);
    }

    public static void load() {
        PulverizerMod.LOGGER.log(Level.INFO, "Loading Items");
    }
}
