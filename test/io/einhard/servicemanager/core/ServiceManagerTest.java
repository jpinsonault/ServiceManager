package io.einhard.servicemanager.core;

import io.einhard.servicemanager.annotations.Implements;
import org.junit.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@Implements(contract = FirstTestServiceContract.class)
class TestService extends Service {
    public TestService(ServiceManager serviceManager) {
        super(serviceManager);
    }
}

public class ServiceManagerTest {
    @Test
    public void instantiateServiceThrowsOnMissingClassName() {
        String className = "NotAService";

        try {
            ServiceManager.instantiateService(className, null);
            fail("Should not reach this point");
        } catch (Exception e) {
            assert(e instanceof ClassNotFoundException);
        }
    }

    @Test
    public void instantiateServiceDoesntThrowOnRealClass() {
        String className = "io.einhard.servicemanager.core.TestService";

        Service service = null;
        try {
            service = ServiceManager.instantiateService(className, null);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Couldn't find Service");
        }

        assertEquals(TestService.class, service.getClass());
    }

    @Test
    public void testGetServiceForContract() {
        @Implements(contract = FirstTestServiceContract.class)
        class TestService1 extends Service {
            public TestService1(ServiceManager serviceManager) {
                super(serviceManager);
            }
        }
        @Implements(contract = SecondTestServiceContract.class)
        class TestService2 extends Service {
            public TestService2(ServiceManager serviceManager) {
                super(serviceManager);
            }
        }

        TestService1 service1 = new TestService1(null);
        TestService2 service2 = new TestService2(null);

        List<Service> services = asList(service1, service2);

        assertEquals(service1, ServiceManager.getServiceForContract(services, FirstTestServiceContract.class));
    }
}

