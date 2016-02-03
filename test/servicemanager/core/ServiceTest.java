package servicemanager.core;

import org.junit.Before;
import org.junit.Test;
import servicemanager.annotations.Dependencies;
import servicemanager.annotations.Implements;

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
        class TestService extends Service {}

        assertEquals(new TestService().contract(), FirstTestServiceContract.class);
    }

    @Test(expected=RuntimeException.class)
    public void testContractMissingImplementsFailure() {
        class TestService extends Service {}

        new TestService().contract();
    }

    @Test
    public void testDependenciesSuccess() {
        @Implements(contract = FirstTestServiceContract.class)
        @Dependencies(services = {FirstTestServiceContract.class,
                SecondTestServiceContract.class})
        class TestService extends Service {}

        Class[] expectedServices = {FirstTestServiceContract.class, SecondTestServiceContract.class};

        assertArrayEquals(new TestService().dependencies(), expectedServices);
    }

    @Test
    public void testNoDependenciesReturnsNull() {
        @Implements(contract = FirstTestServiceContract.class)
        class TestService extends Service {}

        assertNull(new TestService().dependencies());
    }
}
