package servicemanager.core;

import com.sun.istack.internal.Pool;
import servicemanager.annotations.Dependencies;
import servicemanager.annotations.Implements;

import java.lang.annotation.Annotation;

import static java.lang.String.format;

public class Service {
    void init() {}
    void start() {}
    void stop() {}

    Class contract() {
        ensureAnnotated(Implements.class, Implements.class.getSimpleName());
        return this.getClass().getAnnotation(Implements.class).contract();
    }

    Class[] dependencies() {
        ensureAnnotated(Dependencies.class, Dependencies.class.getSimpleName());
        return this.getClass().getAnnotation(Dependencies.class).services();
    }

    void ensureAnnotated(Class annotation, String name) {
        if (!this.getClass().isAnnotationPresent(annotation)){
            String error = format("Missing annotation '%s' on class '%s'",
                    name,
                    this.getClass().getSimpleName());
            throw new RuntimeException(error);
        }
    }
}
