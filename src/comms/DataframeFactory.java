package comms;

public class DataframeFactory {

    public static Dataframe ackDataframe(String src, String dest) {
        return new Dataframe()
                .source(src)
                .operation(Dataframe.ACK)
                .message("")
                .destination(dest);
    }

    public static Dataframe updateDataframe(String src, String message, String dest) {
        return new Dataframe()
                .source(src)
                .operation(Dataframe.ACK)
                .message(message)
                .destination(dest);
    }

}
