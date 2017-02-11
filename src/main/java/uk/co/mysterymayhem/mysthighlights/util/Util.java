package uk.co.mysterymayhem.mysthighlights.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Created by Mysteryem on 10/02/2017.
 */
public class Util {
    // This is used to trick the entity renderer into thinking the entity being rendered should have a team coloured outline when rendered with outlines
    public static final String TEAM_NAME_PREFIX = /*Section character*/ "\u00a7" + /*Possibly any second character*/ "2";

    public static String getScoreboardName(Entity entity) {
        if (entity instanceof EntityPlayer) {
            return entity.getName();
        }
        else if (entity instanceof EntityTameable) {
            EntityTameable tameable = (EntityTameable)entity;
            if (tameable.isTamed()) {
                EntityLivingBase entitylivingbase = tameable.getOwner();

                if (entitylivingbase != null) {
                    return getScoreboardName(entitylivingbase);
                }
            }
        }
        return entity.getCachedUniqueIdString();
    }
}
