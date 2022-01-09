package nodes;

import comms.Chatter;
import comms.Dataframe;
import comms.DataframeFactory;
import utils.Parser;

import java.io.IOException;
import java.net.ServerSocket;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;

// Update everywhere, active, and eager replication to replicate data
public class CNode extends Node {
    private volatile int acknowledges = 0;
    private volatile Queue<Dataframe> queue = new LinkedList<Dataframe>();

    public CNode(String id, int port, int numNodes) {
        super(id, port, numNodes);
        new Thread(this::initOutConn).start();
    }

    @Override
    public void write(int target, int value) {
        // Nothing. Only write in Nodes A.
        System.err.println("Unused method. No writing in allowed in this node.");
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
            case Dataframe.UPDATE -> {
                int[] values = Parser.valuesFromString(dataframe.getMessage());
                if(values.length < 10) System.err.println("Incorrect message. At least two values (value, target)");
                else update(values);
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
                line = clientChatter.read();
                if(line == null)
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
