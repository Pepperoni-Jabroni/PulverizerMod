package pepjebs.pulverizermod.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import pepjebs.pulverizermod.block.screen.PulverizerScreen;
import pepjebs.pulverizermod.block.screen.PulverizerScreenHandlers;

@Environment(EnvType.CLIENT)
public class PulverizerClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ScreenRegistry.register(PulverizerScreenHandlers.PULVERIZER_SCREEN_HANDLER, PulverizerScreen::new);
    }
}
