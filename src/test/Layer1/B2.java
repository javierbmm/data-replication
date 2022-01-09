package test.Layer1;

import nodes.BNode;
import utils.Constants;
import utils.Constants.L1;
import utils.Constants.L2;

import static utils.Constants.L1.NUM_NODES;

public class B2 {
    public static void main(String[] args) {
        final int
                PARENT_BACKUPS  = 1,
                TOTAL_NODES     = NUM_NODES + PARENT_BACKUPS;

        BNode node = new BNode(L1.ID.B2, L1.PORT.B2, TOTAL_NODES);
        // Adding neighbour nodes
        node.connectNode(L1.PORT.B1, L1.ID.B1);
        // Adding backups
        node.connectBackup(L2.PORT.C1, L2.ID.C1);
        node.connectBackup(L2.PORT.C2, L2.ID.C2);

        node.start();
    }
}
