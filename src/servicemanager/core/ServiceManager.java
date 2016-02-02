package servicemanager.core;

import com.google.gson.Gson;
import com.sun.org.apache.xpath.internal.operations.Bool;
import com.sun.tools.internal.ws.wsdl.document.jaxws.Exception;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.lang.String.format;

public class ServiceManager {
    List<String> classList;
    Map<String, Service> services;

    public ServiceManager(List<String> classList){
        this.classList = classList;
    }

    public void startServices() {
        loadAndValidateServices(classList);
        createDependencyTree(services);
    }

    private void createDependencyTree(Map<String, Service> services) {
        // Store what nodes have already been calculated
        Set<DefaultMutableTreeNode> calculatedNodes = new HashSet<DefaultMutableTreeNode>();

        // For each service, calculate its dependency tree
        for (Map.Entry<String, Service> entry : services.entrySet()){
            String serviceName = entry.getKey();
            Service service = entry.getValue();
        }
    }

    private static DefaultMutableTreeNode treeNode(){
        return new DefaultMutableTreeNode();
    }

    public void loadAndValidateServices(List<String> classList){
        /* Ensures:
           * all the classes are in the classpath
           * only one implementation per service contract
        */

        for (String className : classList){
            Service newService;
            try {
                newService = instantiateClass(className);
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

            if (services.containsKey(className)){
                String error = format("Tried to load class twice: %s", className);
                throw new IllegalArgumentException(error);
            }
            services.put(className, newService);
        }
    }

    public static Service instantiateClass(String className) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        return (Service) Class.forName(className).newInstance();
    }

    public Service getService(String serviceName){
        return services.get(serviceName);
    }

    public static Boolean classExists(String className){
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }


}
