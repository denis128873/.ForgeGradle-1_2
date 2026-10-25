package net.minecraftforge.gradle;

import org.gradle.api.Project;

import java.io.File;
import java.lang.reflect.Method;

public class ProjectBuildDirHelper {
    private static final BuildDirGetter BUILD_DIR_GETTER = GradleVersionUtils.choose("4.1",
            OldBuildDirGetter::new,
            NewBuildDirGetter::new
    );

    public static File getBuildDir(Project project) {
        return BUILD_DIR_GETTER.getBuildDir(project);
    }

    public interface BuildDirGetter {
        File getBuildDir(Project project);
    }

    private static class OldBuildDirGetter implements BuildDirGetter {
        private static final Method getBuildDir;

        static {
            try {
                getBuildDir = Project.class.getMethod("getBuildDir");
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public File getBuildDir(Project project) {
            return ReflectionHelper.call(getBuildDir, project);
        }
    }

    private static class NewBuildDirGetter implements BuildDirGetter {
        @Override
        public File getBuildDir(Project project) {
            return project.getLayout().getBuildDirectory().get().getAsFile();
        }
    }
}
