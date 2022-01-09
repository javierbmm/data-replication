package transactions;

public class Transaction {
    private final String destination, value;
    private final Type type;
    public enum Type { READ, WRITE };

    public Transaction(String destination, String value, Type type) {
        this.destination = destination;
        this.value = value;
        this.type = type;
    }

    public String getDestination() {
        return destination;
    }

    public String getValue() {
        return value;
    }

    public Type getType() {
        return type;
    }

    public int getTypeInt() {
        return type.ordinal();
    }
}
