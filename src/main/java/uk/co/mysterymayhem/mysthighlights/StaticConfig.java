package uk.co.mysterymayhem.mysthighlights;

/**
 * Created by Mysteryem on 2016-12-08.
 */
public class StaticConfig {

    public static final boolean DISABLE_VANILLA_BLOCK_HIGHLIGHT;
    public static final boolean RENDER_BLOCK_LINES;
    public static final boolean RENDER_BLOCK_OVERLAY;
    public static final boolean RENDER_BLOCK_OVERLAY_USES_COLLISION;
    public static final boolean RENDER_BLOCK_LINES_USES_COLLISION;
    public static final boolean DRAW_BLOCK_LINES_AFTER_OVERLAY;
    public static final boolean BLOCK_COLLISION_BOXES_CLAMPED;
    public static final boolean DRAW_ENTITY_LINES_AFTER_OVERLAY;
    public static final boolean RENDER_ENTITY_HITBOX_LINES;
    public static final boolean RENDER_ENTITY_HITBOX_OVERLAY;
    public static final boolean RENDER_ENTITY_MODEL_OVERLAY;
    public static final float BLOCK_LINES_WIDTH;
    public static final float ENTITY_LINES_WIDTH;
    public static final float BLOCK_LINES_RED;
    public static final float BLOCK_LINES_GREEN;
    public static final float BLOCK_LINES_BLUE;
    public static final float BLOCK_LINES_ALPHA;
    public static final float BLOCK_OVERLAY_RED;
    public static final float BLOCK_OVERLAY_GREEN;
    public static final float BLOCK_OVERLAY_BLUE;
    public static final float BLOCK_OVERLAY_ALPHA;
    public static final float ENTITY_LINES_RED;
    public static final float ENTITY_LINES_GREEN;
    public static final float ENTITY_LINES_BLUE;
    public static final float ENTITY_LINES_ALPHA;
    public static final float ENTITY_OVERLAY_RED;
    public static final float ENTITY_OVERLAY_GREEN;
    public static final float ENTITY_OVERLAY_BLUE;
    public static final float ENTITY_OVERLAY_ALPHA;
    public static final float ENTITY_MODEL_RED;
    public static final float ENTITY_MODEL_GREEN;
    public static final float ENTITY_MODEL_BLUE;


    static {
        if (ConfigHandler.CONFIG_SETUP_ALLOWED) {
            DISABLE_VANILLA_BLOCK_HIGHLIGHT = ConfigHandler.DISABLE_VANILLA_BLOCK_HIGHLIGHT;
            RENDER_BLOCK_LINES = ConfigHandler.RENDER_BLOCK_LINES;
            RENDER_BLOCK_OVERLAY = ConfigHandler.RENDER_BLOCK_OVERLAY;
            RENDER_BLOCK_OVERLAY_USES_COLLISION = ConfigHandler.RENDER_BLOCK_OVERLAY_USES_COLLISION;
            RENDER_BLOCK_LINES_USES_COLLISION = ConfigHandler.RENDER_BLOCK_LINES_USES_COLLISION;
            BLOCK_COLLISION_BOXES_CLAMPED = ConfigHandler.BLOCK_COLLISION_BOXES_CLAMPED;
            DRAW_BLOCK_LINES_AFTER_OVERLAY = ConfigHandler.DRAW_BLOCK_LINES_AFTER_OVERLAY;
            DRAW_ENTITY_LINES_AFTER_OVERLAY = ConfigHandler.DRAW_ENTITY_LINES_AFTER_OVERLAY;
            RENDER_ENTITY_HITBOX_LINES = ConfigHandler.RENDER_ENTITY_HITBOX_LINES;
            RENDER_ENTITY_HITBOX_OVERLAY = ConfigHandler.RENDER_ENTITY_HITBOX_OVERLAY;
            RENDER_ENTITY_MODEL_OVERLAY = ConfigHandler.RENDER_ENTITY_MODEL_OVERLAY;
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
        }
        else {
            throw new RuntimeException("Accessing static config before config has been set up!");
        }
    }
}
