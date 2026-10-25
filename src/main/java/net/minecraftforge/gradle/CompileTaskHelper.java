package net.minecraftforge.gradle;

import org.gradle.api.tasks.compile.AbstractCompile;

import java.io.File;
import java.lang.reflect.Method;

public class CompileTaskHelper {
    private static final CompileTaskWrapper BUILD_DIR_GETTER = GradleVersionUtils.choose("6.1",
            OldCompileTaskWrapper::new,
            NewCompileTaskWrapper::new
    );

    public static void setDestinationDir(AbstractCompile task, File file) {
        BUILD_DIR_GETTER.setDestinationDir(task, file);
    }

    public interface CompileTaskWrapper {
        void setDestinationDir(AbstractCompile task, File file);
    }

    private static class OldCompileTaskWrapper implements CompileTaskWrapper {
        private static final Method setDestinationDir;

        static {
            try {
                setDestinationDir = AbstractCompile.class.getMethod("setDestinationDir", File.class);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void setDestinationDir(AbstractCompile task, File file) {
            ReflectionHelper.call(setDestinationDir, task, file);
        }
    }

    private static class NewCompileTaskWrapper implements CompileTaskWrapper {
        @Override
        public void setDestinationDir(AbstractCompile task, File file) {
            task.getDestinationDirectory().set(file);
        }
    }

}
