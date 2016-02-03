package servicemanager.tree;

import java.util.Enumeration;
import java.util.Iterator;

public class DepthFirstIterable implements Iterable<TreeNode>, Iterator<TreeNode> {
    Enumeration mTreeEnumeration;

    public DepthFirstIterable(TreeNode root){
        this.mTreeEnumeration = root.depthFirstEnumeration();
    }

    @Override
    public boolean hasNext() {
        return mTreeEnumeration.hasMoreElements();
    }

    public TreeNode next() {
        return (TreeNode) mTreeEnumeration.nextElement();
    }

    @Override
    public Iterator<TreeNode> iterator() {
        return this;
    }
}
