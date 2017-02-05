package uk.co.mysterymayhem.mysthighlights;


import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.io.File;
import java.nio.file.Path;
import java.util.regex.Pattern;

/**
 * Created by Mysteryem on 2016-12-08.
 */
public class ConfigHandler {

    // The values during class initialisation are the defaults
    static boolean DISABLE_VANILLA_BLOCK_HIGHLIGHT = true;
    static boolean RENDER_BLOCK_LINES = true;
    static boolean RENDER_BLOCK_OVERLAY = false;
    static boolean RENDER_BLOCK_OVERLAY_USES_COLLISION = true;
    static boolean BLOCK_COLLISION_BOXES_CLAMPED = true;
    static boolean RENDER_BLOCK_LINES_USES_COLLISION = false;
    static boolean RENDER_ENTITY_HITBOX_LINES = false;
    static boolean RENDER_ENTITY_HITBOX_OVERLAY = false;
    static boolean RENDER_ENTITY_MODEL_OVERLAY = true;
    static boolean RENDER_LIVING_MODEL_OUTLINE = true;
    static boolean RENDER_LIVING_GLOW = false;
    static float BLOCK_LINES_WIDTH = 2f;
    static float ENTITY_LINES_WIDTH = 2f;
    final static float[] BLOCK_LINES_COLOUR = new float[]{0f, 0f, 0f, 0.4f};
    final static float[] BLOCK_OVERLAY_COLOUR = new float[]{0f, 0f, 0f, 0.4f};
    final static float[] ENTITY_HITBOX_LINES_COLOUR = new float[]{0f, 0f, 0f, 0.4f};
    final static float[] ENTITY_HITBOX_OVERLAY_COLOUR = new float[]{0f, 0f, 0f, 0.4f};
    final static float[] ENTITY_MODEL_OVERLAY_COLOUR = new float[]{0f, 0f, 0f, 1f};
    final static float[] ENTITY_MODEL_OUTLINE_COLOUR = new float[]{1f, 1f, 1f, 1f};
    private static final Pattern COLOUR_VALIDATOR = Pattern.compile("(\\d+(\\.\\d+)?,+){2,3}(\\d+(\\.\\d+)?)");

    private static final String CONFIG_DIRECTORY_NAME = "myst_highlights";
    private static final String MAIN_CONFIG_NAME = "core.cfg";
    private static Path CONFIG_DIRECTORY;
    private static Configuration MAIN_CONFIG;
    static void initialLoadConfig(FMLPreInitializationEvent event) {
        CONFIG_DIRECTORY = event.getModConfigurationDirectory().toPath().resolve(CONFIG_DIRECTORY_NAME);
        MAIN_CONFIG = new Configuration(CONFIG_DIRECTORY.resolve(MAIN_CONFIG_NAME).toFile());
    }

    static void loadConfig(File file) {
        Configuration configuration = new Configuration(file);
        configuration.load();

        DISABLE_VANILLA_BLOCK_HIGHLIGHT = configuration.getBoolean(
                "blockLinesDisableVanilla", Configuration.CATEGORY_GENERAL, DISABLE_VANILLA_BLOCK_HIGHLIGHT, "Disable the vanilla block outline on the block you're looking at");
        RENDER_BLOCK_LINES = configuration.getBoolean(
                "blockLinesEnabled", Configuration.CATEGORY_GENERAL, RENDER_BLOCK_LINES, "Draw a custom outline on the block you're looking at");
        RENDER_BLOCK_OVERLAY = configuration.getBoolean(
                "blockOverlayEnabled", Configuration.CATEGORY_GENERAL, RENDER_BLOCK_OVERLAY, "Draw an overlay on the block you're looking at");
        RENDER_BLOCK_OVERLAY_USES_COLLISION = configuration.getBoolean(
                "blockOverlayUsesCollision", Configuration.CATEGORY_GENERAL, RENDER_BLOCK_OVERLAY_USES_COLLISION,
                "When drawing the overlay on a block, should the overlay be drawn over the collision boxes of the block." +
                        "\nWith this enabled, blocks like stairs will look better, but blocks like fences will look worse");
        BLOCK_COLLISION_BOXES_CLAMPED = configuration.getBoolean(
                "blockCollisionClamped", Configuration.CATEGORY_GENERAL, BLOCK_COLLISION_BOXES_CLAMPED,
                "When drawing the collision overlay on a block, should the overlay be clamped to fit within the block." +
                        "\nWith this enabled, the overlay for fences won't extend outside of a 1x1x1 block.");
        RENDER_BLOCK_LINES_USES_COLLISION = configuration.getBoolean(
                "blockLinesUsesCollision", Configuration.CATEGORY_GENERAL, RENDER_BLOCK_LINES_USES_COLLISION,
                "When drawing the outline of a block, should the outline be drawn over the collision boxes of the block." +
                        "\nI don't think outlines look very good with this option on, but it's here if you want both outlines and overlays enabled and using collision boxes");
        RENDER_ENTITY_HITBOX_LINES = configuration.getBoolean(
                "entityHitboxLinesEnabled", Configuration.CATEGORY_GENERAL, RENDER_ENTITY_HITBOX_LINES, "Draw an outline of the hitbox of the entity you're looking at");
        RENDER_ENTITY_HITBOX_OVERLAY = configuration.getBoolean(
                "entityHitboxOverlayEnabled", Configuration.CATEGORY_GENERAL, RENDER_ENTITY_HITBOX_OVERLAY, "Draw an overlay of the hitbox of the entity you're looking at");
        RENDER_ENTITY_MODEL_OVERLAY = configuration.getBoolean(
                "entityModelOverlayEnabled", Configuration.CATEGORY_GENERAL, RENDER_ENTITY_MODEL_OVERLAY, "Draw an overlay on top of the model of the entity you're looking at");
        RENDER_LIVING_MODEL_OUTLINE = configuration.getBoolean(
                "entityModelOutlineEnabled", Configuration.CATEGORY_GENERAL, RENDER_LIVING_MODEL_OUTLINE, "Draw an outline around the model of the entity you're looking at");
        RENDER_LIVING_GLOW = configuration.getBoolean(
                "entityGlowEnabled", Configuration.CATEGORY_GENERAL, RENDER_LIVING_GLOW, "Apply the 'glowing' effect to the entity you're looking at");
        BLOCK_LINES_WIDTH = configuration.getFloat(
                "blockLinesWidth", Configuration.CATEGORY_GENERAL, BLOCK_LINES_WIDTH, 0f, Float.MAX_VALUE, "Width of the outline of the block you're looking at");
        ENTITY_LINES_WIDTH = configuration.getFloat(
                "entityLinesWidth", Configuration.CATEGORY_GENERAL, ENTITY_LINES_WIDTH, 0f, Float.MAX_VALUE, "Width of the outline of the hitbox of the entity you're looking at");
        String currentConfigString = "blockLinesColour";
        String colourString = configuration.getString(currentConfigString, Configuration.CATEGORY_GENERAL,
                "0, 0, 0, 0.4", "The colour block outlines should be in RGBA format: \"red, green, blue, alpha\", each value is from 0-1, alpha can be omitted", COLOUR_VALIDATOR);
        parseColourString(colourString, BLOCK_LINES_COLOUR, currentConfigString);

        currentConfigString = "blockOverlayColour";
        colourString = configuration.getString(currentConfigString, Configuration.CATEGORY_GENERAL,
                "0, 0, 0, 0.4", "The colour block overlays should be in RGBA format: \"red, green, blue, alpha\", each value is from 0-1, alpha can be omitted", COLOUR_VALIDATOR);
        parseColourString(colourString, BLOCK_OVERLAY_COLOUR, currentConfigString);

        currentConfigString = "entityHitboxLinesColour";
        colourString = configuration.getString(currentConfigString, Configuration.CATEGORY_GENERAL,
                "0, 0, 0, 0.4", "The colour entity hitbox outlines should be in RGBA format: \"red, green, blue, alpha\", each value is from 0-1, alpha can be omitted", COLOUR_VALIDATOR);
        parseColourString(colourString, ENTITY_HITBOX_LINES_COLOUR, currentConfigString);

        currentConfigString = "entityHitboxOverlayColour";
        colourString = configuration.getString(currentConfigString, Configuration.CATEGORY_GENERAL,
                "0, 0, 0, 0.4", "The colour entity hitbox overlays should be in RGBA format: \"red, green, blue, alpha\", each value is from 0-1, alpha can be omitted", COLOUR_VALIDATOR);
        parseColourString(colourString, ENTITY_HITBOX_OVERLAY_COLOUR, currentConfigString);

        currentConfigString = "entityModelOverlayColour";
        colourString = configuration.getString(currentConfigString, Configuration.CATEGORY_GENERAL,
                "1, 1, 1", "The colour entity model overlays should be in RGB format: \"red, green, blue\", each value is from 0-1", COLOUR_VALIDATOR);
        parseColourString(colourString, ENTITY_MODEL_OVERLAY_COLOUR, currentConfigString);

        currentConfigString = "entityModelOutlineColour";
        colourString = configuration.getString(currentConfigString, Configuration.CATEGORY_GENERAL,
                "1, 1, 1", "The colour entity model outlines should be in RGB format: \"red, green, blue\", each value is from 0-1", COLOUR_VALIDATOR);
        parseColourString(colourString, ENTITY_MODEL_OUTLINE_COLOUR, currentConfigString);
        if (configuration.hasChanged()) {
            configuration.save();
        }

        Config.loadConfigFromHandler();
    }

    private static void parseColourString(String colourString, float[] arrayToStoreTo, String exceptionInfo) {
        String[] split = colourString.split(",");
        if (split.length == 4 || split.length == 3) {
            for (int i = 0; i < split.length; i++) {
                try {
                    arrayToStoreTo[i] = Math.max(0f, Math.min(1f, Float.parseFloat(split[i])));
                } catch (NumberFormatException e) {
                    arrayToStoreTo[i] = 1f;
                    FMLLog.warning("[MystHighlights] Invalid colour string \"" + colourString + "\" in \"" + exceptionInfo + "\", could not parse \"" + split[i] + "\"");
                }
            }
            if (split.length == 3) {
                arrayToStoreTo[3] = 1;
            }
        }
        else {
            FMLLog.warning("[MystHighlights] Invalid colour string \"" + colourString + "\" in \"" + exceptionInfo + "\"");
        }
    }
}
