package uk.co.mysterymayhem.mysthighlights.highlighters;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import uk.co.mysterymayhem.mysthighlights.Config;

/**
 * Created by Mysteryem on 2017-01-16.
 */
public class EntityBoxDrawer {
    @SubscribeEvent
    public static void onBlockHighlight(DrawBlockHighlightEvent event) {
        // Vanilla seems to check for this, not sure what it's used for, doesn't seem to have a use outside of
        // highlighting the block you're currently looking at
        if (event.getSubID() != 0) {
            return;
        }

        RayTraceResult target = event.getTarget();
        if (target.typeOfHit == RayTraceResult.Type.ENTITY) {

            // Interpolate player and entity positions
            net.minecraft.entity.Entity entityHit = target.entityHit;
            EntityPlayer player = event.getPlayer();
            float partialTicks = event.getPartialTicks();

            double playerXInterp = player.lastTickPosX + (player.posX - player.lastTickPosX) * (double) partialTicks;
            double playerYInterp = player.lastTickPosY + (player.posY - player.lastTickPosY) * (double) partialTicks;
            double playerZInterp = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * (double) partialTicks;

            double entityXInterp = entityHit.lastTickPosX + (entityHit.posX - entityHit.lastTickPosX) * (double) partialTicks;
            double entityYInterp = entityHit.lastTickPosY + (entityHit.posY - entityHit.lastTickPosY) * (double) partialTicks;
            double entityZInterp = entityHit.lastTickPosZ + (entityHit.posZ - entityHit.lastTickPosZ) * (double) partialTicks;

            double entityXDiff = entityHit.posX - entityXInterp;
            double entityYDiff = entityHit.posY - entityYInterp;
            double entityZDiff = entityHit.posZ - entityZInterp;

            // Vanilla GL setup
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.glLineWidth(Config.ENTITY_LINES_WIDTH);
            GlStateManager.disableTexture2D();
            GlStateManager.depthMask(false);

            // Draw boxes/lines/both
            if (Config.RENDER_ENTITY_HITBOX_OVERLAY) {
                RenderGlobal.renderFilledBox(
                        entityHit.getRenderBoundingBox().offset(-playerXInterp - entityXDiff, -playerYInterp - entityYDiff, -playerZInterp - entityZDiff),
                        Config.ENTITY_OVERLAY_RED, Config.ENTITY_OVERLAY_GREEN, Config.ENTITY_OVERLAY_BLUE, Config.ENTITY_OVERLAY_ALPHA);
            }
            if (Config.RENDER_ENTITY_HITBOX_LINES) {
                RenderGlobal.drawSelectionBoundingBox(
                        entityHit.getRenderBoundingBox().offset(-playerXInterp - entityXDiff, -playerYInterp - entityYDiff, -playerZInterp - entityZDiff),
                        Config.ENTITY_LINES_RED, Config.ENTITY_LINES_GREEN, Config.ENTITY_LINES_BLUE, Config.ENTITY_LINES_ALPHA);
            }

            // Vanilla GL cleanup
            GlStateManager.depthMask(true);
            GlStateManager.enableTexture2D();
            GlStateManager.disableBlend();
        }
    }
}
