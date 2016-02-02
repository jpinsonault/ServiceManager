package servicemanager.core;

import org.junit.Before;
import org.junit.Test;
import servicemanager.annotations.Dependencies;
import servicemanager.annotations.Implements;

import static org.junit.Assert.*;

interface FirstTestServiceContract extends ServiceContract {}
interface SecondTestServiceContract extends ServiceContract {}

public class ServiceTest {

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void testContractSuccess() throws Exception {
        @Implements(contract = FirstTestServiceContract.class)
        class TestService extends Service {}

        assertEquals(new TestService().contract(), FirstTestServiceContract.class);
    }

    @Test(expected=RuntimeException.class)
    public void testContractMissingImplementsFailure() throws Exception {
        class TestService extends Service {}

        new TestService().contract();
    }

    @Test
    public void testDependenciesSuccess() throws Exception {
        @Implements(contract = FirstTestServiceContract.class)
        @Dependencies(services = {FirstTestServiceContract.class,
                SecondTestServiceContract.class})
        class TestService extends Service {}

        Class[] expectedServices = {FirstTestServiceContract.class, SecondTestServiceContract.class};

        assertArrayEquals(new TestService().dependencies(), expectedServices);
    }

    @Test(expected=RuntimeException.class)
    public void testDependenciesMissingDependenciesFailure() throws Exception {
        @Implements(contract = FirstTestServiceContract.class)
        class TestService extends Service {}

        new TestService().dependencies();
    }

    @Test
    public void testEnsureAnnotatedReturnsGoodException() throws Exception {
        class TestService extends Service {}

        try {
            TestService service = new TestService();
            service.dependencies();

        } catch(RuntimeException e) {
            assertEquals("Missing annotation 'Dependencies' on class 'TestService'",
                         e.getMessage());
        }
    }
}
