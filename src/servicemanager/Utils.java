package servicemanager;


import javax.swing.tree.DefaultMutableTreeNode;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Optional;
import java.util.function.Predicate;

import servicemanager.tree.DepthFirstIterable;
import servicemanager.tree.TreeNode;

public class Utils {
    public static <T> T firstMatch(Collection<T> collection, Predicate<T> predicate){
        Optional<T> result = collection.stream().filter(predicate).findFirst();

        if (result.isPresent()) {
            return result.get();
        }
        else {
            return null;
        }
    }
}
