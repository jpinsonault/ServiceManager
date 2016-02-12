package io.einhard.servicemanager.services;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import io.einhard.servicemanager.annotations.Implements;
import io.einhard.servicemanager.core.Service;
import io.einhard.servicemanager.core.ServiceManager;

import java.util.Arrays;

import static java.lang.String.format;

/*
    This service is a bit special. It gets bootstrapped into the list of
    services to load  by the ServiceManager. It's implementation should never
    be specified by the list of services loaded at runtime.
 */
@Implements(contract = ConfigServiceContract.class)
public class ConfigService extends Service implements ConfigServiceContract {
    JsonObject mConfig;

    public ConfigService(ServiceManager serviceManager) {
        super(serviceManager);
    }

    public ConfigService(ServiceManager serviceManager, JsonObject config) {
        super(serviceManager);

        mConfig = config;
    }

    @Override
    public JsonObject getConfig() {
        return mConfig;
    }

    public JsonValue getInConfig(String... keys){
        JsonObject result = mConfig;

        // Loop through all but last key
        for (String key : Arrays.asList(keys).subList(0, keys.length - 1)) {
            if (result.get(key) == null){
                throw new IllegalArgumentException(format("Tried to get keys: %s", keys.toString()));
            }

            result = result.get(key).asObject();
        }

        // Return the last key as a value
        return result.get(keys[keys.length - 1]);
    }
}
