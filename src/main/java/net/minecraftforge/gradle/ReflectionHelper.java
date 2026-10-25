package net.minecraftforge.gradle;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReflectionHelper {
    @SuppressWarnings("unchecked")
    public static <T> T call(Method method, Object self, Object... args) {
        try {
            return (T) method.invoke(self, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("Can not call " + method, e);
        }
    }
}
