package uk.co.mysterymayhem.mysthighlights.highlighters;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import uk.co.mysterymayhem.mysthighlights.config.Config;

import static org.lwjgl.opengl.GL11.*;

/**
 * There's no rendering events for entities that don't extend EntityLivingBase, so instead, I render a copy of the
 * entity over the top of itself. The copy having modified lighting.
 */
public class NonLivingColourer {

    @SubscribeEvent
    public static void onRenderWorld(RenderWorldLastEvent event) {
        Minecraft minecraft = Minecraft.getMinecraft();
        RenderManager renderManager = minecraft.getRenderManager();
        RayTraceResult objectMouseOver = minecraft.objectMouseOver;
        if (objectMouseOver.typeOfHit == RayTraceResult.Type.ENTITY) {
            Entity entityHit = objectMouseOver.entityHit;
            if (entityHit instanceof EntityLivingBase) {
                return;
            }
            float partialTicks = event.getPartialTicks();

            minecraft.entityRenderer.enableLightmap();
            GlStateManager.pushMatrix();
            boolean currentlyRenderingShadows = renderManager.isRenderShadow();
            renderManager.setRenderShadow(false);

            //NEEDED
            RenderHelper.enableStandardItemLighting();

            float red = Config.entityOverlayModel_red;
            float green = Config.entityOverlayModel_green;
            float blue = Config.entityOverlayModel_blue;
            float alpha = 1f;
            glLightModel(GL_LIGHT_MODEL_AMBIENT, RenderHelper.setColorBuffer(red, green, blue, alpha));
            for (int i = 0; i < 8; ++i) {
                GlStateManager.glLight(GL_LIGHT0 + i, GL_DIFFUSE, RenderHelper.setColorBuffer(red, green, blue, alpha));
            }
            GlStateManager.color(red, green, blue, alpha);
            renderManager.renderEntityStatic(entityHit, partialTicks, false);
            if (renderManager.isRenderMultipass(entityHit)) {
                renderManager.renderMultipass(entityHit, partialTicks);
            }
            renderManager.setRenderShadow(currentlyRenderingShadows);
            GlStateManager.popMatrix();
            minecraft.entityRenderer.disableLightmap();
        }
    }

}
