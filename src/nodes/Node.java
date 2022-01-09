package nodes;

import comms.Chatter;
import comms.Dataframe;
import utils.Logger;
import utils.Parser;

import java.util.Arrays;
import java.util.HashMap;

public abstract class Node {
    protected final int port, numNodes;
    protected int updateCounter = 0;
    protected final String id;
    protected HashMap<String, Chatter>
                nodes = new HashMap<String, Chatter>(), // Mapping node names to port number.
                outConns = new HashMap<String, Chatter>(), // Out connections for some control operations
                backups = new HashMap<String, Chatter>(); // Backup nodes comms

    // protected final Chatter inChatter;
    protected final Logger logger;
    protected int[] data =  {0,0,0,0,0,0,0,0,0,0}; // Fixed data size of 10 values.
    protected Boolean running = false;

    public Node(String id, int port, int numNodes) {
        this.id = id;
        this.port = port;
        this.numNodes = numNodes;
        this.logger = new Logger(id);

//        ServerSocket socket = null;
//        try {
//            socket = new ServerSocket(port);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        this.inChatter = new Chatter(socket);
    }

    protected void replicate(String data) {
        broadcast(Dataframe.REPLICATE, data);
    }

    private Chatter _connect(int port, String id) {
        Chatter chatter = new Chatter();
        chatter.connectTo(null, port);
        chatter.send(this.id); // Greetings message with ID.

        return chatter;
    }

    public void connectNode(int port, String id) {
        Chatter chatter = _connect(port, id);
        nodes.put(id, chatter);
    }

    public void connectBackup(int port, String id) {
        Chatter chatter = _connect(port, id);
        backups.put(id, chatter);
    }

    protected void broadcast(String operation, String message) {
        broadcastTo(nodes, operation, message);
    }

    protected void broadcastTo(HashMap<String, Chatter> group, String operation, String message) {
        if(group.isEmpty())
            return;

        Dataframe df = new Dataframe()
                .source(id)
                .operation(operation)
                .message(message)
                .destination(Dataframe.BROADCAST);

        for (Chatter node : group.values()) {
            node.send(df.toString());
        }
    }

    protected void update(int[] updatedData) {
        if (!Arrays.equals(this.data, updatedData))
            this.data = updatedData;

        logger.write(Arrays.toString(this.data));
    }

    protected void updateTarget(int target, int value) {
        data[target] = value;
        logger.write(Arrays.toString(data));
    }

    protected void read(Dataframe dataframe) {
        // Parsing target from message
        int target = Parser.valuesFromString(dataframe.getMessage())[0];
        // Getting value from data
        int value = data[target];
        String data = Integer.toString(value);
        // Generating a response dataframe
        String message = Dataframe
                .parse(id, Dataframe.READ, data, dataframe.getSrc())
                .toString();
        // Obtaining source Chatter and sending response message
        outConns.get(dataframe.getSrc()).send(message);
    }

    protected void lazyUpdate() {
        if(++updateCounter > 9) {
            updateCounter = 0;
            broadcastTo(backups, Dataframe.UPDATE, Arrays.toString(data));
        }
    }

    protected void setRunning() {
        running = true;
    }

    abstract void write(int target, int value);

}
