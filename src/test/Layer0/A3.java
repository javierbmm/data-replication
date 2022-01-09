package test.Layer0;

import nodes.ANode;
import utils.Constants;
import utils.Constants.L0.*;
import static utils.Constants.L0.NUM_NODES;

public class A3 {
    public static void main(String[] args) {
        ANode node = new ANode("A3", PORT.A3, NUM_NODES);
        // Adding neighbour nodes
        node.connectNode(PORT.A1, "A1");
        node.connectNode(PORT.A2, "A2");
        // Adding backups
        node.connectBackup(Constants.L1.PORT.B2, Constants.L1.ID.B2);

        node.start();
    }
}
