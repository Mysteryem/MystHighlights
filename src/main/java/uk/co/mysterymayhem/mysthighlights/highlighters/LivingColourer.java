package uk.co.mysterymayhem.mysthighlights.highlighters;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import uk.co.mysterymayhem.mysthighlights.Config;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;

/**
 * For colouring living entities we can intercept RenderLivingEvents.
 * In the event, we change the lighting colour to whatever's specified in the configs.
 *
 * I wanted to render entities a second time in completely one colour, and ignoring lighting
 * (GlStateManager.enableOutlineMode(...)), but couldn't get transparency to work properly
 *
 * Slimes would render weird due to their partial transparency.
 * Endermen and spiders would render weird due to their eyes being rendered separately.
 * Mooshrooms would render weird whereby the mushrooms on their backs would not inherit some properties and end up
 *      a different colour/transparency than everything else.
 * Held equipment and armour on mobs would act similar to mooshrooms' mushrooms.
 *
 * Whenever it would seem that I finally got one of the above working, one of the others would break.
 *
 * Changing the lighting is the only solution I could come up with that seems to work on everything. But it gives
 *      the user less control over the resultant colour.
 */
public class LivingColourer {

    private static boolean needToPop = false;

    @SubscribeEvent
    public static void onLivingRenderPre(RenderLivingEvent.Pre<EntityLivingBase> event) {
        RayTraceResult objectMouseOver = Minecraft.getMinecraft().objectMouseOver;
        if (objectMouseOver.typeOfHit == RayTraceResult.Type.ENTITY) {
            EntityLivingBase entity = event.getEntity();
            if (objectMouseOver.entityHit == entity) {

                needToPop = true;
                GlStateManager.pushMatrix();
                GlStateManager.pushAttrib();

                float red = Config.ENTITY_MODEL_RED;
                float green = Config.ENTITY_MODEL_GREEN;
                float blue = Config.ENTITY_MODEL_BLUE;
                float alpha = 1f;
                FloatBuffer colourBuffer = RenderHelper.setColorBuffer(red, green, blue, alpha);
                glLightModel(GL_LIGHT_MODEL_AMBIENT, colourBuffer);
                for (int i = 0; i < 8; ++i) {
                    GlStateManager.glLight(GL_LIGHT0 + i, GL_DIFFUSE, colourBuffer);
                }

                GlStateManager.color(red, green, blue, alpha);
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            }
        }
    }

    @SubscribeEvent
    public static void onLivingRenderPost(RenderLivingEvent.Post<EntityLivingBase> event) {
        if (needToPop) {
            needToPop = false;
            GlStateManager.disableBlend();
            GlStateManager.disableOutlineMode();
            GlStateManager.popAttrib();
            GlStateManager.popMatrix();
        }
    }

}
