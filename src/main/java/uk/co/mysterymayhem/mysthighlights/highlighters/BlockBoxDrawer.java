package uk.co.mysterymayhem.mysthighlights.highlighters;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import uk.co.mysterymayhem.mysthighlights.config.Config;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mysteryem on 2017-01-16.
 */
public class BlockBoxDrawer {
    @SubscribeEvent
    public static void onBlockHighlight(DrawBlockHighlightEvent event) {
        // Vanilla seems to check for this, not sure what it's used for, doesn't seem to have a use outside of
        // highlighting the block you're currently looking at
        if (event.getSubID() != 0) {
            return;
        }

        RayTraceResult target = event.getTarget();
        if (target.typeOfHit == RayTraceResult.Type.BLOCK) {
            // By default we prevent vanilla from drawing it's own block highlights
            // I suppose some people might want to simply disable all highlights, which this will also enable them to do
            if (Config.blockOutline_disableVanilla) {
                event.setCanceled(true);
            }

            if (Config.blockOutline_enabled || Config.blockOverlay_enabled) {

                BlockPos blockpos = event.getTarget().getBlockPos();
                EntityPlayer player = event.getPlayer();
                World world = player.world;
                float partialTicks = event.getPartialTicks();

                IBlockState iblockstate = world.getBlockState(blockpos);

                if (iblockstate.getMaterial() != Material.AIR && world.getWorldBorder().contains(blockpos)) {
                    // Vanilla GL setup
                    GlStateManager.enableBlend();
                    GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                    GlStateManager.glLineWidth(Config.blockOutline_lineWidth);
                    GlStateManager.disableTexture2D();
                    GlStateManager.depthMask(false);

                    // Interpolate player position
                    double d0 = player.lastTickPosX + (player.posX - player.lastTickPosX) * (double)partialTicks;
                    double d1 = player.lastTickPosY + (player.posY - player.lastTickPosY) * (double)partialTicks;
                    double d2 = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * (double)partialTicks;

                    // If using collision bounding boxes, there may be multiple that we want to draw
                    List<AxisAlignedBB> collisionBoundingBoxes = new ArrayList<>();
                    AxisAlignedBB selectedBoundingBox = iblockstate.getSelectedBoundingBox(world, blockpos).expandXyz(0.0020000000949949026D).offset(-d0, -d1, -d2);

                    if (Config.blockOverlay_usesCollision || Config.blockOutline_usesCollision) {
                        List<AxisAlignedBB> tempList = new ArrayList<>();
                        // Adds all collision AABBs that collide with the general bounding box of the block
                        iblockstate.addCollisionBoxToList(world, blockpos, iblockstate.getSelectedBoundingBox(world, blockpos), tempList, null);

                        // If there are no collision boxes, we fall back to adding the general bounding box
                        if (tempList.isEmpty()) {
                            collisionBoundingBoxes.add(selectedBoundingBox);
                        }
                        else if (Config.blockCommon_clampCollision) {
                            AxisAlignedBB offsetFullBlock = Block.FULL_BLOCK_AABB.offset(blockpos);
                            for (AxisAlignedBB tempBB : tempList) {
//                                    AxisAlignedBB resultantBB = tempBB.func_191500_a(offsetFullBlock).expandXyz(0.0020000000949949026D).offset(-d0, -d1, -d2);
                                AxisAlignedBB resultantBB = intersection(tempBB, offsetFullBlock).expandXyz(0.0020000000949949026D).offset(-d0, -d1, -d2);
                                collisionBoundingBoxes.add(resultantBB);
                            }
                        }
                        else {
                            for (AxisAlignedBB tempBB : tempList) {
                                collisionBoundingBoxes.add(tempBB.expandXyz(0.0020000000949949026D).offset(-d0, -d1, -d2));
                            }
                        }
                    }

                    if (Config.blockOverlay_enabled) {
                        if (Config.blockOverlay_usesCollision) {
                            for (AxisAlignedBB bb : collisionBoundingBoxes) {
                                RenderGlobal.renderFilledBox(
                                        bb, Config.blockOverlay_red, Config.blockOverlay_green, Config.blockOverlay_blue, Config.blockOverlay_alpha);
                            }
                        }
                        else {
                            RenderGlobal.renderFilledBox(
                                    selectedBoundingBox, Config.blockOverlay_red, Config.blockOverlay_green, Config.blockOverlay_blue, Config.blockOverlay_alpha);
                        }
                    }
                    if (Config.blockOutline_enabled) {
                        if (Config.blockOutline_usesCollision) {
                            for (AxisAlignedBB bb : collisionBoundingBoxes) {
                                RenderGlobal.drawSelectionBoundingBox(
                                        bb, Config.blockOutline_red, Config.blockOutline_green, Config.blockOutline_blue, Config.blockOutline_alpha);
                            }
                        }
                        else {
                            RenderGlobal.drawSelectionBoundingBox(
                                    selectedBoundingBox, Config.blockOutline_red, Config.blockOutline_green, Config.blockOutline_blue, Config.blockOutline_alpha);
                        }
                    }

                    // Vanilla GL cleanup
                    GlStateManager.depthMask(true);
                    GlStateManager.enableTexture2D();
                    GlStateManager.disableBlend();
                }
            }
        }
    }

    //AxisAlignedBB::func_191500_a is a method new to 1.11 (not in 1.10.2)
    private static AxisAlignedBB intersection(AxisAlignedBB first, AxisAlignedBB second) {
        double minX = Math.max(first.minX, second.minX);
        double minY = Math.max(first.minY, second.minY);
        double minZ = Math.max(first.minZ, second.minZ);
        double maxX = Math.min(first.maxX, second.maxX);
        double maxY = Math.min(first.maxY, second.maxY);
        double maxZ = Math.min(first.maxZ, second.maxZ);
        return new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
    }
}
