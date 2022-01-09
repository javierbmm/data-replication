package test.Layer0;

import nodes.ANode;
import utils.Constants.*;

import static utils.Constants.L0.NUM_NODES;

public class A2 {
    public static void main(String[] args) {
        ANode node = new ANode("A2", L0.PORT.A2, NUM_NODES);
        // Adding neighbour nodes
        node.connectNode(L0.PORT.A1, "A1");
        node.connectNode(L0.PORT.A3, "A3");
        // Adding backups
        node.connectBackup(L1.PORT.B1, L1.ID.B1);

        node.start();
    }
}
