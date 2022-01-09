package comms;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Duration;
import java.time.Instant;

public class Chatter {
    private int socket;
    private String ip;
    private final Boolean isServer;
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private boolean debug = false;
    private final static int TIMEOUT = 500000; // Miliseconds
    // Use this constructor to initialize a server-side connection
    public Chatter(ServerSocket serverSocket) {
        isServer = true;
        // this.socket = socket;
        this.serverSocket = serverSocket;
    }

    // Use this constructor to initialize a client-side connection
    public Chatter() {
        isServer = false;
    }

    public void openConnection() {
        assert !isServer;
        try {
            // serverSocket = new ServerSocket(socket);
            clientSocket = serverSocket.accept();
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e) {
            System.err.println("Error opening connection");
            e.printStackTrace();
        }
    }

    public Chatter connectTo(String ip, int port) {
        assert isServer;
        Instant start;
        Instant end;
        Duration timeElapsed;
        start = Instant.now();
        // Try to connect until timout
        while(true) {
            try {
                clientSocket = new Socket(ip, port);
                break;
            } catch (IOException e){
                end = Instant.now();
                timeElapsed = Duration.between(start, end);
                if(timeElapsed.getSeconds() >= TIMEOUT*1000) {
                    System.err.println("Error connecting to "+ip+":"+port);
                    e.printStackTrace();
                    System.exit(-1);
                }
            }
        }

        try {
            socket = port;
            this.ip = ip;
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e) {
            System.err.println("Error connecting to "+ip+":"+port);
            e.printStackTrace();
            System.exit(-1);
        }

        return this;
    }

    public void stop() {
        try {
            if(in!=null) in.close();
            if(out!=null) out.close();
            if(clientSocket!=null) clientSocket.close();
            if(serverSocket!=null) serverSocket.close();
        } catch (IOException e) {
            System.err.println("Error closing connection");
            e.printStackTrace();
        }
    }

    public String read() {
        try {
            return in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void send(String message) {
        //if(debug)
            System.out.println("[SENDING]: " + message);
        out.println(message);
    }

    public Chatter debug(boolean mode) {
        debug = true;
        return this;
    }
}
