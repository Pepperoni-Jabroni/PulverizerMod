package pepjebs.pulverizermod.config;

import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;
import me.sargunvohra.mcmods.autoconfig1u.shadowed.blue.endless.jankson.Comment;
import pepjebs.pulverizermod.PulverizerMod;

@Config(name = PulverizerMod.MODID)
public class PulverizerModConfig implements ConfigData {
    @Comment("The number of items a Diamond will pulverize before the Pulverizer consumes another.")
    public int pulverizerDiamondDurability = 512;

    @Comment("If 'true', recipes which provide bonus output will be enabled (E.g. bone -> bone meal, blaze rod -> blaze powder)")
    public boolean enableBonusRecipes = true;

    @Comment("If 'true', recipes which erode blocks will be enabled (E.g. stone -> gravel, gravel -> sand)")
    public boolean enableErosionRecipes = true;

    @Comment("If 'true', recipes which pulverize blocks into their respective dye will be enabled (E.g. blue wool -> blue dye)")
    public boolean enableDyeRecipes = true;

    @Comment("If 'true', recipes which enable you to recycle gold/iron tools/armors into an ingot will be enabled")
    public boolean enableRecycleRecipes = true;

    @Comment("If 'true', recipes which pulverize mined Ores into their materials will be enabled (E.g. Iron Ore -> Iron Ingots)")
    public boolean enableOreRecipes = true;
}
