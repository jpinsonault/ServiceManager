package io.einhard.servicemanager.core;

import io.einhard.servicemanager.annotations.Dependencies;
import io.einhard.servicemanager.annotations.Implements;
import org.junit.Test;
import io.einhard.servicemanager.tree.TreeNode;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class DependencyTreeTest{
    @Implements(contract = FifthTestServiceContract.class)
    @Dependencies(services = {ThirdTestServiceContract.class})
    class TestService5 extends Service {
        public TestService5(ServiceManager serviceManager) {
            super(serviceManager);
        }
    }

    @Implements(contract = FourthTestServiceContract.class)
    class TestService4 extends Service {
        public TestService4(ServiceManager serviceManager) {
            super(serviceManager);
        }
    }

    @Implements(contract = ThirdTestServiceContract.class)
    @Dependencies(services = {FourthTestServiceContract.class})
    class TestService3 extends Service {
        public TestService3(ServiceManager serviceManager) {
            super(serviceManager);
        }
    }

    @Implements(contract = SecondTestServiceContract.class)
    class TestService2 extends Service {
        public TestService2(ServiceManager serviceManager) {
            super(serviceManager);
        }
    }

    @Implements(contract = FirstTestServiceContract.class)
    @Dependencies(services = {SecondTestServiceContract.class, ThirdTestServiceContract.class})
    class TestService1 extends Service {
        public TestService1(ServiceManager serviceManager) {
            super(serviceManager);
        }
    }

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

        Service service1 = new TestService1(null);
        Service service2 = new TestService2(null);
        Service service3 = new TestService3(null);
        Service service4 = new TestService4(null);

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

        Service service1 = new TestService1(null);
        Service service2 = new TestService2(null);
        Service service3 = new TestService3(null);
        Service service4 = new TestService4(null);
        Service service5 = new TestService5(null);

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

    @Test
    public void duplicateContractsTest() {
        @Implements(contract = SecondTestServiceContract.class)
        class TestService2Duplicate extends Service {
            public TestService2Duplicate(ServiceManager serviceManager) {
                super(serviceManager);
            }
        }

        Service service2Duplicate = new TestService2Duplicate(null);
        Service service2 = new TestService2(null);

        Service[] services = {
                service2Duplicate,
                service2
        };

        try{
            ServiceManager.checkForDuplicateContractImplementations(asList(services));
            fail("Should not reach this point");
        } catch(IllegalStateException e){
            assertEquals(
                    "Two services implement the same contract: SecondTestServiceContract",
                    e.getMessage());
        }
    }
}
