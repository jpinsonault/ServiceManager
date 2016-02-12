package io.einhard.servicemanager.core;

import io.einhard.servicemanager.annotations.Dependencies;
import io.einhard.servicemanager.annotations.Implements;
import org.junit.Test;

import static org.junit.Assert.*;

interface FirstTestServiceContract extends ServiceContract {}
interface SecondTestServiceContract extends ServiceContract {}
interface ThirdTestServiceContract extends ServiceContract {}
interface FourthTestServiceContract extends ServiceContract {}
interface FifthTestServiceContract extends ServiceContract {}

public class ServiceTest {
    @Test
    public void testContractSuccess() {
        @Implements(contract = FirstTestServiceContract.class)
        class TestService extends Service {
            public TestService(ServiceManager serviceManager) {
                super(serviceManager);
            }
        }

        assertEquals(new TestService(null).contract(), FirstTestServiceContract.class);
    }

    @Test(expected=RuntimeException.class)
    public void testContractMissingImplementsFailure() {
        class TestService extends Service {
            public TestService(ServiceManager serviceManager) {
                super(serviceManager);
            }
        }

        new TestService(null).contract();
    }

    @Test
    public void testDependenciesSuccess() {
        @Implements(contract = FirstTestServiceContract.class)
        @Dependencies(services = {FirstTestServiceContract.class,
                SecondTestServiceContract.class})
        class TestService extends Service {
            public TestService(ServiceManager serviceManager) {
                super(serviceManager);
            }
        }

        Class[] expectedServices = {FirstTestServiceContract.class, SecondTestServiceContract.class};

        assertArrayEquals(new TestService(null).dependencies(), expectedServices);
    }

    @Test
    public void testNoDependenciesReturnsNull() {
        @Implements(contract = FirstTestServiceContract.class)
        class TestService extends Service {
            public TestService(ServiceManager serviceManager) {
                super(serviceManager);
            }
        }

        assertNull(new TestService(null).dependencies());
    }
}
