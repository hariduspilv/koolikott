package ee.hm.dop.config.guice;

import static com.google.inject.util.Modules.override;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GuiceInjector {

    private static final Logger logger = LoggerFactory.getLogger(GuiceInjector.class);

    private static Injector injector;

    public static void init() {
        if (injector == null) {
            injector = Guice.createInjector(getModules());
        }
    }

    public static Injector getInjector() {
        return injector;
    }

    private static com.google.inject.Module[] getModules() {
        logger.info("Loading modules annotated with @Module...");
        Reflections reflections = new Reflections("ee.hm.dop");
        Set<Class<?>> moduleClasses = reflections.getTypesAnnotatedWith(Module.class);

        Vector<com.google.inject.Module> modules = new Vector<>();
        // We don't have any guarantee of order, so we have to keep track of
        // what was overridden.
        Set<Class<?>> overriddenModules = new HashSet<>();

        for (Class<?> moduleClass : moduleClasses) {
            Class<?> originalModule = moduleClass.getAnnotation(Module.class).override();

            if (originalModule == com.google.inject.Module.class) {
                if (!overriddenModules.contains(moduleClass)) {
                    logger.debug("Adding module '" + moduleClass + "'");
                    modules.add(newModule(moduleClass));
                }
            } else {
                logger.debug("Adding module '" + moduleClass + "' which overrides '" + originalModule + "'");
                overriddenModules.add(originalModule);
                com.google.inject.Module original = newModule(originalModule);
                removeModule(original, modules);
                modules.add(override(original).with(newModule(moduleClass)));
            }
        }

        logger.info(modules.size() + " modules loaded.");
        logger.debug("Loaded modules: " + modules);

        return modules.toArray(new com.google.inject.Module[modules.size()]);
    }

    private static void removeModule(com.google.inject.Module module, List<com.google.inject.Module> list) {
        Iterator<com.google.inject.Module> iterator = list.iterator();
        while (iterator.hasNext()) {
            com.google.inject.Module original = iterator.next();
            if (original.getClass() == module.getClass()) {
                iterator.remove();
            }
        }
    }

    private static com.google.inject.Module newModule(Class<?> testModuleClass) {
        try {
            return (com.google.inject.Module) testModuleClass.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Module {
        Class<? extends com.google.inject.Module> override() default com.google.inject.Module.class;
    }
}