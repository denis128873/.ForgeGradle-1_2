package net.minecraftforge.gradle;

import org.gradle.process.JavaExecSpec;

import java.lang.reflect.Method;

public class JavaExecSpecHelper {
    private static final MainSetter MAIN_SETTERS = GradleVersionUtils.choose("6.4",
            OldMainSetter::new, NewMainSetter::new);

    interface MainSetter {
        void setMain(JavaExecSpec spec, String main);
    }

    private static class OldMainSetter implements MainSetter {
        private static final Method setMain;

        static {
            try {
                setMain = JavaExecSpec.class.getMethod("setMain", String.class);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void setMain(JavaExecSpec spec, String main) {
            ReflectionHelper.call(setMain, spec, main);
        }
    }

    private static class NewMainSetter implements MainSetter {
        @Override
        public void setMain(JavaExecSpec spec, String main) {
            spec.getMainClass().set(main);
        }
    }

    public static void setMainClass(JavaExecSpec spec, String main) {
        MAIN_SETTERS.setMain(spec, main);
    }
}
