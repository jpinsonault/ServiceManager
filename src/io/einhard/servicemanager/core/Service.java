package io.einhard.servicemanager.core;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import io.einhard.servicemanager.annotations.Dependencies;
import io.einhard.servicemanager.annotations.Implements;
import io.einhard.servicemanager.services.ConfigService;
import io.einhard.servicemanager.services.ConfigServiceContract;

import static java.lang.String.format;

public class Service {
    protected ServiceManager mServiceManager;
    public Service(ServiceManager serviceManager){
        mServiceManager = serviceManager;
    }

    public void init() {}
    public void start() {}
    public void stop() {}

    public Service getService(Class<? extends ServiceContract> contract){
        return mServiceManager.getServiceForContract(contract);
    }

    public JsonObject getConfig(){
        ConfigService configService = (ConfigService) mServiceManager.getServiceForContract(ConfigServiceContract.class);

        return configService.getConfig();
    }

    public JsonValue getInConfig(String... keys){
        ConfigService configService = (ConfigService) mServiceManager.getServiceForContract(ConfigServiceContract.class);

        return configService.getInConfig(keys);
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
