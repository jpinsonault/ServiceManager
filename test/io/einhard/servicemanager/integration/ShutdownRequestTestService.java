package io.einhard.servicemanager.integration;

import io.einhard.servicemanager.annotations.Dependencies;
import io.einhard.servicemanager.annotations.Implements;
import io.einhard.servicemanager.core.Service;
import io.einhard.servicemanager.core.ServiceManager;
import io.einhard.servicemanager.services.ConfigServiceContract;

@Implements(contract = EmptyContract.class)
@Dependencies(services = {ConfigServiceContract.class})
public class ShutdownRequestTestService extends Service implements EmptyContract {
    public ShutdownRequestTestService(ServiceManager serviceManager) {
        super(serviceManager);
    }

    @Override
    public void start(){
        Runnable shutMeDown = () -> mServiceManager.requestShutdown("I feel like it");

        new Thread(shutMeDown).start();
    }
}
