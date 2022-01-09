package test;

import transactions.Interpreter;
import transactions.Transaction;
import transactions.ClientManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class InterpreterTest {
    final static String FILEPATH = "resources/transactions.txt";

    public static void main(String[] args) {

        ArrayList<Transaction> transactions;
        try {
            File file = new File(FILEPATH);
            Scanner scanner = new Scanner(file);
            ClientManager.connectToL0();
            ClientManager.connectToL1();
            ClientManager.connectToL2();
            while (scanner.hasNextLine()) {
                String data = scanner.nextLine();
                System.out.println(data);
                transactions = Interpreter.parse(data);
                ClientManager.sendTransactions(transactions);
            }
            scanner.close();
            ClientManager.close();
        } catch (FileNotFoundException | InterruptedException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}
