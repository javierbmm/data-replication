package test.Layer2;

import nodes.BNode;
import nodes.CNode;
import utils.Constants;
import utils.Constants.L2;

import static utils.Constants.L2.NUM_NODES;


public class C1 {
    public static void main(String[] args) {
        final int
                PARENT_BACKUPS  = 1,
                TOTAL_NODES     = NUM_NODES + PARENT_BACKUPS;

        CNode node = new CNode(L2.ID.C1, L2.PORT.C1, TOTAL_NODES);
        // Adding neighbour nodes
        node.connectNode(L2.PORT.C2, L2.ID.C2);

        node.start();
    }
}
