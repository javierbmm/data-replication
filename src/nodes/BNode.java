package nodes;

import comms.Chatter;
import comms.Dataframe;
import comms.DataframeFactory;
import utils.Parser;

import java.io.IOException;
import java.net.ServerSocket;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

// Update everywhere, active, and eager replication to replicate data
public class BNode extends Node {
    private volatile int acknowledges = 0;
    private volatile Queue<Dataframe> queue = new LinkedList<Dataframe>();
    private Instant start, end;

    public BNode(String id, int port, int numNodes) {
        super(id, port, numNodes);
        new Thread(this::initOutConn).start();
    }

    @Override
    public void write(int target, int value) {
        // Nothing. Only write in Nodes A.
        System.err.println("Unused method. No writing in allowed in this node.");
    }

    public void start() {
        start = Instant.now();
        while(true) {
            if (!queue.isEmpty())
                handle(Objects.requireNonNull(queue.poll()));

            lazyUpdate(); // every 10 secs
        }
    }

    @Override
    protected void lazyUpdate() {
        Duration timeElapsed;
        end = Instant.now();
        timeElapsed = Duration.between(start, end);
        if (timeElapsed.getSeconds() >= 10) {
            start = Instant.now();
            broadcastTo(backups, Dataframe.UPDATE, Arrays.toString(data));
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
                if (Dataframe.ACK.equals(df.getOp()))
                    continue;
                queue.add(df);
            }
        }
    }
}
