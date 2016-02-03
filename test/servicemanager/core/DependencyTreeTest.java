package servicemanager.core;

import org.junit.Test;
import servicemanager.annotations.Dependencies;
import servicemanager.annotations.Implements;
import servicemanager.tree.TreeNode;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

public class DependencyTreeTest{
    @Implements(contract = FifthTestServiceContract.class)
    @Dependencies(services = {ThirdTestServiceContract.class})
    class TestService5 extends Service {}

    @Implements(contract = FourthTestServiceContract.class)
    class TestService4 extends Service {}

    @Implements(contract = ThirdTestServiceContract.class)
    @Dependencies(services = {FourthTestServiceContract.class})
    class TestService3 extends Service {}

    @Implements(contract = SecondTestServiceContract.class)
    class TestService2 extends Service {}

    @Implements(contract = FirstTestServiceContract.class)
    @Dependencies(services = {SecondTestServiceContract.class, ThirdTestServiceContract.class})
    class TestService1 extends Service {}

    @Test
    public void createDependenciesTest() {

    }

    @Test
    public void simpleDependencyTreeTest() {
        // Dependency Tree:
        // - First
        //   - Second
        //   - Third
        //      - Fourth

        Service service1 = new TestService1();
        Service service2 = new TestService2();
        Service service3 = new TestService3();
        Service service4 = new TestService4();

        Service[] services = {
                service1,
                service2,
                service3,
                service4
        };

        TreeNode dependencyTree = ServiceManager.createServiceDependencyTree(asList(services));

        assert(dependencyTree.contains(service1));
        assert(dependencyTree.contains(service2));
        assert(dependencyTree.contains(service3));
        assert(dependencyTree.contains(service4));
    }

    @Test
    public void lessSimpleDependencyTreeTest() {
        // Dependency Tree:
        // - First
        //   - Second
        //   - Third
        //      - Fourth
        // - Fifth
        //   - Third

        Service service1 = new TestService1();
        Service service2 = new TestService2();
        Service service3 = new TestService3();
        Service service4 = new TestService4();
        Service service5 = new TestService5();

        Service[] services = {
                service1,
                service2,
                service3,
                service4,
                service5
        };

        TreeNode dependencyTree = ServiceManager.createServiceDependencyTree(asList(services));

        // Make sure the service5 node only has one child
        TreeNode fifthNode = dependencyTree.find(service5);
        assertEquals(1, fifthNode.getChildCount());
        // And that it is the service3 node
        TreeNode thirdNode = (TreeNode) fifthNode.getFirstChild();
        assertEquals(service3, thirdNode.getUserObject());

        // Make sure the third node contains service4
        assert(thirdNode.contains(service4));
    }
}
