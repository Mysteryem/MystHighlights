package uk.co.mysterymayhem.mysthighlights;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import uk.co.mysterymayhem.mysthighlights.config.Config;

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
        acceptedMinecraftVersions = "[1.12, 1.13)",
        guiFactory = MystHighlights.CONFIG_GUI_FACTORY
)
public class MystHighlights {
    public static final String MODID = "mysthighlights";
    public static final String VERSION = "1.2";
    public static final String NAME = "Myst Highlights";
    public static final String CONFIG_GUI_FACTORY = "uk.co.mysterymayhem.mysthighlights.config.HighlightsGuiFactory";

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(Config.class);
        Config.initialConfigLoad(event);
    }
}
