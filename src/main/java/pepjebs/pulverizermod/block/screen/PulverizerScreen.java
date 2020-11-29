package pepjebs.pulverizermod.block.screen;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import pepjebs.pulverizermod.PulverizerMod;
import pepjebs.pulverizermod.block.entity.PulverizerBlockEntity;

public class PulverizerScreen extends HandledScreen<ScreenHandler> {

    private static final Identifier TEXTURE = new Identifier(PulverizerMod.MODID, "textures/gui/container/pulverizer.png");

    public PulverizerScreen(ScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        // Center the title
        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        drawMouseoverTooltip(matrices, mouseX, mouseY);
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        client.getTextureManager().bindTexture(TEXTURE);
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        drawTexture(matrices, x, y, 0, 0, backgroundWidth, backgroundHeight);
        drawDiamondGear(matrices);
        drawFuelBurn(matrices);
        drawPulverizeProgress(matrices);
    }

    private PulverizerScreenHandler getThisHandler() {
        return ((PulverizerScreenHandler)this.handler);
    }

    private void drawFuelBurn(MatrixStack matrices) {
        if (getThisHandler().isBurning()) {
            int fuelProgress = getThisHandler().getFuelProgress();
            int fuelTotalTime = getThisHandler().getFuelTimeTotal();
            int height = (int)(((float) fuelProgress / (float) fuelTotalTime) * 14);
            if (fuelProgress != 0) {
                height = height < 2 ? 2 : height;
            }
            this.drawTexture(matrices, x + 56, y + 37 + (14 - height), 176, 14 - height, 14, height);
        }
    }

    private void drawDiamondGear(MatrixStack matrices) {
        int grindTime = getThisHandler().getGrindTime();
        if (grindTime != 0) {
            int width = (int)(((float) grindTime / (float) PulverizerBlockEntity.GRIND_LIFETIME) * 14);
            this.drawTexture(matrices, x + 34, y + 36, 176, 30, width, 14);
        }
    }

    private void drawPulverizeProgress(MatrixStack matrices) {
        int pulverizeTime = getThisHandler().getPulverizeTime();
        int pulverizeTimeTotal = getThisHandler().getPulverizeTimeTotal();
        if (pulverizeTime != 0 && pulverizeTimeTotal != 0) {
            int width = (int)(((float) pulverizeTime / (float) pulverizeTimeTotal) * 24);
            this.drawTexture(matrices, x + 80, y + 35, 177, 14, width, 17);
        }
    }
}
