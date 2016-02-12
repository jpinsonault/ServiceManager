package io.einhard.servicemanager.integration;

import io.einhard.servicemanager.annotations.Dependencies;
import io.einhard.servicemanager.annotations.Implements;
import io.einhard.servicemanager.core.Service;
import io.einhard.servicemanager.core.ServiceManager;
import io.einhard.servicemanager.services.ConfigServiceContract;

@Implements(contract = PointlessServiceContract.class)
@Dependencies(services = {ConfigServiceContract.class})
public class IntegrationTestService extends Service implements PointlessServiceContract {
    Boolean inited = false;
    Boolean started = false;
    Boolean stopped = false;
    public IntegrationTestService(ServiceManager serviceManager) {
        super(serviceManager);
    }

    @Override
    public int intFromConfig() {
        return getInConfig("my_special_int").asInt();
    }

    @Override
    public void init(){
        inited = true;
    }

    @Override
    public void start(){
        started = true;
    }

    @Override
    public void stop(){
        stopped = true;
    }

    public Boolean isInited(){ return inited; }
    public Boolean isStarted(){ return started; }
    public Boolean isStopped(){ return stopped; }
}
