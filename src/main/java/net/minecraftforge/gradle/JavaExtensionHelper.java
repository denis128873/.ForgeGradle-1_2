package net.minecraftforge.gradle;

import org.gradle.api.JavaVersion;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.tasks.SourceSetContainer;

import java.lang.reflect.Method;
import java.util.Map;

public class JavaExtensionHelper {
    private static final Extensions GETTERS_AND_SETTERS = GradleVersionUtils.choose("7.1",
            ConventionGetter::new,
            ExtensionGetter::new
    );

    private static final Extensions VERSION_GETTERS = GradleVersionUtils.choose("4.10",
            ConventionGetter::new,
            ExtensionGetter::new
    );
    interface Extensions {
        SourceSetContainer getSourceSets(Project project);

        void setSourceCompatibility(Project project, Object value);

        void setTargetCompatibility(Project project, Object value);

        JavaVersion getTargetCompatibility(Project project);
    }

    private static class ConventionGetter implements Extensions {
        private static final Method getConvention;
        private static final Method getPlugins;
        private static final Class<?> javaConventionClass;
        private static final Method getSourceSets;
        private static final Method getTargetCompatibility;
        private static final Method setSourceCompatibility;
        private static final Method setTargetCompatibility;
        private static final Class<?> conventionClass;

        static {
            try {
                conventionClass = Class.forName("org.gradle.api.plugins.Convention");
                getConvention = Project.class.getMethod("getConvention");
                getPlugins = conventionClass.getMethod("getPlugins");
                javaConventionClass = Class.forName("org.gradle.api.plugins.JavaPluginConvention");
                getSourceSets = javaConventionClass.getMethod("getSourceSets");
                getTargetCompatibility = javaConventionClass.getMethod("getTargetCompatibility");
                setSourceCompatibility = javaConventionClass.getMethod("setSourceCompatibility", Object.class);
                setTargetCompatibility = javaConventionClass.getMethod("setTargetCompatibility", Object.class);
            } catch (ClassNotFoundException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public SourceSetContainer getSourceSets(Project project) {
            return ReflectionHelper.call(getSourceSets, getType(project));
        }

        @Override
        public void setSourceCompatibility(Project project, Object value) {
            ReflectionHelper.call(setSourceCompatibility, getType(project), value);
        }

        @Override
        public void setTargetCompatibility(Project project, Object value) {
            ReflectionHelper.call(setTargetCompatibility, getType(project), value);
        }

        @Override
        public JavaVersion getTargetCompatibility(Project project) {
            return ReflectionHelper.call(getTargetCompatibility, getType(project));
        }

        public Object getType(Project project) {
            Object convention = ReflectionHelper.call(getConvention, project);
            Map<String, Object> plugins = ReflectionHelper.call(getPlugins, convention);
            return plugins.get("java");
        }
    }

    private static class ExtensionGetter implements Extensions {
        @Override
        public SourceSetContainer getSourceSets(Project project) {
            return getType(project).getSourceSets();
        }

        @Override
        public void setSourceCompatibility(Project project, Object value) {
            getType(project).setSourceCompatibility(value);
        }

        @Override
        public void setTargetCompatibility(Project project, Object value) {
            getType(project).setTargetCompatibility(value);
        }

        @Override
        public JavaVersion getTargetCompatibility(Project project) {
            return getType(project).getTargetCompatibility();
        }

        public JavaPluginExtension getType(Project project) {
            return project.getExtensions().getByType(JavaPluginExtension.class);
        }
    }

    public static SourceSetContainer getSourceSet(Project project) {
        return GETTERS_AND_SETTERS.getSourceSets(project);
    }

    public static void setSourceCompatibility(Project project, Object value) {
        GETTERS_AND_SETTERS.setSourceCompatibility(project, value);
    }

    public static void setTargetCompatibility(Project project, Object value) {
        GETTERS_AND_SETTERS.setTargetCompatibility(project, value);
    }

    public static JavaVersion getTargetCompatibility(Project project) {
        return VERSION_GETTERS.getTargetCompatibility(project);
    }

}
