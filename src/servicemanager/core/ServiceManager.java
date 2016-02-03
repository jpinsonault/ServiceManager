package servicemanager.core;

import apple.laf.JRSUIUtils;
import servicemanager.tree.TreeNode;

import java.util.*;

import static java.lang.String.format;
import static servicemanager.Utils.firstMatch;

public class ServiceManager {
    List<String> mServiceList;
    Map<String, Service> mServices;
    TreeNode mDependencyTree;

    public ServiceManager(List<String> serviceList){
        this.mServiceList = serviceList;
    }

    public void startServices() {
        loadAndValidateServices(mServiceList);
        mDependencyTree = createServiceDependencyTree(mServices.values());
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
        /* Ensures:
           * all the classes are in the classpath
           * only one implementation per service contract
        */

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

            if (mServices.containsKey(className)){
                String error = format("Tried to load class twice: %s", className);
                throw new IllegalArgumentException(error);
            }
            mServices.put(className, newService);
        }
    }

    public static Service instantiateService(String className) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        return (Service) Class.forName(className).newInstance();
    }

    public Service getService(String serviceName){
        return mServices.get(serviceName);
    }
}
