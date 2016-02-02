package servicemanager.core;

import org.junit.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

public class ServiceManagerTest {
    @Test
    public void serviceManagerSetsClassList() {
        List<String> classes = asList("one", "two", "three");

        ServiceManager manager = new ServiceManager(classes);

        assertEquals(classes, manager.classList);
    }

    @Test(expected=IllegalArgumentException.class)
    public void serviceManagerThrowsOnMissingClassName() {
        List<String> classes = asList("one", "two", "three");

        ServiceManager manager = new ServiceManager(classes);

        manager.startServices();
    }

    @Test
    public void serviceManagerDoesntThrowOnRealClass() {
        List<String> classes = asList("java.util.List");

        ServiceManager manager = new ServiceManager(classes);

        manager.startServices();

        assertEquals(classes, manager.classList);
    }
}
