package uk.co.mysterymayhem.mysthighlights.config;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import uk.co.mysterymayhem.mysthighlights.MystHighlights;
import uk.co.mysterymayhem.mysthighlights.highlighters.*;

import java.util.*;

/**
 * Created by Mysteryem on 2017-02-07.
 */
public class Config {
    public static final String CATEGORY_BLOCK_OUTLINE = "category_block_outline";
    public static boolean blockOutline_enabled = false;
    public static boolean blockOutline_disableVanilla = false;
    public static boolean blockOutline_usesCollision = false;
    public static float blockOutline_lineWidth = 2f;
    public static float blockOutline_red = 0;
    public static float blockOutline_green = 0;
    public static float blockOutline_blue = 0;
    public static float blockOutline_alpha = 0.4f;

    public static final String CATEGORY_BLOCK_OVERLAY = "category_block_overlay";
    public static boolean blockOverlay_enabled = false;
    public static boolean blockOverlay_usesCollision = true;
    public static float blockOverlay_red = 0;
    public static float blockOverlay_green = 0;
    public static float blockOverlay_blue = 0;
    public static float blockOverlay_alpha = 0.4f;

    public static final String CATEGORY_BLOCK_COMMON = "category_block_common";
    public static boolean blockCommon_clampCollision = true;

    public static final String CATEGORY_ENTITY_OUTLINE_MODEL_VANILLAGLOW = "category_entity_outline_model_glow";
    public static boolean entityOutlineModelGlow_enabled = false;
    private static float entityOutlineModelGlow_red = 1;
    private static float entityOutlineModelGlow_green = 1;
    private static float entityOutlineModelGlow_blue = 1;
    public static int entityOutlineModelGlow_colour = 0xffffff;


    public static final String CATEGORY_ENTITY_OUTLINE_MODEL_CUSTOM = "category_entity_outline_model_custom";
    public static boolean entityOutlineModelCustom_enabled = false;
    private static float entityOutlineModelCustom_red = 1;
    private static float entityOutlineModelCustom_green = 1;
    private static float entityOutlineModelCustom_blue = 1;
    //alpha currently not working and likely won't be able to work due to things like armour messing up alpha values
    // Not in config (calculated using the above three values)
//    //RGBA -> ARGB, [0,1,2,3] -> [3,1,2,3] --> 0x[A][R][G][B]
//    ENTITY_MODEL_OUTLINE_COLOUR = (int) (modelOutlineColour[3] * 0xFF) << 24
//            | (int) (modelOutlineColour[0] * 0xFF) << 16
//            | (int) (modelOutlineColour[1] * 0xFF) << 8
//            | (int) (modelOutlineColour[2] * 0xFF);
    public static int entityOutlineModelCustom_colour = 0xffffff;

    public static final String CATEGORY_ENTITY_OUTLINE_HITBOX = "category_entity_outline_hitbox";
    public static boolean entityOutlineHitbox_enabled = false;
    public static float entityOutlineHitbox_lineWidth = 2f;
    public static float entityOutlineHitbox_red = 0;
    public static float entityOutlineHitbox_green = 0;
    public static float entityOutlineHitbox_blue = 0;
    public static float entityOutlineHitbox_alpha = 0.4f;

    public static final String CATEGORY_ENTITY_OVERLAY_MODEL = "category_entity_overlay_model";
    public static boolean entityOverlayModel_enabled = false;
    public static float entityOverlayModel_red = 1;
    public static float entityOverlayModel_green = 1;
    public static float entityOverlayModel_blue = 1;

    public static final String CATEGORY_ENTITY_OVERLAY_HITBOX = "category_entity_overlay_hitbox";
    public static boolean entityOverlayHitbox_enabled = false;
    public static float entityOverlayHitbox_red = 0;
    public static float entityOverlayHitbox_green = 0;
    public static float entityOverlayHitbox_blue = 0;
    public static float entityOverlayHitbox_alpha = 0.4f;



    public static Configuration config;

    public static void initialConfigLoad(FMLPreInitializationEvent event) {
        config = new Configuration(event.getSuggestedConfigurationFile());
        syncConfig(true);
    }

    private static List<String> propertyOrder;
    private static String category;
    private static Property prop;
    private static Map<String, Set<String>> configNameToPropertyKeySet;

    public static void syncConfig(boolean load) {
        propertyOrder = new ArrayList<>();
        configNameToPropertyKeySet = new HashMap<>();

        if (load) {
            config.load();
            FMLLog.info("Loading MystHighlights config");
        }
        else {
            FMLLog.info("Reloading MystHighlights config");
        }

        category = CATEGORY_BLOCK_OUTLINE;

        prop = config.get(category, "enabled", false);
        prop.setComment("Set to true to enable custom block outlines for the block you're looking at. It is strongly suggested that you disable the vanilla " +
                "outline.");
        process();
        blockOutline_enabled = prop.getBoolean(blockOutline_enabled);

        prop = config.get(category, "disableVanilla", false);
        prop.setComment("Set to true to disable the vanilla block outlines for the block you're looking at. If custom block outlines are enabled, it is " +
                "strongly suggested that you also disable the vanilla outlines");
        process();
        blockOutline_disableVanilla = prop.getBoolean(blockOutline_disableVanilla);

        prop = config.get(category, "red", 0d, "Red color amount.", 0d, 1d);
        process();
        blockOutline_red = getFloat(blockOutline_red);

        prop = config.get(category, "green", 0d, "Green color amount.", 0d, 1d);
        process();
        blockOutline_green = getFloat(blockOutline_green);

        prop = config.get(category, "blue", 0d, "Blue color amount.", 0d, 1d);
        process();
        blockOutline_blue = getFloat(blockOutline_blue);

        prop = config.get(category, "alpha", 0.4d, "Alpha (opaqueness) amount.", 0d, 1d);
        process();
        blockOutline_alpha = getFloat(blockOutline_alpha);

        prop = config.get(category, "lineWidth", 2d, "Thickness of drawn lines. '2' is the vanilla thickness. You probably won't want to change this much. " +
                        "Doesn't look very good with large values.",
                0d, 10d);
        process();
        blockOutline_lineWidth = getFloat(blockOutline_lineWidth);

        prop = config.get(category, "usesCollision", false);
        prop.setComment("Set to true to render lines around each of the block's collision boxes instead of just the 'main' box for the block in question " +
                "(vanilla behaviour). Stairs are for example made up of two separate boxes. It is strongly suggested that you enable the option to clamp " +
                "collision boxes if you enable this otherwise fence outlines will be the full 1.5 blocks tall. That option can be found in the 'common' " +
                "section.");
        process();
        blockOutline_usesCollision = prop.getBoolean(blockOutline_usesCollision);

        config.setCategoryPropertyOrder(category, propertyOrder);
        propertyOrder = new ArrayList<>();


        category = CATEGORY_BLOCK_OVERLAY;

        prop = config.get(category, "enabled", false);
        prop.setComment("Set to true to enable drawing a filled colour overlay over the top of the block you're currently looking at.");
        process();
        blockOverlay_enabled = prop.getBoolean(blockOverlay_enabled);

        prop = config.get(category, "red", 0d, "Red color amount.", 0d, 1d);
        process();
        blockOverlay_red = getFloat(blockOverlay_red);

        prop = config.get(category, "green", 0d, "Green color amount.", 0d, 1d);
        process();
        blockOverlay_green = getFloat(blockOverlay_green);

        prop = config.get(category, "blue", 0d, "Blue color amount.", 0d, 1d);
        process();
        blockOverlay_blue = getFloat(blockOverlay_blue);

        prop = config.get(category, "alpha", 0.4d, "Alpha (opaqueness) amount.", 0d, 1d);
        process();
        blockOverlay_alpha = getFloat(blockOverlay_alpha);

        prop = config.get(category, "usesCollision", true);
        prop.setComment("Set to true to render separate overlays for each of the block's collision boxes instead of just the 'main' box for the block " +
                "in question. Stairs are for example made up of two separate boxes. It is strongly suggested that you enable the option to clamp " +
                "collision boxes if you enable this otherwise fence overlays will be the full 1.5 blocks tall. That option can be found in the 'common' " +
                "section.");
        process();
        blockOverlay_usesCollision = prop.getBoolean(blockOverlay_usesCollision);

        config.setCategoryPropertyOrder(category, propertyOrder);
        propertyOrder = new ArrayList<>();


        category = CATEGORY_BLOCK_COMMON;

        prop = config.get(category, "clampCollision", true);
        prop.setComment("Set to true to ensure that block outline/overlay rendering always stays within a 1x1x1 block when Use Collision Boxes is enabled.");
        process();
        blockCommon_clampCollision = prop.getBoolean(blockCommon_clampCollision);

        config.setCategoryPropertyOrder(category, propertyOrder);
        propertyOrder = new ArrayList<>();


        category = CATEGORY_ENTITY_OUTLINE_MODEL_VANILLAGLOW;

        prop = config.get(category, "enabled", false);
        prop.setComment("Set to true to enable the vanilla 'glowing' effect on the entity you're currently looking at.");
        process();
        entityOutlineModelGlow_enabled = prop.getBoolean(entityOutlineModelGlow_enabled);

        prop = config.get(category, "red", 1d, "Red color amount.", 0d, 1d);
        process();
        entityOutlineModelGlow_red = getFloat(entityOutlineModelGlow_red);

        prop = config.get(category, "green", 1d, "Green color amount.", 0d, 1d);
        process();
        entityOutlineModelGlow_green = getFloat(entityOutlineModelGlow_green);

        prop = config.get(category, "blue", 1d, "Blue color amount.", 0d, 1d);
        process();
        entityOutlineModelGlow_blue = getFloat(entityOutlineModelGlow_blue);

        // Calculate the int value (so we can pass this directly to opengl code instead of working it out every time its needed
        // Alpha currently doesn't work and I don't think will ever work due to issues with armour and other layers that set their own alpha settings
        //0x[A][R][G][B]
        entityOutlineModelGlow_colour =
                (int) (entityOutlineModelGlow_red * 0xFF) << 16 //    Red
                | (int) (entityOutlineModelGlow_green * 0xFF) << 8 // Green
                | (int) (entityOutlineModelGlow_blue * 0xFF); //      Blue

        config.setCategoryPropertyOrder(category, propertyOrder);
        propertyOrder = new ArrayList<>();


        category = CATEGORY_ENTITY_OUTLINE_MODEL_CUSTOM;

        prop = config.get(category, "enabled", false);
        prop.setComment("Set to true to enable a custom outline around the entity you're currently looking at.");
        process();
        entityOutlineModelCustom_enabled = prop.getBoolean(entityOutlineModelCustom_enabled);

        prop = config.get(category, "red", 1d, "Red color amount.", 0d, 1d);
        process();
        entityOutlineModelCustom_red = getFloat(entityOutlineModelCustom_red);

        prop = config.get(category, "green", 1d, "Green color amount.", 0d, 1d);
        process();
        entityOutlineModelCustom_green = getFloat(entityOutlineModelCustom_green);

        prop = config.get(category, "blue", 1d, "Blue color amount.", 0d, 1d);
        process();
        entityOutlineModelCustom_blue = getFloat(entityOutlineModelCustom_blue);

        // Calculate the int value (so we can pass this directly to opengl code instead of working it out every time its needed
        // Alpha currently doesn't work and I don't think will ever work due to issues with armour and other layers that set their own alpha settings
        //0x[A][R][G][B]
        entityOutlineModelCustom_colour =
                (int) (entityOutlineModelCustom_red * 0xFF) << 16 //    Red
                | (int) (entityOutlineModelCustom_green * 0xFF) << 8 // Green
                | (int) (entityOutlineModelCustom_blue * 0xFF); //      Blue

        config.setCategoryPropertyOrder(category, propertyOrder);
        propertyOrder = new ArrayList<>();


        category = CATEGORY_ENTITY_OUTLINE_HITBOX;

        prop = config.get(category, "enabled", false);
        prop.setComment("Set to true to enable an outline/wireframe of the hitbox of the entity you're currently looking at.");
        process();
        entityOutlineHitbox_enabled = prop.getBoolean(entityOutlineHitbox_enabled);

        prop = config.get(category, "red", 0d, "Red color amount.", 0d, 1d);
        process();
        entityOutlineHitbox_red = getFloat(entityOutlineHitbox_red);

        prop = config.get(category, "green", 0d, "Green color amount.", 0d, 1d);
        process();
        entityOutlineHitbox_green = getFloat(entityOutlineHitbox_green);

        prop = config.get(category, "blue", 0d, "Blue color amount.", 0d, 1d);
        process();
        entityOutlineHitbox_blue = getFloat(entityOutlineHitbox_blue);

        prop = config.get(category, "alpha", 0.4d, "Alpha (opaqueness) amount.", 0d, 1d);
        process();
        entityOutlineHitbox_alpha = getFloat(entityOutlineHitbox_alpha);

        prop = config.get(category, "lineWidth", 2d, "Thickness of drawn lines. '2' is the vanilla thickness of block outlines. You probably won't want to " +
                "change this much. Doesn't look very good with large values.", 0d, 10d);
        process();
        entityOutlineHitbox_lineWidth = getFloat(entityOutlineHitbox_lineWidth);

        config.setCategoryPropertyOrder(category, propertyOrder);
        propertyOrder = new ArrayList<>();


        category = CATEGORY_ENTITY_OVERLAY_MODEL;

        prop = config.get(category, "enabled", false);
        prop.setComment("Set to true to enable a colored overlay over the top of the model of the entity you're currently looking at.");
        process();
        entityOverlayModel_enabled = prop.getBoolean(entityOverlayModel_enabled);

        prop = config.get(category, "red", 1d, "Red color amount.", 0d, 1d);
        process();
        entityOverlayModel_red = getFloat(entityOverlayModel_red);

        prop = config.get(category, "green", 1d, "Green color amount.", 0d, 1d);
        process();
        entityOverlayModel_green = getFloat(entityOverlayModel_green);

        prop = config.get(category, "blue", 1d, "Blue color amount.", 0d, 1d);
        process();
        entityOverlayModel_blue = getFloat(entityOverlayModel_blue);

        config.setCategoryPropertyOrder(category, propertyOrder);
        propertyOrder = new ArrayList<>();


        category = CATEGORY_ENTITY_OVERLAY_HITBOX;

        prop = config.get(category, "enabled", false);
        prop.setComment("Set to true to enable a colored overlay of the hitbox of the entity you're currently looking at.");
        process();
        entityOverlayHitbox_enabled = prop.getBoolean(entityOverlayHitbox_enabled);

        prop = config.get(category, "red", 0d, "Red color amount.", 0d, 1d);
        process();
        entityOverlayHitbox_red = getFloat(entityOverlayHitbox_red);

        prop = config.get(category, "green", 0d, "Green color amount.", 0d, 1d);
        process();
        entityOverlayHitbox_green = getFloat(entityOverlayHitbox_green);

        prop = config.get(category, "blue", 0d, "Blue color amount.", 0d, 1d);
        process();
        entityOverlayHitbox_blue = getFloat(entityOverlayHitbox_blue);

        prop = config.get(category, "alpha", 0.4d, "Alpha (opaqueness) amount.", 0d, 1d);
        process();
        entityOverlayHitbox_alpha = getFloat(entityOverlayHitbox_alpha);

        config.setCategoryPropertyOrder(category, propertyOrder);
        //propertyOrder = new ArrayList<>();

        for (String categoryName : config.getCategoryNames()) {
            ConfigCategory category = config.getCategory(categoryName);
            Set<String> knownKeys = configNameToPropertyKeySet.get(categoryName);
            if (knownKeys == null) {
                config.removeCategory(category);
                FMLLog.info("Removed unknown config category %s from MystHighlights config", categoryName);
            }
            else {
//                Set<ConfigCategory> children = category.getChildren();
                for (Iterator<String> iterator = category.keySet().iterator(); iterator.hasNext(); /**/) {
                    String propertyKey = iterator.next();
                    if (!knownKeys.contains(propertyKey)) {
                        iterator.remove();
                        FMLLog.info("Removed unknown property %s from MystHighlights config", propertyKey);
                    }
                }
            }
        }

        if (config.hasChanged()) {
            config.save();
        }

        reloadEventListeners();

        // Small bit of tidying up
        configNameToPropertyKeySet = null;
        propertyOrder = null;
        category = null;
        prop = null;
    }

    @SubscribeEvent
    public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(MystHighlights.MODID)) {
            syncConfig(false);
        }
    }

    private static void reloadEventListeners() {
        if (entityOverlayModel_enabled) {
            MinecraftForge.EVENT_BUS.register(LivingColourer.class);
            MinecraftForge.EVENT_BUS.register(NonLivingColourer.class);
        }
        else {
            MinecraftForge.EVENT_BUS.unregister(LivingColourer.class);
            MinecraftForge.EVENT_BUS.unregister(NonLivingColourer.class);
        }
        if (entityOutlineModelCustom_enabled) {
            MinecraftForge.EVENT_BUS.register(EntityCustomOutliner.class);
        }
        else {
            MinecraftForge.EVENT_BUS.unregister(EntityCustomOutliner.class);
        }
        if (entityOutlineModelGlow_enabled) {
            MinecraftForge.EVENT_BUS.register(EntityGlowOutliner.class);
        }
        else {
            MinecraftForge.EVENT_BUS.unregister(EntityGlowOutliner.class);
        }
        if (entityOutlineHitbox_enabled || entityOverlayHitbox_enabled) {
            MinecraftForge.EVENT_BUS.register(EntityBoxDrawer.class);
        }
        else {
            MinecraftForge.EVENT_BUS.unregister(EntityBoxDrawer.class);
        }
        if (blockOutline_disableVanilla
                || blockOverlay_enabled
                || blockOutline_enabled) {
            MinecraftForge.EVENT_BUS.register(BlockBoxDrawer.class);
        }
        else {
            MinecraftForge.EVENT_BUS.unregister(BlockBoxDrawer.class);
        }
    }

    private static float getFloat(float defaultValue) {
        return (float)prop.getDouble(defaultValue);
    }

    private static void process() {
        process(prop);
    }

    private static void process(Property prop) {
        setLangKey(prop);
        order(prop);
        Set<String> propKeys = configNameToPropertyKeySet.get(category);
        if (propKeys == null) {
            propKeys = new HashSet<>();
            configNameToPropertyKeySet.put(category, propKeys);
        }
        propKeys.add(prop.getName());
    }

    private static void order(Property prop) {
        propertyOrder.add(prop.getName());
    }

    private static void setLangKey(Property prop) {
        setLangKey(prop, category);
    }

    private static void setLangKey(Property prop, String category) {
        StringBuilder builder = new StringBuilder();
        builder.append(MystHighlights.MODID).append(".config.");
        // Removes "category_" from the front and then replaces '_' with '.'
        category = category.substring(category.indexOf('_') + 1).replace('_', '.');
        builder.append(category);

        String propName = prop.getName();
//        propName = propName.substring(propName.lastIndexOf('_') + 1);
        builder.append('.').append(propName);

        prop.setLanguageKey(builder.toString());
    }
}
