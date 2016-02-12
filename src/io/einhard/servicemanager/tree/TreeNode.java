package io.einhard.servicemanager.tree;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.Enumeration;
import java.util.Iterator;

public class TreeNode extends DefaultMutableTreeNode {

    class EnumerationIterable implements Iterator<TreeNode>, Iterable<TreeNode>
    {
        Enumeration mEnumeration;

        public EnumerationIterable(Enumeration enumeration){
            this.mEnumeration = enumeration;
        }

        @Override
        public boolean hasNext() {
            return mEnumeration.hasMoreElements();
        }

        public TreeNode next() {
            return (TreeNode) mEnumeration.nextElement();
        }

        @Override
        public Iterator<TreeNode> iterator() {
            return this;
        }
    }

    public TreeNode(Object userObject){
        super(userObject);
    }

    public Iterable<TreeNode> depthFirstIterable(){
        return new EnumerationIterable(depthFirstEnumeration());
    }

    public Iterable<TreeNode> breadthFirstIterable(){
        return new EnumerationIterable(breadthFirstEnumeration());
    }

    public Boolean contains(Object value){
        for (TreeNode node : this.depthFirstIterable()){
            if (node.getUserObject() == value){
                return true;
            }
        }

        return false;
    }

    public TreeNode find(Object value){
        for (TreeNode node : this.depthFirstIterable()){
            if (node.getUserObject() == value){
                return node;
            }
        }

        return null;
    }

    public Iterable<TreeNode> childrenIterable() {
        return new EnumerationIterable(children());
    }
}

