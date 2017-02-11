package uk.co.mysterymayhem.mysthighlights.highlighters;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;
import uk.co.mysterymayhem.mysthighlights.config.Config;
import uk.co.mysterymayhem.mysthighlights.util.ConfigColourFontRenderer;
import uk.co.mysterymayhem.mysthighlights.util.Util;

/**
 * Renders an outline around the entity the palyer's currently looking at. This is achieved by:
 * <p>
 * • Rendering the entity, but scaled in X and Y relative to the camera, but with outlines mode enabled, sometimes moving it backwards (-z) to deal with
 * z-fighting issues.
 * <p>
 * • Then rendering the entity as normally as possible over the top.
 * <p>
 * The amount scaled in the X and Y directions changes based on how far the player is from the entity in question, this is so that the thickness of the
 * outlines (in pixels) remains roughly constant.
 * <p>
 * There is an issue with this method of creating outlines, namely, that as the re-render of living entities is slightly backwards, that will often result in
 * the ground being rendered over the top of the feet of the entity. This had to be done to tackle some z-fighting issues, since I have confirmed that there
 * is no stencil buffer (0 bits are assigned to the stencil buffer, the internet tells me that 8 bits minimum are needed).
 */
public class EntityCustomOutliner {

    private static final String TEAM_NAME = "mh_outlinecustom";
    private static final ConfigColourFontRenderer CUSTOM_OUTLINE_FONT_RENDERER = new ConfigColourFontRenderer() {
        @Override
        public int getColourFromConfig() {
            return Config.entityOutlineModelCustom_colour;
        }
    };

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onRenderWorld(RenderWorldLastEvent event) {
        Minecraft minecraft = Minecraft.getMinecraft();
        // objectMouseOver shouldn't be null during a RenderWorldLastEvent, since its value is set earlier in the client tick
        // It might be possible for mods to screw with this so maybe it will become an issue
        RayTraceResult objectMouseOver = minecraft.objectMouseOver;
        if (objectMouseOver.typeOfHit == RayTraceResult.Type.ENTITY) {
            Entity entity = objectMouseOver.entityHit;

            Render<Entity> renderer = minecraft.getRenderManager().getEntityClassRenderObject(entity.getClass());

            float partialTicks = minecraft.getRenderPartialTicks();

            EntityPlayerSP thePlayer = minecraft.player;

            // Calculate the position the entity should be rendered at, taking into account the partial render ticks
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
            // which helps show that we have selected the current entity (this happened by accident, but works very well and I don't know how I would get both
            // the glow effect and this custom outline effect to work well together)
            if (!entity.isGlowing() && !Config.entityOutlineModelGlow_enabled) {
                renderCustomOutline(entity, x, y, z, partialTicks, renderer);
            }

            renderEntityNormally(entity, x, y, z, partialTicks, renderer, minecraft);

        }
    }

    private static void renderCustomOutline(Entity entity, double x, double y, double z, float partialTicks, Render<Entity> renderer) {
        GlStateManager.depthMask(false);

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
        GlStateManager.enableOutlineMode(Config.entityOutlineModelCustom_colour);

        // Temporarily remove name tag (it would get scaled up and coloured and would visibly result in two name tags being displayed)
        String customName = entity.getCustomNameTag();
        entity.setCustomNameTag("");

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

        // Restore name tag
        entity.setCustomNameTag(customName);

        GlStateManager.disableOutlineMode();

        // We did a pushMatrix in the Projection matrix, so we need to popMatrix in the Projection matrix as well
        GlStateManager.matrixMode(GL11.GL_PROJECTION);
        GlStateManager.popMatrix();

        GlStateManager.matrixMode(GL11.GL_MODELVIEW);

        GlStateManager.depthMask(true);
    }

    private static void renderEntityNormally(Entity entity, double x, double y, double z, float partialTicks, Render<Entity> renderer, Minecraft minecraft) {
        // Enable lighting, this is correct most of the time
        RenderHelper.enableStandardItemLighting();
        int light;
        if (renderer instanceof RenderLiving) {
            light = entity.getBrightnessForRender(partialTicks);
        }
        else {
            //FIXME: This is wrong most (all?) of the time, I'm not sure where lighting for paintings/item frames is done
            light = entity.world.getCombinedLight(new BlockPos(entity), 0);
        }
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, light % 65536, light >> 16);

        GlStateManager.pushAttrib();

        // Not using this so that glowing entities render in a special way
//            renderer.getRenderManager().renderEntityStatic(entity, partialTicks, false);

//                renderer.setRenderOutlines(false);

        // Setting the team and using a very specialised FontRenderer allows us to change the colour the glow effect uses. Because we're not loading the
        // outline shader or anything, this will cause the entity to render as a silhouette in the colour of our choosing
        Scoreboard scoreboard = entity.world.getScoreboard();
        String nameUsedInScoreboard = Util.getScoreboardName(entity);
        ScorePlayerTeam currentTeam = scoreboard.getPlayersTeam(nameUsedInScoreboard);

        // We need to put the entity on a team
        ScorePlayerTeam highlightsteam = scoreboard.getTeam(TEAM_NAME);
        if (highlightsteam == null) {
            highlightsteam = scoreboard.createTeam(TEAM_NAME);
            highlightsteam.setNamePrefix(Util.TEAM_NAME_PREFIX);
        }
//                    highlightsteam.setNamePrefix(Util.TEAM_NAME_PREFIX);
        scoreboard.addPlayerToTeam(nameUsedInScoreboard, highlightsteam.getRegisteredName());
        RenderManager renderManager = renderer.getRenderManager();
        // Not using the method, since it's possible, but highly unlikely, that the field may contain a different value to what the method returns
        // And want to ensure that the state goes back to how it was beforehand
        FontRenderer oldFontRenderer = renderManager.textRenderer;
        //TODO: MEthodHandle for setting the render manager's font renderer to my 'magic' one. (and then back to normal)

        renderManager.textRenderer = CUSTOM_OUTLINE_FONT_RENDERER;

        // Actually render
        renderEntity(renderer, x, y, z, entity, partialTicks);

        // Restore state
        renderManager.textRenderer = oldFontRenderer;
        if (currentTeam != null) {
            scoreboard.addPlayerToTeam(nameUsedInScoreboard, currentTeam.getRegisteredName());
        }
        else {
            scoreboard.removePlayerFromTeam(nameUsedInScoreboard, scoreboard.getTeam(TEAM_NAME));
        }


//                renderer.setRenderOutlines(renderOutlines);

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

    private static <T extends Entity> void renderEntity(Render<T> renderer, double x, double y, double z, T entity, float partialTicks) {
        renderer.doRender(entity, x, y, z, entity.rotationYaw, partialTicks);
        if (renderer.isMultipass()) {
            renderer.renderMultipass(entity, x, y, z, entity.rotationYaw, partialTicks);
        }
    }

}
