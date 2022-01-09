package nodes;

import comms.Chatter;
import comms.Dataframe;
import comms.DataframeFactory;
import utils.Parser;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.*;

// Update everywhere, active, and eager replication to replicate data
public class ANode extends Node {
    private volatile int acknowledges = 0;
    private volatile Queue<Dataframe> queue = new LinkedList<Dataframe>();

    public ANode(String id, int port, int numNodes) {
        super(id, port, numNodes);
        new Thread(this::initOutConn).start();
    }

    @Override
    public void write(int target, int value) {
        // 1. Send request package with updated/new values to other nodes within the same layer (Update everywhere,
        //    active replication)
        acknowledges = 0;
        replicate(target + "," + value);
        // 2. Wait for other nodes to update their replica values send their acknowledge (eager replication).
        while(acknowledges < nodes.size())
            ;
        // 3. Write the values on your replica.
        updateTarget(target, value);
        // 4. Send acknowledge to let the other nodes know that you've finish, and they can process incoming requests.
        broadcast(Dataframe.ACK, "");
    }

    public void start() {
        while(true) {
            if (queue.isEmpty())
                continue;
            else {
                handle(Objects.requireNonNull(queue.poll()));
            }
        }
    }

    void handle(Dataframe dataframe) {
        switch(dataframe.getOp()) {
            case Dataframe.WRITE -> {
                int[] values = Parser.valuesFromString(dataframe.getMessage());
                if(values.length < 2) System.err.println("Incorrect message. At least two values (value, target)");
                else write(values[0], values[1]);

                String ackMessage = DataframeFactory.ackDataframe(id, dataframe.getSrc()).toString();
                lazyUpdate();
                outConns.get(dataframe.getSrc()).send(ackMessage);
            }

            case Dataframe.REPLICATE -> {
                int[] values = Parser.valuesFromString(dataframe.getMessage());
                if(values.length < 2) System.err.println("Incorrect message. At least two values (value, target)");
                else updateTarget(values[0], values[1]);
                acknowledges = 0;
                String ackMessage = DataframeFactory.ackDataframe(id, dataframe.getSrc()).toString();
                nodes.get(dataframe.getSrc()).send(ackMessage);

                while(acknowledges <= 0)
                    ; // Blocking handler to not perform any action until replication is completed on other nodes
                lazyUpdate();
            }

            case Dataframe.READ -> read(dataframe);

            /* TODO:
             case Dataframe.STOP ->
             */
        }

    }

    // Function to open connections for every node
    void initOutConn() {
        ServerSocket serverSocket = null;
        ArrayList<inThread> threads = new ArrayList<inThread>();

        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }

        for(int i=0; i<numNodes; i++) {
            Chatter chatter = new Chatter(serverSocket);
            chatter.openConnection();
            String id = chatter.read();
            //System.out.println("[READ]: "+ id);
            outConns.put(id, chatter);
            threads.add(new inThread(chatter));
            String ackMsg = DataframeFactory.ackDataframe(this.id, id).toString();
            chatter.send(ackMsg);
        }

        for(inThread thread : threads)
            thread.start();
        setRunning();
    }

    synchronized void updateAcknowledges() {
        acknowledges++;
    }

    private class inThread extends Thread {
        private final Chatter clientChatter;

        public inThread(Chatter clientChatter) {
            this.clientChatter = clientChatter;
        }

        @Override
        public void run() {
            Dataframe df;
            String line = "";
            while(true) {
                if(!running)
                    continue;
                line = clientChatter.read();
                if(line == null || line.equals("CLIENT"))
                    continue;
                df = new Dataframe(line);
                System.out.println("[READ]: "+line);
                if (Dataframe.ACK.equals(df.getOp())) {
                    updateAcknowledges();
                } else {
                    queue.add(df);
                }
            }
        }
    }
}
