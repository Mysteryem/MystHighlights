package uk.co.mysterymayhem.mysthighlights.highlighters;

/**
 * Created by Mysteryem on 2017-01-15.
 */

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import uk.co.mysterymayhem.mysthighlights.config.Config;
import uk.co.mysterymayhem.mysthighlights.util.ConfigColourFontRenderer;
import uk.co.mysterymayhem.mysthighlights.util.Util;

/**
 * Applied the vanilla glowing effect to the entity you're looking at.
 * <p>
 * Sets the glowing flag and calls Entity::setGlowing. For some reason, Entity::setGlowing isn't enough to actually make
 * entities glow, I looked through the source and found that a flag gets set only on the server side, I apply this flag
 * as per necessary on the client side.
 * <p>
 * This glow effect specifically does not cause issues with already glowing entities, entities that gain the glowing
 * effect whilst you're looking at them or entities that lose the glowing effect whilst you're looking at them.
 */
public class EntityGlowOutliner {

    private static final String TEAM_NAME = "mh_outlineglow";
    private static final ConfigColourFontRenderer CONFIG_COLOUR_FONT_RENDERER = new ConfigColourFontRenderer() {
        @Override
        public int getColourFromConfig() {
            return Config.entityOutlineModelGlow_colour;
        }
    };

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onRenderWorldLastHigh(RenderWorldLastEvent event) {
        Entity entity;
        Minecraft minecraft = Minecraft.getMinecraft();
        RayTraceResult objectMouseOver = minecraft.objectMouseOver;
        if (objectMouseOver != null && objectMouseOver.typeOfHit == RayTraceResult.Type.ENTITY) {
            entity = objectMouseOver.entityHit;

            // Get renderer
            Render<Entity> renderer = minecraft.getRenderManager().getEntityRenderObject(entity);

            // Method is annotated @Nullable, is that really correct?
            if (renderer == null) {
                return;
            }

            // Get render manager
            RenderManager renderManager = renderer.getRenderManager();

            float partialTicks = minecraft.getRenderPartialTicks();

            Scoreboard scoreboard = entity.world.getScoreboard();
            String nameUsedInScoreboard = Util.getScoreboardName(entity);

            // Current team
            ScorePlayerTeam currentTeam = scoreboard.getPlayersTeam(nameUsedInScoreboard);

            // We need to put the entity on a special team
            ScorePlayerTeam highlightsteam = scoreboard.getTeam(TEAM_NAME);
            if (highlightsteam == null) {
                highlightsteam = scoreboard.createTeam(TEAM_NAME);
                highlightsteam.setNamePrefix(Util.TEAM_NAME_PREFIX);
            }
            // FIXME: Seems to have a tendency to get reset?
//            highlightsteam.setNamePrefix(SPECIAL_NAME_PREFIX);
            // Add player to the team
            scoreboard.addPlayerToTeam(nameUsedInScoreboard, highlightsteam.getRegisteredName());

            // Set the render manager's font renderer
            // Directly accessing the field instead of using the getter method so we can ensure state goes back to how it was before
            FontRenderer oldFontRenderer = renderManager.textRenderer;
            renderManager.textRenderer = CONFIG_COLOUR_FONT_RENDERER;

            RenderGlobal renderGlobal = minecraft.renderGlobal;
            RenderManager mcRenderManager = minecraft.getRenderManager();

            minecraft.getFramebuffer().bindFramebuffer(true);
            renderGlobal.entityOutlinesRendered = true;
            renderGlobal.entityOutlineFramebuffer.bindFramebuffer(false);

            // Disables text rendering, except for itemframes...
            mcRenderManager.setRenderOutlines(true);

            // FIXME: Glowing effect gets applied to already rendered outlines in the entityOutlineFramebuffer

            // Workaround for item frame text rendering even with setRenderOutlines(true)
            ItemStack displayedItem;
            if (entity instanceof EntityItemFrame
                    && (displayedItem = ((EntityItemFrame)entity).getDisplayedItem()) != null // Null check for 1.10.2 compat
                    && displayedItem.hasDisplayName()) {
                String displayName = displayedItem.getDisplayName();
                displayedItem.clearCustomName();

                // Actually render the entity
                mcRenderManager.renderEntityStatic(entity, partialTicks, false);

                // Restore itemstack display name
                displayedItem.setStackDisplayName(displayName);
            }
            else {
                // Actually renders the entity
                mcRenderManager.renderEntityStatic(entity, partialTicks, false);
            }

            // Re-enables text rendering
            mcRenderManager.setRenderOutlines(false);

            // Not needed?
            renderGlobal.entityOutlineFramebuffer.bindFramebuffer(true);

            // Needed (actually 'runs' the shader as far as I can tell)
            renderGlobal.entityOutlineShader.loadShaderGroup(partialTicks);

            // Needed
            GlStateManager.enableDepth();

            // Needed (Some pixels on spawn eggs weren't rendering otherwise. Not noticed any other issues)
            GlStateManager.enableAlpha();

            // Needed
            minecraft.getFramebuffer().bindFramebuffer(false);

//            GlStateManager.disableLighting();
//            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
//            GlStateManager.enableLighting();

            // Needed to clean up lighting
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240f, 240f);

            // Needed to clean up lighting
            minecraft.entityRenderer.disableLightmap();

            // Restore the font renderer
            renderManager.textRenderer = oldFontRenderer;

            // Restore the team
            if (currentTeam != null) {
                scoreboard.addPlayerToTeam(nameUsedInScoreboard, currentTeam.getRegisteredName());
            }
            else {
                scoreboard.removePlayerFromTeam(nameUsedInScoreboard, highlightsteam);
            }
        }
    }
}
