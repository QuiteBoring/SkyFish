package org.skyfish.util;

import java.lang.reflect.Method;

import net.minecraft.client.Minecraft;

public class KeybindUtils {
  
    private static final Minecraft mc = Minecraft.getMinecraft();

    public static void rightClick() {
        if (!invoke(mc, "func_147121_ag")) {
            invoke(mc, "rightClickMouse");
        }
    }

    private static boolean invoke(Object object, String methodName) {
        try {
            final Method method = object.getClass().getDeclaredMethod(methodName);
            method.setAccessible(true);
            method.invoke(object);
            return true;
        } catch (Exception ignored) {}
        
        return false;
    }

}