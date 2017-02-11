package uk.co.mysterymayhem.mysthighlights.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ResourceLocation;

/**
 * This class is extended to work with a hacky workaround that allows choosing the precise colour of the glowing outlines of an entity, instead of being limited
 * to each of the available colour codes.
 * Created by Mysteryem on 10/02/2017.
 */
public abstract class ConfigColourFontRenderer extends FontRenderer {
    public ConfigColourFontRenderer() {
        super(Minecraft.getMinecraft().gameSettings, new ResourceLocation("textures/font/ascii.png"), Minecraft.getMinecraft().renderEngine, false);
    }

    @Override
    public int getColorCode(char character) {
        return this.getColourFromConfig();
    }

    //8 bits per component
    //0x[A][R][G][B], e.g. 0xFF22BB66 -> 0x[FF][22][BB][66]
    //Note that alpha is usually ignored
    public abstract int getColourFromConfig();
}
