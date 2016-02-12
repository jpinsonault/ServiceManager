package io.einhard.servicemanager.services;

import com.eclipsesource.json.JsonObject;
import io.einhard.servicemanager.core.ServiceContract;

public interface ConfigServiceContract extends ServiceContract{
    JsonObject getConfig();
}
