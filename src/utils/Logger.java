package utils;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class Logger {
    private String filename;
    private final static String FOLDER = "resources/logs/";

    public Logger(String filename) {
        this.filename = FOLDER + filename + ".txt";
        clean();
    }

    public void write(String log) {
        String timeStamp = "[" + new SimpleDateFormat("HH.mm.ss").format(new Date()) + "]";
        log = timeStamp + ": " + log;

        try(PrintWriter printWriter = new PrintWriter(new FileWriter(filename, true))) {
            printWriter.println(log);
            printWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void clean() {
        try(PrintWriter printWriter = new PrintWriter(new FileWriter(filename))) {
            printWriter.println("Begin");
            printWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
