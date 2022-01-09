package transactions;
import comms.Chatter;
import comms.Dataframe;
import utils.Constants.L0;
import utils.Constants.L1;
import utils.Constants.L2;
import static transactions.Transaction.Type.READ;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;


public class ClientManager {
    private static final HashMap<String, Chatter> nodes = new HashMap<String, Chatter>();
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_GREEN = "\u001B[32m";
    private static final String READ_MESSAGE = ANSI_GREEN+ "\n[RESULT] Index %s from layer %s is %s \n" + ANSI_RESET;

    public static void connectToL0() {
        connectToNode(L0.PORT.A1, L0.ID.A1);

        connectToNode(L0.PORT.A2, L0.ID.A2);

        connectToNode(L0.PORT.A3, L0.ID.A3);
    }

    public static void connectToL1() {
        connectToNode(L1.PORT.B1, L1.ID.B1);

        connectToNode(L1.PORT.B2, L1.ID.B2);
    }

    public static void connectToL2() {
        connectToNode(L2.PORT.C1, L2.ID.C1);

        connectToNode(L2.PORT.C2, L2.ID.C2);
    }

    private static void connectToNode(int port, String id) {
        Chatter chatter = new Chatter();
        chatter.connectTo(null, port);
        chatter.send("CLIENT");
        chatter.read();
        nodes.put(id, chatter);
    }

    public static void sendTransactions(ArrayList<Transaction> transactions) throws InterruptedException {
        for(Transaction transaction : transactions) {
            String operation = transaction.getType() == READ? Dataframe.READ : Dataframe.WRITE;
            String nodeDest = "";
            Dataframe df = new Dataframe()
                    .source("CLIENT")
                    .operation(operation)
                    .message(transaction.getValue());

            if(transaction.getType() == READ) {
                String dest = transaction.getDestination();
                if(dest.equals("1")) {
                    int index = getRandomNumber(1, 2);
                    nodeDest = "B" + index;
                } else if(dest.equals("2")) {
                    int index = getRandomNumber(1, 2);
                    nodeDest = "C" + index;
                } else // Layer 0
                    nodeDest = getRandomNode();

                df.destination(nodeDest);
                System.out.println("Destination: " + nodeDest);
                nodes.get(nodeDest).send(df.toString());
                String result = new Dataframe(nodes.get(nodeDest).read()).getMessage();
                System.out.printf((READ_MESSAGE) + "%n", transaction.getValue(), transaction.getDestination(), result);
            } else {
                nodeDest = getRandomNode();
                df.destination(nodeDest);
                System.out.println("Destination: " + nodeDest);
                nodes.get(nodeDest).send(df.toString());
                waitAck(nodes.get(nodeDest));
            }
        }
    }

    public static String getRandomNode() {
        int index = (int)(Math.random() * L0.NUM_NODES-1) + 1;

        return "A"+index;
    }

    /**
     * Get a random number between two numbers
     * @param from - Bottom limit (inclusive)
     * @param to - Upper limit (inclusive
     * @return random number between from and to
     */
    public static int getRandomNumber(int from, int to) {
        Random r = new Random();
        to++;
        return r.nextInt(to-from) + from;
    }

    public static void close() {
        for(Chatter node : nodes.values())
            node.stop();
    }

    public static void waitAck(Chatter chatter) {
        while (true) {
            String line = chatter.read();
            Dataframe df = new Dataframe(line);
            if(df.getOp().equals(Dataframe.ACK))
                break;
        }
    }
}
