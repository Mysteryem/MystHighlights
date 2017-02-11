package uk.co.mysterymayhem.mysthighlights.config;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.DummyConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;
import uk.co.mysterymayhem.mysthighlights.MystHighlights;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mysteryem on 2017-02-07.
 */
public class HighlightsGuiScreen extends GuiConfig {
    public HighlightsGuiScreen(GuiScreen parent) {
        super(parent, getConfigElements(), MystHighlights.MODID, false, false, I18n.format("mysthighlights.config.title"));

    }

    private static List<IConfigElement> getConfigElements() {

        ArrayList<IConfigElement> highestLevel = new ArrayList<>();
        ArrayList<IConfigElement> blockMenu = new ArrayList<>();
        ArrayList<IConfigElement> entityMenu = new ArrayList<>();

        highestLevel.add(new DummyConfigElement.DummyCategoryElement("block", "mysthighlights.config.block", blockMenu));
        highestLevel.add(new DummyConfigElement.DummyCategoryElement("entity", "mysthighlights.config.entity", entityMenu));

        blockMenu.add(getCategory("overlay", "mysthighlights.config.block.overlay", Config.CATEGORY_BLOCK_OVERLAY));
        blockMenu.add(getCategory("outline", "mysthighlights.config.block.outline", Config.CATEGORY_BLOCK_OUTLINE));
        blockMenu.add(getCategory("common", "mysthighlights.config.block.common", Config.CATEGORY_BLOCK_COMMON));

        entityMenu.add(getCategory("model.glow", "mysthighlights.config.entity.outline.model.glow", Config.CATEGORY_ENTITY_OUTLINE_MODEL_VANILLAGLOW));
        entityMenu.add(getCategory("model.custom", "mysthighlights.config.entity.outline.model.custom", Config.CATEGORY_ENTITY_OUTLINE_MODEL_CUSTOM));
        entityMenu.add(getCategory("hitbox", "mysthighlights.config.entity.outline.hitbox", Config.CATEGORY_ENTITY_OUTLINE_HITBOX));

        entityMenu.add(getCategory("model", "mysthighlights.config.entity.overlay.model", Config.CATEGORY_ENTITY_OVERLAY_MODEL));
        entityMenu.add(getCategory("hitbox", "mysthighlights.config.entity.overlay.hitbox", Config.CATEGORY_ENTITY_OVERLAY_HITBOX));

        return highestLevel;
    }

    private static IConfigElement getCategory(String name, String langKey, String categoryName) {
        return new DummyConfigElement.DummyCategoryElement(name, langKey, getElementsForCategory(categoryName));
    }

    private static List<IConfigElement> getElementsForCategory(String categoryName) {
        return new ConfigElement(Config.config.getCategory(categoryName)).getChildElements();
    }
}
