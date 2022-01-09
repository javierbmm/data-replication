package test.Layer1;

import nodes.BNode;
import utils.Constants;

import static utils.Constants.L1.NUM_NODES;

public class B1 {
    public static void main(String[] args) {
        final int
                PARENT_BACKUPS  = 1,
                TOTAL_NODES     = NUM_NODES + PARENT_BACKUPS;

        BNode node = new BNode("B1", Constants.L1.PORT.B1, TOTAL_NODES);
        // Adding neighbour nodes
        node.connectNode(Constants.L1.PORT.B2, "B2");

        node.start();
    }
}
