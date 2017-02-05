package uk.co.mysterymayhem.mysthighlights.highlighters;

/**
 * Created by Mysteryem on 2017-01-15.
 */

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * Applied the vanilla glowing effect to the entity you're looking at.
 *
 * Sets the glowing flag and calls Entity::setGlowing. For some reason, Entity::setGlowing isn't enough to actually make
 * entities glow, I looked through the source and found that a flag gets set only on the server side, I apply this flag
 * as per necessary on the client side.
 *
 * This glow effect specifically does not cause issues with already glowing entities, entities that gain the glowing
 * effect whilst you're looking at them or entities that lose the glowing effect whilst you're looking at them.
 */
public class EntityGlowOutliner {

    private static final Getter ENTITY_FLAG_GETTER;
    private static final Setter ENTITY_FLAG_SETTER;

    static {
        try {
            // Unreflection on fields/methods/etc that have been set to accessible can be unreflected by any Lookup object
            MethodHandles.Lookup lookup = MethodHandles.publicLookup();

            // ReflectionHelper calls setAccessible(true) on the method
            Method method = ReflectionHelper.findMethod(Entity.class, null, new String[]{"getFlag", "func_70083_f"}, int.class);
            MethodHandle getFlagHandle = lookup.unreflect(method);

            method = ReflectionHelper.findMethod(Entity.class, null, new String[]{"setFlag", "func_70052_a"}, int.class, boolean.class);
            MethodHandle setFlagHandle = lookup.unreflect(method);

            Constructor<MethodHandles.Lookup> classArgsCtr = MethodHandles.Lookup.class.getDeclaredConstructor(Class.class);
            // Got to setAcessible ourselves
            classArgsCtr.setAccessible(true);
            MethodHandles.Lookup entityClassLookup = classArgsCtr.newInstance(Entity.class);

            ENTITY_FLAG_GETTER = (Getter)LambdaMetafactory.metafactory(
                    //caller/context for classloader and access privileges of created lambda class
                    entityClassLookup,
                    // interface method name
                    "get",
                    // imagine return type is type the lambda class' construct returns, arg types are arg types for that constructor
                    MethodType.methodType(Getter.class),
                    // interface method's return type and parameters
                    MethodType.methodType(boolean.class, Entity.class, int.class),
                    // _Direct_ MethodHandle to be used as the implementation (must be for a method/constructor)
                    getFlagHandle,
                    // return type and parameters of the MethodHandle === return type and parameters of the method, with
                    // the declaring class prepended to the parameters if the method is an instance method
                    MethodType.methodType(getFlagHandle.type().returnType(), getFlagHandle.type().parameterArray())
            ).getTarget().invoke();

            ENTITY_FLAG_SETTER = (Setter)LambdaMetafactory.metafactory(
                    entityClassLookup,
                    "set",
                    MethodType.methodType(Setter.class),
                    MethodType.methodType(void.class, Entity.class, int.class, boolean.class),
                    setFlagHandle,
                    MethodType.methodType(setFlagHandle.type().returnType(), setFlagHandle.type().parameterArray())
            ).getTarget().invoke();

        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    @SuppressWarnings("WeakerAccess")
    @FunctionalInterface
    public interface Getter {
        boolean get(Entity entity, int flagIndex);
    }

    @SuppressWarnings("WeakerAccess")
    @FunctionalInterface
    public interface Setter {
        void set(Entity entity, int flagIndex, boolean value);
    }

    // WeakReference in case it's possible to keep reference to the entity even after leaving a server/singleplayer world
    private static WeakReference<Entity> lastNonLivingEntity = null;
    private static boolean lastNonLivingWasPreviouslyGlowing = false;

    @SubscribeEvent
    public static void onRenderWorld(RenderWorldLastEvent event) {
        Entity entity;
        if (lastNonLivingEntity != null && (entity = lastNonLivingEntity.get()) != null) {
            if (!lastNonLivingWasPreviouslyGlowing) {
                boolean getFlag = ENTITY_FLAG_GETTER.get(entity, 6);

                if (getFlag) {
                    ENTITY_FLAG_SETTER.set(entity, 6, false);
                }
                entity.setGlowing(false);
            }
            lastNonLivingEntity = null;
        }

        Minecraft minecraft = Minecraft.getMinecraft();
        RayTraceResult objectMouseOver = minecraft.objectMouseOver;
        if (objectMouseOver.typeOfHit == RayTraceResult.Type.ENTITY) {
            entity = objectMouseOver.entityHit;

            boolean isGlowing = entity.isGlowing();
            lastNonLivingWasPreviouslyGlowing = isGlowing;
            lastNonLivingEntity = new WeakReference<>(entity);
            if (!isGlowing) {
                boolean getFlag = ENTITY_FLAG_GETTER.get(entity, 6);

                if (!getFlag) {
                    ENTITY_FLAG_SETTER.set(entity, 6, true);
                }
                entity.setGlowing(true);
            }
        }
    }
}
