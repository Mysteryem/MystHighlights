package uk.co.mysterymayhem.mysthighlights.highlighters;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import uk.co.mysterymayhem.mysthighlights.config.Config;

/**
 * Created by Mysteryem on 2017-01-16.
 */
public class EntityBoxDrawer {
    @SubscribeEvent
    public static void onRenderWorldLast(RenderWorldLastEvent event) {
        Minecraft minecraft = Minecraft.getMinecraft();
        // objectMouseOver shouldn't be null during a RenderWorldLastEvent, since its value is set earlier in the client tick
        // It might be possible for mods to screw with this so maybe it will become an issue
        RayTraceResult target = minecraft.objectMouseOver;
        if (target.typeOfHit == RayTraceResult.Type.ENTITY) {

            // Interpolate player and entity positions
            net.minecraft.entity.Entity entityHit = target.entityHit;
            EntityPlayer player = minecraft.player;
            float partialTicks = event.getPartialTicks();

            double playerXInterp = player.lastTickPosX + (player.posX - player.lastTickPosX) * (double)partialTicks;
            double playerYInterp = player.lastTickPosY + (player.posY - player.lastTickPosY) * (double)partialTicks;
            double playerZInterp = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * (double)partialTicks;

            double entityXInterp = entityHit.lastTickPosX + (entityHit.posX - entityHit.lastTickPosX) * (double)partialTicks;
            double entityYInterp = entityHit.lastTickPosY + (entityHit.posY - entityHit.lastTickPosY) * (double)partialTicks;
            double entityZInterp = entityHit.lastTickPosZ + (entityHit.posZ - entityHit.lastTickPosZ) * (double)partialTicks;

            double entityXDiff = entityHit.posX - entityXInterp;
            double entityYDiff = entityHit.posY - entityYInterp;
            double entityZDiff = entityHit.posZ - entityZInterp;

            // Vanilla GL setup
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.glLineWidth(Config.entityOutlineHitbox_lineWidth);
            GlStateManager.disableTexture2D();
            GlStateManager.depthMask(false);

            // Not needed for some reason...
//            GlStateManager.alphaFunc(GL11.GL_ALWAYS, 0);

            // Draw boxes/lines/both
            if (Config.entityOverlayHitbox_enabled) {
                RenderGlobal.renderFilledBox(
                        entityHit.getRenderBoundingBox()
                                .expandXyz(0.005)
                                .offset(-playerXInterp - entityXDiff, -playerYInterp - entityYDiff, -playerZInterp - entityZDiff),
                        Config.entityOverlayHitbox_red, Config.entityOverlayHitbox_green, Config.entityOverlayHitbox_blue, Config.entityOverlayHitbox_alpha);
            }
            if (Config.entityOutlineHitbox_enabled) {
                RenderGlobal.drawSelectionBoundingBox(
                        entityHit.getRenderBoundingBox()
                                .expandXyz(0.005)
                                .offset(-playerXInterp - entityXDiff, -playerYInterp - entityYDiff, -playerZInterp - entityZDiff),
                        Config.entityOutlineHitbox_red, Config.entityOutlineHitbox_green, Config.entityOutlineHitbox_blue, Config.entityOutlineHitbox_alpha);
            }

            // See above use of alphaFunc
//            GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1f);

            // Vanilla GL cleanup
            GlStateManager.depthMask(true);
            GlStateManager.enableTexture2D();
            GlStateManager.disableBlend();
        }
    }
}
