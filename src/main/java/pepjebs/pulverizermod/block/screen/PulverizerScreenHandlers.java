package pepjebs.pulverizermod.block.screen;

import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.Level;
import pepjebs.pulverizermod.PulverizerMod;

public class PulverizerScreenHandlers {

    public static final ScreenHandlerType<PulverizerScreenHandler> PULVERIZER_SCREEN_HANDLER;

    static {
        PULVERIZER_SCREEN_HANDLER = ScreenHandlerRegistry.registerExtended(new Identifier(PulverizerMod.MODID, "pulverizer"), PulverizerScreenHandler::new);
    }

    public static void load() {
        PulverizerMod.LOGGER.log(Level.INFO, "Loading Screen Handlers");
    }
}
