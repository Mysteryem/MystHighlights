package uk.co.mysterymayhem.mysthighlights;

/**
 * Created by Mysteryem on 2016-12-08.
 */
public class Highlights {

//    //AxisAlignedBB::func_191500_a is a method new to 1.11 (not in 1.10.2)
//    public static AxisAlignedBB intersection(AxisAlignedBB first, AxisAlignedBB second) {
//        double minX = Math.max(first.minX, second.minX);
//        double minY = Math.max(first.minY, second.minY);
//        double minZ = Math.max(first.minZ, second.minZ);
//        double maxX = Math.min(first.maxX, second.maxX);
//        double maxY = Math.min(first.maxY, second.maxY);
//        double maxZ = Math.min(first.maxZ, second.maxZ);
//        return new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
//    }
//
//    /**
//     * For colouring living entities we can intercept RenderLivingEvents.
//     * In the event, we change the lighting colour to whatever's specified in the configs.
//     *
//     * I wanted to render entities a second time in completely one colour, and ignoring lighting
//     * (GlStateManager.enableOutlineMode(...)), but couldn't get transparency to work properly
//     *
//     * Slimes would render weird due to their partial transparency.
//     * Endermen and spiders would render weird due to their eyes being rendered separately.
//     * Mooshrooms would render weird whereby the mushrooms on their backs would not inherit some properties and end up
//     *      a different colour/transparency than everything else.
//     * Held equipment and armour on mobs would act similar to mooshrooms' mushrooms.
//     *
//     * Whenever it would seem that I finally got one of the above working, one of the others would break.
//     *
//     * Changing the lighting is the only solution I could come up with that seems to work on everything. But it gives
//     *      the user less control over the resultant colour.
//     */
//    public static class LivingColourer {
//
//        private static boolean needToPop = false;
//        private static boolean internalRendering = false;
//
//        @SubscribeEvent
//        public static void onLivingRenderPre(RenderLivingEvent.Pre<EntityLivingBase> event) {
//            if (internalRendering) {
//                return;
//            }
//            RayTraceResult objectMouseOver = Minecraft.getMinecraft().objectMouseOver;
//            if (objectMouseOver.typeOfHit == RayTraceResult.Type.ENTITY) {
//                EntityLivingBase entity = event.getEntity();
//                if (objectMouseOver.entityHit == entity) {
//
//                    needToPop = true;
//                    GlStateManager.pushMatrix();
//                    GlStateManager.pushAttrib();
//
//                    float red = Config.ENTITY_MODEL_RED;
//                    float green = Config.ENTITY_MODEL_GREEN;
//                    float blue = Config.ENTITY_MODEL_BLUE;
//                    float alpha = 1f;
//                    FloatBuffer colourBuffer = RenderHelper.setColorBuffer(red, green, blue, alpha);
//                    glLightModel(GL_LIGHT_MODEL_AMBIENT, colourBuffer);
//                    for (int i = 0; i < 8; ++i) {
//                        GlStateManager.glLight(GL_LIGHT0 + i, GL_DIFFUSE, colourBuffer);
//                    }
//
//                    GlStateManager.color(red, green, blue, alpha);
//                    GlStateManager.enableBlend();
//                    GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
//                }
//            }
//        }
//
//        @SubscribeEvent
//        public static void onLivingRenderPost(RenderLivingEvent.Post<EntityLivingBase> event) {
//            if (needToPop) {
//                needToPop = false;
//                GlStateManager.disableBlend();
//                GlStateManager.disableOutlineMode();
//                GlStateManager.popAttrib();
//                GlStateManager.popMatrix();
//            }
//        }
//
//    }
//
//    /**
//     * There's no rendering events for entities that don't extend EntityLivingBase, so instead, I render a copy of the
//     * entity over the top of itself. The copy having modified lighting.
//     */
//    public static class NonLivingColourer {
//
//        @SubscribeEvent
//        public static void onRenderWorld(RenderWorldLastEvent event) {
//            Minecraft minecraft = Minecraft.getMinecraft();
//            RenderManager renderManager = minecraft.getRenderManager();
//            RayTraceResult objectMouseOver = minecraft.objectMouseOver;
//            if (objectMouseOver.typeOfHit == RayTraceResult.Type.ENTITY) {
//                Entity entityHit = objectMouseOver.entityHit;
//                if (entityHit instanceof EntityLivingBase) {
//                    return;
//                }
//                float partialTicks = event.getPartialTicks();
//
//                minecraft.entityRenderer.enableLightmap();
//                GlStateManager.pushMatrix();
//                boolean currentlyRenderingShadows = renderManager.isRenderShadow();
//                renderManager.setRenderShadow(false);
//
//                //NEEDED
//                RenderHelper.enableStandardItemLighting();
//
//                float red = Config.ENTITY_MODEL_RED;
//                float green = Config.ENTITY_MODEL_GREEN;
//                float blue = Config.ENTITY_MODEL_BLUE;
//                float alpha = 1f;
//                glLightModel(GL_LIGHT_MODEL_AMBIENT, RenderHelper.setColorBuffer(red, green, blue, alpha));
//                for (int i = 0; i < 8; ++i) {
//                    GlStateManager.glLight(GL_LIGHT0 + i, GL_DIFFUSE, RenderHelper.setColorBuffer(red, green, blue, alpha));
//                }
//                GlStateManager.color(red, green, blue, alpha);
//                renderManager.renderEntityStatic(entityHit, partialTicks, false);
//                if (renderManager.isRenderMultipass(entityHit)) {
//                    renderManager.renderMultipass(entityHit, partialTicks);
//                }
//                renderManager.setRenderShadow(currentlyRenderingShadows);
//                GlStateManager.popMatrix();
//                minecraft.entityRenderer.disableLightmap();
//            }
//        }
//
//    }
//
//    /**
//     * Renders the boxes (either filled or their outline or both) around the current block/entity the client player is
//     * looking at.
//     *
//     * Optionally disables the vanilla block highlight
//     */
//    public static class BlockAndEntityBoxDrawer {
//
//        @SubscribeEvent
//        public static void onBlockHighlight(DrawBlockHighlightEvent event) {
//            // Vanilla seems to check for this, not sure what it's used for, doesn't seem to have a use outside of
//            // highlighting the block you're currently looking at
//            if (event.getSubID() != 0) {
//                return;
//            }
//
//            RayTraceResult target = event.getTarget();
//            if ((Config.RENDER_ENTITY_HITBOX_LINES || Config.RENDER_ENTITY_HITBOX_OVERLAY) && target.typeOfHit == RayTraceResult.Type.ENTITY) {
//
//                // Interpolate player and entity positions
//                Entity entityHit = target.entityHit;
//                EntityPlayer player = event.getPlayer();
//                float partialTicks = event.getPartialTicks();
//
//                double playerXInterp = player.lastTickPosX + (player.posX - player.lastTickPosX) * (double) partialTicks;
//                double playerYInterp = player.lastTickPosY + (player.posY - player.lastTickPosY) * (double) partialTicks;
//                double playerZInterp = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * (double) partialTicks;
//
//                double entityXInterp = entityHit.lastTickPosX + (entityHit.posX - entityHit.lastTickPosX) * (double) partialTicks;
//                double entityYInterp = entityHit.lastTickPosY + (entityHit.posY - entityHit.lastTickPosY) * (double) partialTicks;
//                double entityZInterp = entityHit.lastTickPosZ + (entityHit.posZ - entityHit.lastTickPosZ) * (double) partialTicks;
//
//                double entityXDiff = entityHit.posX - entityXInterp;
//                double entityYDiff = entityHit.posY - entityYInterp;
//                double entityZDiff = entityHit.posZ - entityZInterp;
//
//                // Vanilla GL setup
//                GlStateManager.enableBlend();
//                GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
//                GlStateManager.glLineWidth(Config.ENTITY_LINES_WIDTH);
//                GlStateManager.disableTexture2D();
//                GlStateManager.depthMask(false);
//
//                // Draw boxes/lines/both
//                if (Config.RENDER_ENTITY_HITBOX_OVERLAY) {
//                    RenderGlobal.renderFilledBox(
//                            entityHit.getRenderBoundingBox().offset(-playerXInterp - entityXDiff, -playerYInterp - entityYDiff, -playerZInterp - entityZDiff),
//                            Config.ENTITY_OVERLAY_RED, Config.ENTITY_OVERLAY_GREEN, Config.ENTITY_OVERLAY_BLUE, Config.ENTITY_OVERLAY_ALPHA);
//                }
//                if (Config.RENDER_ENTITY_HITBOX_LINES) {
//                    RenderGlobal.drawSelectionBoundingBox(
//                            entityHit.getRenderBoundingBox().offset(-playerXInterp - entityXDiff, -playerYInterp - entityYDiff, -playerZInterp - entityZDiff),
//                            Config.ENTITY_LINES_RED, Config.ENTITY_LINES_GREEN, Config.ENTITY_LINES_BLUE, Config.ENTITY_LINES_ALPHA);
//                }
//
//                // Vanilla GL cleanup
//                GlStateManager.depthMask(true);
//                GlStateManager.enableTexture2D();
//                GlStateManager.disableBlend();
//            }
//
//            // I'm hoping the JVM can optimise at runtime such that the StaticConfig checks no longer exist in the runtime code
//            else if ((Config.RENDER_BLOCK_LINES || Config.RENDER_BLOCK_OVERLAY || Config.DISABLE_VANILLA_BLOCK_HIGHLIGHT)
//                    && target.typeOfHit == RayTraceResult.Type.BLOCK) {
//                // By default we prevent vanilla from drawing it's own block highlights
//                // I suppose some people might want to simply disable all highlights, which this will also enable them to do
//                if (Config.DISABLE_VANILLA_BLOCK_HIGHLIGHT) {
//                    event.setCanceled(true);
//                }
//
//                if (Config.RENDER_BLOCK_LINES || Config.RENDER_BLOCK_OVERLAY) {
//
//                    BlockPos blockpos = event.getTarget().getBlockPos();
//                    EntityPlayer player = event.getPlayer();
//                    World world = player.worldObj;
//                    float partialTicks = event.getPartialTicks();
//
//                    IBlockState iblockstate = world.getBlockState(blockpos);
//
//                    if (iblockstate.getMaterial() != Material.AIR && world.getWorldBorder().contains(blockpos)) {
//                        // Vanilla GL setup
//                        GlStateManager.enableBlend();
//                        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
//                        GlStateManager.glLineWidth(Config.BLOCK_LINES_WIDTH);
//                        GlStateManager.disableTexture2D();
//                        GlStateManager.depthMask(false);
//
//                        // Interpolate player position
//                        double d0 = player.lastTickPosX + (player.posX - player.lastTickPosX) * (double) partialTicks;
//                        double d1 = player.lastTickPosY + (player.posY - player.lastTickPosY) * (double) partialTicks;
//                        double d2 = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * (double) partialTicks;
//
//                        // If using collision bounding boxes, there may be multiple that we want to draw
//                        List<AxisAlignedBB> collisionBoundingBoxes = new ArrayList<>();
//                        AxisAlignedBB selectedBoundingBox = iblockstate.getSelectedBoundingBox(world, blockpos).expandXyz(0.0020000000949949026D).offset(-d0, -d1, -d2);
//
//                        if (Config.RENDER_BLOCK_OVERLAY_USES_COLLISION || Config.RENDER_BLOCK_LINES_USES_COLLISION) {
//                            List<AxisAlignedBB> tempList = new ArrayList<>();
//                            // Adds all collision AABBs that collide with the general bounding box of the block
//                            iblockstate.addCollisionBoxToList(world, blockpos, iblockstate.getSelectedBoundingBox(world, blockpos), tempList, null);
//
//                            // If there are no collision boxes, we fall back to adding the general bounding box
//                            if (tempList.isEmpty()) {
//                                collisionBoundingBoxes.add(selectedBoundingBox);
//                            }
//                            else if (Config.BLOCK_COLLISION_BOXES_CLAMPED){
//                                AxisAlignedBB offsetFullBlock = Block.FULL_BLOCK_AABB.offset(blockpos);
//                                for (AxisAlignedBB tempBB : tempList) {
////                                    AxisAlignedBB resultantBB = tempBB.func_191500_a(offsetFullBlock).expandXyz(0.0020000000949949026D).offset(-d0, -d1, -d2);
//                                    AxisAlignedBB resultantBB = Highlights.intersection(tempBB, offsetFullBlock).expandXyz(0.0020000000949949026D).offset(-d0, -d1, -d2);
//                                    collisionBoundingBoxes.add(resultantBB);
//                                }
//                            }
//                            else {
//                                for (AxisAlignedBB tempBB : tempList) {
//                                    collisionBoundingBoxes.add(tempBB.expandXyz(0.0020000000949949026D).offset(-d0, -d1, -d2));
//                                }
//                            }
//                        }
//
//                        if (Config.RENDER_BLOCK_OVERLAY) {
//                            if (Config.RENDER_BLOCK_OVERLAY_USES_COLLISION) {
//                                for (AxisAlignedBB bb : collisionBoundingBoxes) {
//                                    RenderGlobal.renderFilledBox(
//                                            bb, Config.BLOCK_OVERLAY_RED, Config.BLOCK_OVERLAY_GREEN, Config.BLOCK_OVERLAY_BLUE, Config.BLOCK_OVERLAY_ALPHA);
//                                }
//                            }
//                            else {
//                                RenderGlobal.renderFilledBox(
//                                        selectedBoundingBox, Config.BLOCK_OVERLAY_RED, Config.BLOCK_OVERLAY_GREEN, Config.BLOCK_OVERLAY_BLUE, Config.BLOCK_OVERLAY_ALPHA);
//                            }
//                        }
//                        if (Config.RENDER_BLOCK_LINES) {
//                            if (Config.RENDER_BLOCK_LINES_USES_COLLISION) {
//                                for (AxisAlignedBB bb : collisionBoundingBoxes) {
//                                    RenderGlobal.drawSelectionBoundingBox(
//                                            bb, Config.BLOCK_OVERLAY_RED, Config.BLOCK_OVERLAY_GREEN, Config.BLOCK_OVERLAY_BLUE, Config.BLOCK_OVERLAY_ALPHA);
//                                }
//                            }
//                            else {
//                                RenderGlobal.drawSelectionBoundingBox(
//                                        selectedBoundingBox, Config.BLOCK_LINES_RED, Config.BLOCK_LINES_GREEN, Config.BLOCK_LINES_BLUE, Config.BLOCK_LINES_ALPHA);
//                            }
//                        }
//
//                        // Vanilla GL cleanup
//                        GlStateManager.depthMask(true);
//                        GlStateManager.enableTexture2D();
//                        GlStateManager.disableBlend();
//                    }
//                }
//            }
//        }
//    }
}
