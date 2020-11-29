package pepjebs.pulverizermod.block.entity;

import pepjebs.pulverizermod.PulverizerMod;
import pepjebs.pulverizermod.block.PulverizerBlocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.Level;

public class PulverizerBlockEntityType {

    public static final BlockEntityType<PulverizerBlockEntity> PULVERIZER_BLOCK_ENTITY;

    static{
        PULVERIZER_BLOCK_ENTITY = registerBlockEntity("pulverizer_block_entity");
    }

    private static BlockEntityType<PulverizerBlockEntity> registerBlockEntity(String name) {
        return Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(PulverizerMod.MODID, name),
                BlockEntityType.Builder.create(PulverizerBlockEntity::new, PulverizerBlocks.PULVERIZER).build(null));
    }

    public static void load() {
        PulverizerMod.LOGGER.log(Level.INFO, "Loading Block Entities");
    }
}
