package transactions;
import transactions.Transaction.Type;

import java.util.ArrayList;

public class Interpreter {


    public static ArrayList<Transaction> parse(String text) {
        ArrayList<Transaction> transactions = new ArrayList<Transaction>();
        String dest = "", op = "";
        Type type;
        String[] commands = text.split(", ");

        // getting first to see if readonly or write
        if(!commands[0].equals("b")) {
            dest = extractData(commands[0], "<", ">");
        }

        for(int i=1; i<commands.length; i++) {
            if(commands[i].contains("c"))
                break;

            if(commands[i].startsWith("r")) // Read transaction
                type = Type.READ;
             else  // Write transaction
                type = Type.WRITE;

            op = extractData(commands[i], "(", ")");

            transactions.add(new Transaction(dest, op, type));
        }

        return transactions;
    }

    /**
     * Extract data between delimiters "openDelimiter" and "closeDelimiter"
     * @param text - String text
     * @param openDelimiter - Opening delimiter
     * @param closeDelimiter - Closing delimiter
     * @return Data extracted as string
     */
    private static String extractData(String text, String openDelimiter, String closeDelimiter) {
        return text.substring(text.indexOf(openDelimiter) + 1, text.indexOf(closeDelimiter));
    }
}
