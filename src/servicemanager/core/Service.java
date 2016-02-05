package servicemanager.core;

import com.sun.corba.se.spi.activation.ServerManager;
import com.sun.istack.internal.Pool;
import com.sun.xml.internal.ws.api.policy.PolicyResolver;
import servicemanager.annotations.Dependencies;
import servicemanager.annotations.Implements;

import java.lang.annotation.Annotation;

import static java.lang.String.format;

public class Service {
    private ServiceManager mServiceManager;
    public Service(ServiceManager serviceManager){
        mServiceManager = serviceManager;
    }

    void init() {}
    void start() {}
    void stop() {}

    Service getService(Class<? extends ServiceContract> contract){
        return mServiceManager.getServiceForContract(contract);
    }

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
