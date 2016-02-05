package servicemanager.core;

import servicemanager.tree.TreeNode;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static java.lang.String.format;
import static servicemanager.Utils.firstMatch;

public class ServiceManager {
    List<String> mServiceList;
    Set<Service> mServices;
    TreeNode mDependencyTree;

    public ServiceManager(List<String> serviceList){
        mServiceList = serviceList;
        loadAndValidateServices(mServiceList);

        mDependencyTree = createServiceDependencyTree(mServices);
        checkForDuplicateContractImplementations(mDependencyTree);
    }

    public void initServices(){
        initServices(mDependencyTree);
    }

    public void initServices(TreeNode dependencyTree){
        ArrayList<Service> initedServices = new ArrayList<>();

        for (TreeNode node : mDependencyTree.depthFirstIterable()){
            Service service = (Service) node.getUserObject();
            if (!initedServices.contains(service)){
                service.init();
                initedServices.add(service);
            }
        }
    }

    public void startServices(){
        startServices(mDependencyTree);
    }

    public void startServices(TreeNode dependencyTree) {
        initServices(dependencyTree);

        ArrayList<Service> startedServices = new ArrayList<>();

        for (TreeNode node : dependencyTree.depthFirstIterable()){
            Service service = (Service) node.getUserObject();
            if (!startedServices.contains(service)){
                service.start();
                startedServices.add(service);
            }
        }
    }

    public void stopServices(){
        stopServices(mDependencyTree);
    }

    public void stopServices(TreeNode dependencyTree){
        ArrayList<Service> stoppedServices = new ArrayList<>();

        for (TreeNode node : dependencyTree.breadthFirstIterable()){
            Service service = (Service) node.getUserObject();
            if (!stoppedServices.contains(service)){
                service.stop();
                stoppedServices.add(service);
            }
        }
    }

    static void checkForDuplicateContractImplementations(TreeNode dependencyTree){
        Set<Class<? extends ServiceContract>> contracts = new HashSet<>();

        for (TreeNode node : dependencyTree.depthFirstIterable()){
            Service service = (Service) node.getUserObject();
            Class<? extends ServiceContract> contract = service.contract();

            if (contracts.contains(contract)){
                String error = format("Two services implement the same contract: %s", contract.getSimpleName());
                throw new IllegalStateException(error);
            }
            else{
                contracts.add(contract);
            }
        }
    }

    static TreeNode createServiceDependencyTree(Collection<Service> services) {
        // Make top node an empty service
        TreeNode rootNode = new TreeNode(new Service(null));

        // For each service, calculate its dependency tree
        for (Service service : services){
            rootNode.add(getDependenciesTreeRecur(services, service));
        }

        return rootNode;
    }


    static TreeNode getDependenciesTreeRecur(Collection<Service> services, Service thisService) {
        TreeNode newNode = new TreeNode(thisService);

        Class[] dependencies = thisService.dependencies();
        // Terminating condition
        if (dependencies == null) {
            return newNode;
        }

        for (Class contract : thisService.dependencies()) {
            Service matchingService = getServiceForContract(services, contract);

            if (matchingService == null) {
                throw new IllegalStateException(format("Can't find service for contract %s", contract.getSimpleName()));
            }

            newNode.add(getDependenciesTreeRecur(services, matchingService));
        }

        return newNode;
    }

    public Service getServiceForContract(Class<? extends ServiceContract> contract){
        return getServiceForContract(mServices, contract);
    }

    static Service getServiceForContract(Collection<Service> services, Class<? extends ServiceContract> contract) {
        return firstMatch(services, s -> s.contract() == contract);
    }

    public void loadAndValidateServices(List<String> classList){
        // Ensures all the classes are in the classpath

        for (String className : classList){
            Service newService;
            try {
                newService = instantiateService(className);
            } catch (ClassNotFoundException e) {
                String error = format("Could not find class: %s", className);
                throw new IllegalArgumentException(error);
            } catch (InstantiationException e) {
                String error = format("Could not instantiate class: %s", className);
                throw new IllegalArgumentException(error);
            } catch (IllegalAccessException e) {
                String error = format("Unknown problem loading class: %s", className);
                throw new IllegalArgumentException(error);
            }

            if (mServices.contains(newService)){
                String error = format("Tried to load class twice: %s", className);
                throw new IllegalArgumentException(error);
            }
            mServices.add(newService);
        }
    }

    public Service instantiateService(String className) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        try {
            return (Service) Class.forName(className).getDeclaredConstructor().newInstance(this);
        } catch (InvocationTargetException | NoSuchMethodException e) {
            String error = format("Problem calling constructor on class: %s", className);
            throw new IllegalStateException(error);
        }
    }
}
