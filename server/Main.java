package server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import java.util.ArrayList;
import java.util.Date;

import java.util.Hashtable;
import java.util.List;

import static client.ExampleGUI.getIPAddress;

public class Main {

    public static void main(String[] args) throws Exception {
        System.out.println("Hello World!");
        Hashtable<String,List<someFile>> SharedFiles = new Hashtable<String, List<someFile>>();
        Hashtable<String, Score> scores = new Hashtable<>();
        int          serverPort   = 8080;
        ServerSocket serverSocket = new ServerSocket(8080);

        System.out.println("this is my of server " + getIPAddress());
        while(true) {
            Socket clientSocket = null;
            try {
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                System.out.println("Error accepting client.");
            }
            new Thread(new WorkerRunnable(clientSocket, SharedFiles, scores)).start();
        }



    }
}
