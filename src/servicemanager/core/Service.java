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

    Class<? extends ServiceContract> contract() {
        if (!this.getClass().isAnnotationPresent(Implements.class)) {
            String error = format("Missing annotation 'Implements' on class '%s'",
                                  this.getClass().getSimpleName());
            throw new RuntimeException(error);
        }

        return this.getClass().getAnnotation(Implements.class).contract();
    }

    Class<? extends ServiceContract>[] dependencies() {
        if (this.getClass().isAnnotationPresent(Dependencies.class)) {
            return this.getClass().getAnnotation(Dependencies.class).services();
        }
        else {
            return null;
        }
    }
}
