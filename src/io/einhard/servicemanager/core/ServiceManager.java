package io.einhard.servicemanager.core;

import com.eclipsesource.json.JsonObject;
import io.einhard.servicemanager.Utils;
import io.einhard.servicemanager.services.ConfigService;
import io.einhard.servicemanager.tree.TreeNode;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static java.lang.String.format;

public class ServiceManager {
    final JsonObject mConfig;
    Set<Service> mServices;
    TreeNode mDependencyTree;

    public ServiceManager(List<String> serviceList, JsonObject config){
        mServices = new HashSet<>();
        mConfig = config;
        loadAndValidateServices(serviceList);
        checkForDuplicateContractImplementations(mServices);

        mDependencyTree = createServiceDependencyTree(mServices);
    }

    public void initServices(){
        initServices(mDependencyTree);
    }

    public static void initServices(TreeNode dependencyTree){
        ArrayList<Service> initedServices = new ArrayList<>();

        for (TreeNode node : dependencyTree.depthFirstIterable()){
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

    static void checkForDuplicateContractImplementations(Collection<Service> services){
        Set<Class<? extends ServiceContract>> contracts = new HashSet<>();

        for (Service service : services){
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
        return Utils.firstMatch(services, s -> s.contract() == contract);
    }

    public void loadAndValidateServices(List<String> classList){
        // Ensures all the classes are in the classpath

        // The config service is hardcoded to be loaded unconditionally.
        // Not sure if this is good, but it's integral to any other service
        // that can be created, and it needs to be bootstrapped somehow
        ConfigService configService = new ConfigService(this, mConfig);
        mServices.add(configService);

        for (String className : classList){
            Service newService;
            try {
                newService = instantiateService(className, this);
            } catch (ClassNotFoundException e) {
                String error = format("Could not find class: %s", className);
                throw new IllegalArgumentException(error);
            } catch (InstantiationException e) {
                String error = format("Could not instantiate class: %s", className);
                throw new IllegalArgumentException(error);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                String error = format("Unknown problem loading class: %s", className);
                throw new IllegalStateException(error);
            }

            if (mServices.contains(newService)){
                String error = format("Tried to load class twice: %s", className);
                throw new IllegalArgumentException(error);
            }
            mServices.add(newService);
        }
    }

    public static Service instantiateService(String className, ServiceManager serviceManager) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        try {
            return (Service) Class.forName(className).getDeclaredConstructors()[0].newInstance(serviceManager);
        } catch (InvocationTargetException e) {
            String error = format("Problem calling constructor on class: %s", className);
            e.printStackTrace();
            throw new IllegalStateException(error);
        }
    }
}
