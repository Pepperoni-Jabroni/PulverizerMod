package pepjebs.pulverizermod.block;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.Level;
import pepjebs.pulverizermod.PulverizerMod;

public class PulverizerBlocks {

    public static final Block PULVERIZER;

    private static Block registerBlock(String name, Block block){
        return Registry.register(Registry.BLOCK, new Identifier(PulverizerMod.MODID, name), block);
    }

    static{
        PULVERIZER = registerBlock("pulverizer", new PulverizerBlock(FabricBlockSettings.of(Material.STONE).strength(2.5f).luminance(l -> l.get(Properties.LIT) ? 13 : 0)));

    }

    public static void load(){
        PulverizerMod.LOGGER.log(Level.INFO, "Loading Blocks");
    }
}
