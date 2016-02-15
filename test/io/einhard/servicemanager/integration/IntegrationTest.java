package io.einhard.servicemanager.integration;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import io.einhard.servicemanager.annotations.Dependencies;
import io.einhard.servicemanager.annotations.Implements;
import io.einhard.servicemanager.core.Service;
import io.einhard.servicemanager.core.ServiceContract;
import io.einhard.servicemanager.core.ServiceManager;
import io.einhard.servicemanager.services.ConfigService;
import io.einhard.servicemanager.services.ConfigServiceContract;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.*;

interface PointlessServiceContract extends ServiceContract {
    public int intFromConfig();
    public Boolean isInited();
    public Boolean isStarted();
    public Boolean isStopped();
}

interface EmptyContract extends ServiceContract {}

public class IntegrationTest {
    @Test
    public void simpleServiceWithConfigTest() {
        JsonObject config = Json.parse("{\"my_special_int\": 42}").asObject();

        String[] services = {IntegrationTestService.class.getName()};
        ServiceManager serviceManager = new ServiceManager(Arrays.asList(services), config);

        PointlessServiceContract pointlessService = (PointlessServiceContract) serviceManager.getServiceForContract(PointlessServiceContract.class);

        assertFalse(pointlessService.isInited());
        assertFalse(pointlessService.isStarted());
        assertFalse(pointlessService.isStopped());

        serviceManager.startServices();

        assertTrue(pointlessService.isInited());
        assertTrue(pointlessService.isStarted());
        assertFalse(pointlessService.isStopped());

        // Test that the config service is integrated properly and that
        // service functions can be called.
        assertEquals(42, pointlessService.intFromConfig());

        serviceManager.stopServices();

        assertTrue(pointlessService.isStopped());
    }

    @Test
    public void requestShutdownTest() {
        // This test starts a service that spawns a thread that immediately requests a shutdown
        String[] services = {ShutdownRequestTestService.class.getName()};
        ServiceManager serviceManager = new ServiceManager(Arrays.asList(services), null);

        try {
            String shutdownReason = serviceManager.runUntilShutdown();
            assertEquals("I feel like it", shutdownReason);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            fail();
        }
    }
}
