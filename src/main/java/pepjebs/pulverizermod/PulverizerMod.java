package pepjebs.pulverizermod;

import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pepjebs.pulverizermod.block.PulverizerBlocks;
import pepjebs.pulverizermod.block.entity.PulverizerBlockEntityType;
import pepjebs.pulverizermod.block.screen.PulverizerScreenHandlers;
import pepjebs.pulverizermod.config.PulverizerModConfig;
import pepjebs.pulverizermod.item.PulverizerItem;
import pepjebs.pulverizermod.recipe.PulverizerRecipes;


public class PulverizerMod implements ModInitializer {

    public static final String MODID = "pulverizer_mod";
    public static final Logger LOGGER = LogManager.getLogger(MODID);
    public static PulverizerModConfig CONFIG = null;

    public static final Identifier PULVERIZING_SOUND_ID = new Identifier("pulverizer_mod:pulverizing");
    public static SoundEvent PULVERIZING_SOUND_EVENT = new SoundEvent(PULVERIZING_SOUND_ID);

    @Override
    public void onInitialize() {
        AutoConfig.register(PulverizerModConfig.class, JanksonConfigSerializer::new);
        CONFIG = AutoConfig.getConfigHolder(PulverizerModConfig.class).getConfig();

        PulverizerBlocks.load();
        PulverizerBlockEntityType.load();
        PulverizerItem.load();
        PulverizerRecipes.load();
        PulverizerScreenHandlers.load();
        Registry.register(Registry.SOUND_EVENT, PulverizerMod.PULVERIZING_SOUND_ID, PULVERIZING_SOUND_EVENT);
    }

}