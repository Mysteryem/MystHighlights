package uk.co.mysterymayhem.mysthighlights;

/**
 * Created by Mysteryem on 2016-12-08.
 */
public class Config {

    // BlockBoxDrawer
    public static boolean DISABLE_VANILLA_BLOCK_HIGHLIGHT;
    public static boolean RENDER_BLOCK_LINES;
    public static boolean RENDER_BLOCK_OVERLAY;
    public static boolean RENDER_BLOCK_OVERLAY_USES_COLLISION;
    public static boolean RENDER_BLOCK_LINES_USES_COLLISION;
    public static boolean BLOCK_COLLISION_BOXES_CLAMPED;
    public static float BLOCK_LINES_WIDTH;
    public static float BLOCK_LINES_RED;
    public static float BLOCK_LINES_GREEN;
    public static float BLOCK_LINES_BLUE;
    public static float BLOCK_LINES_ALPHA;
    public static float BLOCK_OVERLAY_RED;
    public static float BLOCK_OVERLAY_GREEN;
    public static float BLOCK_OVERLAY_BLUE;
    public static float BLOCK_OVERLAY_ALPHA;

    // EntityBoxDrawer
    public static boolean RENDER_ENTITY_HITBOX_LINES;
    public static boolean RENDER_ENTITY_HITBOX_OVERLAY;
    public static float ENTITY_LINES_WIDTH;
    public static float ENTITY_LINES_RED;
    public static float ENTITY_LINES_GREEN;
    public static float ENTITY_LINES_BLUE;
    public static float ENTITY_LINES_ALPHA;
    public static float ENTITY_OVERLAY_RED;
    public static float ENTITY_OVERLAY_GREEN;
    public static float ENTITY_OVERLAY_BLUE;
    public static float ENTITY_OVERLAY_ALPHA;

    // LivingColourer & NonLivingColourer
    public static boolean RENDER_ENTITY_MODEL_OVERLAY;
    public static float ENTITY_MODEL_RED;
    public static float ENTITY_MODEL_GREEN;
    public static float ENTITY_MODEL_BLUE;

    // LivingOutliner
    public static boolean RENDER_LIVING_MODEL_OUTLINE;
    public static int ENTITY_MODEL_OUTLINE_COLOUR;

    // LivingGlowHighlighter
    public static boolean RENDER_LIVING_GLOW;


    static {
        loadConfigFromHandler();
    }

    static void loadConfigFromHandler() {
        DISABLE_VANILLA_BLOCK_HIGHLIGHT = ConfigHandler.DISABLE_VANILLA_BLOCK_HIGHLIGHT;
        RENDER_BLOCK_LINES = ConfigHandler.RENDER_BLOCK_LINES;
        RENDER_BLOCK_OVERLAY = ConfigHandler.RENDER_BLOCK_OVERLAY;
        RENDER_BLOCK_OVERLAY_USES_COLLISION = ConfigHandler.RENDER_BLOCK_OVERLAY_USES_COLLISION;
        RENDER_BLOCK_LINES_USES_COLLISION = ConfigHandler.RENDER_BLOCK_LINES_USES_COLLISION;
        BLOCK_COLLISION_BOXES_CLAMPED = ConfigHandler.BLOCK_COLLISION_BOXES_CLAMPED;
        RENDER_ENTITY_HITBOX_LINES = ConfigHandler.RENDER_ENTITY_HITBOX_LINES;
        RENDER_ENTITY_HITBOX_OVERLAY = ConfigHandler.RENDER_ENTITY_HITBOX_OVERLAY;
        RENDER_ENTITY_MODEL_OVERLAY = ConfigHandler.RENDER_ENTITY_MODEL_OVERLAY;
        RENDER_LIVING_MODEL_OUTLINE = ConfigHandler.RENDER_LIVING_MODEL_OUTLINE;
        RENDER_LIVING_GLOW = ConfigHandler.RENDER_LIVING_GLOW;
        BLOCK_LINES_WIDTH = ConfigHandler.BLOCK_LINES_WIDTH;
        ENTITY_LINES_WIDTH = ConfigHandler.ENTITY_LINES_WIDTH;
        BLOCK_LINES_RED = ConfigHandler.BLOCK_LINES_COLOUR[0];
        BLOCK_LINES_GREEN = ConfigHandler.BLOCK_LINES_COLOUR[1];
        BLOCK_LINES_BLUE = ConfigHandler.BLOCK_LINES_COLOUR[2];
        BLOCK_LINES_ALPHA = ConfigHandler.BLOCK_LINES_COLOUR[3];
        BLOCK_OVERLAY_RED = ConfigHandler.BLOCK_OVERLAY_COLOUR[0];
        BLOCK_OVERLAY_GREEN = ConfigHandler.BLOCK_OVERLAY_COLOUR[1];
        BLOCK_OVERLAY_BLUE = ConfigHandler.BLOCK_OVERLAY_COLOUR[2];
        BLOCK_OVERLAY_ALPHA = ConfigHandler.BLOCK_OVERLAY_COLOUR[3];
        ENTITY_LINES_RED = ConfigHandler.ENTITY_HITBOX_LINES_COLOUR[0];
        ENTITY_LINES_GREEN = ConfigHandler.ENTITY_HITBOX_LINES_COLOUR[1];
        ENTITY_LINES_BLUE = ConfigHandler.ENTITY_HITBOX_LINES_COLOUR[2];
        ENTITY_LINES_ALPHA = ConfigHandler.ENTITY_HITBOX_LINES_COLOUR[3];
        ENTITY_OVERLAY_RED = ConfigHandler.ENTITY_HITBOX_OVERLAY_COLOUR[0];
        ENTITY_OVERLAY_GREEN = ConfigHandler.ENTITY_HITBOX_OVERLAY_COLOUR[1];
        ENTITY_OVERLAY_BLUE = ConfigHandler.ENTITY_HITBOX_OVERLAY_COLOUR[2];
        ENTITY_OVERLAY_ALPHA = ConfigHandler.ENTITY_HITBOX_OVERLAY_COLOUR[3];
        ENTITY_MODEL_RED = ConfigHandler.ENTITY_MODEL_OVERLAY_COLOUR[0];
        ENTITY_MODEL_GREEN = ConfigHandler.ENTITY_MODEL_OVERLAY_COLOUR[1];
        ENTITY_MODEL_BLUE = ConfigHandler.ENTITY_MODEL_OVERLAY_COLOUR[2];
        float[] modelOutlineColour = ConfigHandler.ENTITY_MODEL_OUTLINE_COLOUR;
        //RGBA -> ARGB, [0,1,2,3] -> [3,1,2,3] --> 0x[A][R][G][B]
        ENTITY_MODEL_OUTLINE_COLOUR = (int) (modelOutlineColour[3] * 0xFF) << 24
                | (int) (modelOutlineColour[0] * 0xFF) << 16
                | (int) (modelOutlineColour[1] * 0xFF) << 8
                | (int) (modelOutlineColour[2] * 0xFF);
    }
}
