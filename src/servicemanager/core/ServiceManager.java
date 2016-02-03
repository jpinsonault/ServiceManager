package servicemanager.core;

import servicemanager.tree.TreeNode;

import java.util.*;

import static java.lang.String.format;
import static servicemanager.Utils.firstMatch;

public class ServiceManager {
    List<String> mServiceList;
    Set<Service> mServices;
    TreeNode mDependencyTree;

    public ServiceManager(List<String> serviceList){
        this.mServiceList = serviceList;
        loadAndValidateServices(mServiceList);
    }

    public void startServices() {
        mDependencyTree = createServiceDependencyTree(mServices);
        checkForDuplicateContractImplementations(mDependencyTree);

        for (TreeNode topLevelNode : mDependencyTree.childrenIterable()){

        }
    }

    private void startServicesRecur() {

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
        TreeNode rootNode = new TreeNode("root");

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

    public static Service instantiateService(String className) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        return (Service) Class.forName(className).newInstance();
    }
}
