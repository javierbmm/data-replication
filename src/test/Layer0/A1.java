package test.Layer0;

import nodes.ANode;
import utils.Constants.*;

import static utils.Constants.L0.NUM_NODES;

public class A1 {
    static int value = 39, target = 7;

    public static void main(String[] args) {
        ANode node = new ANode("A1", L0.PORT.A1, NUM_NODES);
        // Adding neighbour nodes
        node.connectNode(L0.PORT.A2, "A2");
        node.connectNode(L0.PORT.A3, "A3");

        node.start();
    }
}
