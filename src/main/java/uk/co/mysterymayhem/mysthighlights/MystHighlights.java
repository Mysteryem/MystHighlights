package uk.co.mysterymayhem.mysthighlights;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.io.File;

/**
 * Created by Mysteryem on 2016-12-08.
 */
@Mod(
        modid = MystHighlights.MODID,
        version = MystHighlights.VERSION,
        acceptableRemoteVersions = "*",
        acceptableSaveVersions = "*",
        clientSideOnly = true,
        name = MystHighlights.NAME,
        acceptedMinecraftVersions = "[1.10.2, 1.12)")
public class MystHighlights {
    public static final String MODID = "mysthighlights";
    public static final String VERSION = "1.0";
    public static final String NAME = "Myst Highlights";

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        File suggestedConfigurationFile = event.getSuggestedConfigurationFile();
        ConfigHandler.loadConfig(suggestedConfigurationFile);
        if (ConfigHandler.RENDER_ENTITY_MODEL_OVERLAY) {
            MinecraftForge.EVENT_BUS.register(Highlights.LivingColourer.class);
            MinecraftForge.EVENT_BUS.register(Highlights.NonLivingColourer.class);
        }
        if (StaticConfig.RENDER_BLOCK_LINES
                || StaticConfig.RENDER_BLOCK_OVERLAY
                || StaticConfig.DISABLE_VANILLA_BLOCK_HIGHLIGHT
                || StaticConfig.RENDER_ENTITY_HITBOX_LINES
                || StaticConfig.RENDER_ENTITY_HITBOX_OVERLAY) {
            MinecraftForge.EVENT_BUS.register(Highlights.BlockAndEntityBoxDrawer.class);
        }
    }
}
