package servicemanager.core;

import org.junit.Test;
import servicemanager.annotations.Implements;

import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@Implements(contract = FirstTestServiceContract.class)
class TestService extends Service {}

public class ServiceManagerTest {
    @Test
    public void serviceManagerSetsClassList() {
        List<String> classes = asList("one", "two", "three");

        ServiceManager manager = new ServiceManager(classes);

        assertEquals(classes, manager.mServiceList);
    }

    @Test
    public void instantiateServiceThrowsOnMissingClassName() {
        String className = "NotAService";

        try {
            ServiceManager.instantiateService(className);
            fail("Should not reach this point");
        } catch (Exception e) {
            assert(e instanceof ClassNotFoundException);
        }
    }

    @Test
    public void instantiateServiceDoesntThrowOnRealClass() {
        String className = "servicemanager.core.TestService";

        Service service = null;
        try {
            service = ServiceManager.instantiateService(className);
            fail("Should not reach this point");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Couldn't find Service");
        }

        assertEquals(TestService.class, service.getClass());
    }

    @Test
    public void testGetServiceForContract() {
        @Implements(contract = FirstTestServiceContract.class)
        class TestService1 extends Service {}
        @Implements(contract = SecondTestServiceContract.class)
        class TestService2 extends Service {}

        TestService1 service1 = new TestService1();
        TestService2 service2 = new TestService2();

        List<Service> services = asList(service1, service2);

        assertEquals(service1, ServiceManager.getServiceForContract(services, FirstTestServiceContract.class));
    }
}

