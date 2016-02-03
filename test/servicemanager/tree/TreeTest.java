package servicemanager.tree;

import org.junit.Test;
import static org.junit.Assert.*;

public class TreeTest {
    @Test
    public void testTreeFindWorks() {
        TreeNode node1 = new TreeNode("one");
        TreeNode node1Child = new TreeNode("underNode1");
        TreeNode node1ChildChild = new TreeNode("underNode1Child");
        TreeNode node2 = new TreeNode("two");

        node1.add(node1Child);
        node1.add(node2);
        node1Child.add(node1ChildChild);

        assertEquals(node2, node1.find("two"));
        assertEquals(node1Child, node1.find("underNode1"));
        assertEquals(node1ChildChild, node1.find("underNode1Child"));

        assertNull(node1.find("bob"));
        assertNull(node1Child.find("one"));
    }


}

