package comms;

/* Class to define and operate messages sent through sockets */
public class Dataframe {
    public final static String BROADCAST = "-1";
    public final static String // operations
            CLOSE = "close",
            ACK = "ack",
            WRITE = "write",
            READ = "read",
            REPLICATE = "replicate",
            UPDATE = "update",
            STOP = "stop",
            DONE = "done";
    private String // Package information: [src;operation;message;dest]
            src,
            op,
            message,
            dest;
    private static final String
            INVALID_FRAME = "ERROR. Invalid dataframe <%s>. It must have 4 semicolon separated values.\n";

    public Dataframe(String src, String op, String message, String dest) {
        this.src = src;
        this.op = op;
        this.message = message;
        this.dest = dest;
    }

    public Dataframe() { }

    public Dataframe(String frame) {
        assert frame != null;
        String[] data = frame.split(";");
        if(data.length != 4)
            throw new RuntimeException(String.format(INVALID_FRAME, frame));

        this.src = data[0];
        this.op = data[1];
        this.message = data[2];
        this.dest = data[3];
    }

    public static Dataframe parse(String src, String operation, String message, String dest) {
        return new Dataframe(src, operation, message, dest);
    }

    public String toString() {
        // Comma separated values
        return src + ";" + op + ";" + message + ";" + dest;
    }

    public String getDest() {
        return dest;
    }

    public Dataframe destination(String dest) {
        this.dest = dest;
        return this;
    }

    public String getSrc() {
        return src;
    }

    public Dataframe source(String src) {
        this.src = src;
        return this;
    }

    public String getOp() {
        return op;
    }

    public Dataframe operation(String operation) {
        this.op = operation;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public Dataframe message(String message) {
        this.message = message;
        return this;
    }
}
