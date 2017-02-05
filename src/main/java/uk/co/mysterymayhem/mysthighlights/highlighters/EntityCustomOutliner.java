package uk.co.mysterymayhem.mysthighlights.highlighters;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;
import uk.co.mysterymayhem.mysthighlights.Config;

/**
 * Renders an outline around the entity the palyer's currently looking at. This is achieved by effectively re-rending
 * the entity, but slightly further away from the camera and scaled in the X and Y directions relative to the camera to
 * offset the fact that the entity is effectively slightly further away.
 *
 * The amount scaled in the X and Y directions changes based on how far the player is from the entity in question, this
 * is so that the thickness of the outlines (in pixels) remains roughly constant.
 *
 * There is an issue with this method of creating outlines, namely, that as the re-render of the entity is slightly
 * backwards, that will often result in the ground being rendered over the top of the feet of the entity. This issue is
 * more noticeable when looking at item frames or at paintings as their outline will almost entirely be covered by the
 * block behind them.
 */
public class EntityCustomOutliner {

    @SubscribeEvent
    public static void onRenderWorld(RenderWorldLastEvent event) {
        Minecraft minecraft = Minecraft.getMinecraft();
        RayTraceResult objectMouseOver = minecraft.objectMouseOver;
        if (objectMouseOver.typeOfHit == RayTraceResult.Type.ENTITY) {
            Entity entity = objectMouseOver.entityHit;

            Render<Entity> renderer = minecraft.getRenderManager().getEntityClassRenderObject(entity.getClass());

            float partialTicks = minecraft.getRenderPartialTicks();

            EntityPlayerSP thePlayer = minecraft.thePlayer;
            double interpX = thePlayer.lastTickPosX + (thePlayer.posX - thePlayer.lastTickPosX) * partialTicks;
            double interpY = thePlayer.lastTickPosY + (thePlayer.posY - thePlayer.lastTickPosY) * partialTicks;
            double interpZ = thePlayer.lastTickPosZ + (thePlayer.posZ - thePlayer.lastTickPosZ) * partialTicks;

            GlStateManager.pushMatrix();
            double entX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks;
            double entY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks;
            double entZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks;
            double x = entX - interpX;
            double y = entY - interpY;
            double z = entZ - interpZ;

            double distFromPlayerSquared = Math.min(20, x * x + y * y + z * z);
            double percent = distFromPlayerSquared * 0.05;
            double oneMinusPercent = (1 - percent) * 0.1;
            double extraScale = oneMinusPercent * 1.4;

            GlStateManager.matrixMode(GL11.GL_PROJECTION);
            GlStateManager.pushMatrix();
            GlStateManager.translate(0, 0, -0.2);
            //1.2 up real close
            //1.1 at max range
            GlStateManager.scale(1.1 + extraScale, 1.1 + extraScale, 1);
            GlStateManager.matrixMode(GL11.GL_MODELVIEW);

            GlStateManager.enableOutlineMode(Config.ENTITY_MODEL_OUTLINE_COLOUR);

//            internalRendering = true;
            renderer.doRender(entity, x, y, z, entity.rotationYaw, partialTicks);
            if (renderer.isMultipass()) {
                renderer.renderMultipass(entity, x, y, z, entity.rotationYaw, partialTicks);
            }
//            internalRendering = false;

            GlStateManager.disableOutlineMode();

            GlStateManager.matrixMode(GL11.GL_PROJECTION);
            GlStateManager.popMatrix();

            GlStateManager.matrixMode(GL11.GL_MODELVIEW);
            GlStateManager.popMatrix();
        }
    }

}
