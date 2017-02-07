package uk.co.mysterymayhem.mysthighlights.highlighters;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import org.lwjgl.opengl.GL11;
import uk.co.mysterymayhem.mysthighlights.Config;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;

/**
 * Renders an outline around the entity the palyer's currently looking at. This is achieved by:
 * <p>
 * • Rendering the entity, but scaled in X and Y relative to the camera, but with outlines mode enabled, sometimes moving
 * it backwards (-z) to deal with z-fighting issues.
 * <p>
 * • Then rendering the entity as normally as possible over the top.
 * <p>
 * The amount scaled in the X and Y directions changes based on how far the player is from the entity in question, this
 * is so that the thickness of the outlines (in pixels) remains roughly constant.
 * <p>
 * There is an issue with this method of creating outlines, namely, that as the re-render of living entities is slightly
 * backwards, that will often result in the ground being rendered over the top of the feet of the entity. This had to be
 * done to tackle some z-fighting issues, since it is now confirmed that there is no stencil buffer.
 */
public class EntityCustomOutliner {

    // Can't turn this into a lambda without depending on my WIP library, something to do in the future maybe
    private static final MethodHandle GET_SHOULD_RENDER_OUTLINES;

    static {
        try {
            Field field = ReflectionHelper.findField(Render.class, "field_188301_f", "renderOutlines");
            GET_SHOULD_RENDER_OUTLINES = MethodHandles.publicLookup().unreflectGetter(field);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

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

            double entX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks;
            double entY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks;
            double entZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks;
            double x = entX - interpX;
            double y = entY - interpY;
            double z = entZ - interpZ;

            GlStateManager.pushMatrix();

            // If glowing, the glow effect is applied over the outline and looks bad.
            // Because we're not calling RenderManager::renderEntityStatic later, the glow effect applies to the entire model
            // which helps show that we have selected the current entity (this happened by accident, but works very well)
            if (!entity.isGlowing()) {

                GlStateManager.depthMask(false);

                GlStateManager.enableOutlineMode(Config.ENTITY_MODEL_OUTLINE_COLOUR);

                GlStateManager.matrixMode(GL11.GL_PROJECTION);
                GlStateManager.pushMatrix();

                if (entity instanceof EntityLivingBase) {
                    // Delicious magic numbers
                    double distFromPlayerSquared = Math.min(20, x * x + y * y + z * z);
                    double percent = distFromPlayerSquared * 0.05;
                    double oneMinusPercent = (1 - percent) * 0.1;
                    double extraScale = oneMinusPercent * 0.3;

                    // Their extra render layers cause issues
                    if (entity instanceof EntityEnderman || entity instanceof EntitySlime || entity instanceof EntitySpider) {

                        extraScale += extraScale; // *=2
                        GlStateManager.translate(0, 0, -0.2);
                    }
                    // Armour is also an extra layer that causes issues and there could be other things
                    else {
                        GlStateManager.translate(0, 0, -0.075);
                    }
                    GlStateManager.scale(1.1 + extraScale, 1.1 + extraScale, 1);
                }
                else {
                    GlStateManager.scale(1.1, 1.1, 1);
                }

                // Rendering of entities kind of has to be done with the modelview matrix as the current matrix mode
                // You get some...bizarre results when using the projection matrix though.
                // Messing with the texture matrix is fun too, but is much more likely to cause crashes.
                GlStateManager.matrixMode(GL11.GL_MODELVIEW);

                // Get colour from config
                GlStateManager.enableOutlineMode(Config.ENTITY_MODEL_OUTLINE_COLOUR);

                // Store old state
                boolean oldRenderOutlines;
                try {
                    oldRenderOutlines = (boolean)GET_SHOULD_RENDER_OUTLINES.invokeExact(renderer);
                } catch (Throwable throwable) {
                    throw new RuntimeException(throwable);
                }

                // Disables name drawing (we don't want to render names multiple times in different places due to the transformations)
                renderer.setRenderOutlines(true);

                // Begin outline render
                if (entity instanceof EntityLivingBase) {
                    EntityLivingBase entityLivingBase = (EntityLivingBase)entity;
                    int hurtTime = entityLivingBase.hurtTime;
                    entityLivingBase.hurtTime = 0;
                    renderEntity(renderer, x, y, z, entity, partialTicks);
                    entityLivingBase.hurtTime = hurtTime;
                }
                else {
                    // TODO: Use compat layer for null itemstack checking?
                    // Null check will pass in 1.11.x always, but only sometimes in 1.10.2
                    if (entity instanceof EntityItemFrame && ((EntityItemFrame)entity).getDisplayedItem() != null) {
                        EntityItemFrame entityItemFrame = (EntityItemFrame)entity;

                        ItemStack displayedItem = entityItemFrame.getDisplayedItem();
                        if (displayedItem.hasDisplayName()) {
                            ItemStack copy = displayedItem.copy();
                            copy.clearCustomName();
                            entityItemFrame.setDisplayedItem(copy);
                        }

                        renderEntity(renderer, x, y, z, entity, partialTicks);

                        entityItemFrame.setDisplayedItem(displayedItem);
                    }
                    else {
                        renderEntity(renderer, x, y, z, entity, partialTicks);
                    }
                }
                // Restore the renderOutlines field
                renderer.setRenderOutlines(oldRenderOutlines);

                GlStateManager.disableOutlineMode();

                GlStateManager.matrixMode(GL11.GL_PROJECTION);
                GlStateManager.popMatrix();

                GlStateManager.matrixMode(GL11.GL_MODELVIEW);

                GlStateManager.depthMask(true);

            }

            // Begin normal render

            // Enable lighting, this is correct most of the time
            RenderHelper.enableStandardItemLighting();
            int light;
            if (renderer instanceof RenderLiving) {
                light = entity.getBrightnessForRender(partialTicks);
            }
            else {
                //FIXME: This is wrong most (all?) of the time, I'm not sure where lighting for paintings/item frames is done
                light = thePlayer.worldObj.getCombinedLight(new BlockPos(entity), 0);
            }
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, light % 65536, light >> 16);

            GlStateManager.pushAttrib();

            // Not using this so that glowing entities render in a special way
//            renderer.getRenderManager().renderEntityStatic(entity, partialTicks, false);

            renderEntity(renderer, x, y, z, entity, partialTicks);

            // TODO: I don't know which of these are needed
            GlStateManager.depthMask(true);
            GlStateManager.disableLighting();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.enableLighting();
            GlStateManager.enableTexture2D();
            // Seems to fix 3rd person lighting weirdness
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f);
            minecraft.entityRenderer.disableLightmap();
            GlStateManager.popMatrix();
            GlStateManager.disableAlpha();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
//            GlStateManager.disableBlend();
//            GlStateManager.disableRescaleNormal();
            GlStateManager.glLineWidth(2.0F);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.popAttrib();

        }
    }

    private static <T extends Entity> void renderEntity(Render<T> renderer, double x, double y, double z, T entity, float partialTicks) {
        renderer.doRender(entity, x, y, z, entity.rotationYaw, partialTicks);
        if (renderer.isMultipass()) {
            renderer.renderMultipass(entity, x, y, z, entity.rotationYaw, partialTicks);
        }
    }

}
