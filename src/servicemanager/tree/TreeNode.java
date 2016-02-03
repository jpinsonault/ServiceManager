package servicemanager.tree;

import javax.swing.tree.DefaultMutableTreeNode;

public class TreeNode extends DefaultMutableTreeNode {

    public TreeNode(Object userObject){
        super(userObject);
    }

    public DepthFirstIterable depthFirstIterator(){
        return new DepthFirstIterable(this);
    }

    public Boolean contains(Object value){
        for (TreeNode node : this.depthFirstIterator()){
            if (node.getUserObject() == value){
                return true;
            }
        }

        return false;
    }

    public TreeNode find(Object value){
        for (TreeNode node : this.depthFirstIterator()){
            if (node.getUserObject() == value){
                return node;
            }
        }

        return null;
    }
}

