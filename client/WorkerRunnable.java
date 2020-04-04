package client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

import static java.lang.Integer.parseInt;

public class WorkerRunnable implements Runnable {
    
    protected Socket cClientSocket;
    DataOutputStream outToClient;
    BufferedReader inFromClient;
    ServerSocket serverSocket;

    public WorkerRunnable(Socket cClientSocket, ServerSocket serverSocket) throws Exception
    {
        this.cClientSocket = cClientSocket;
        outToClient = new DataOutputStream(cClientSocket.getOutputStream());
        inFromClient = new BufferedReader(new InputStreamReader(cClientSocket.getInputStream()));
    }
    
    // Receive requests for download and send data for desired
    public void run() {

        String rec;
        
        try {
            System.out.println("Reading file...");
            rec = inFromClient.readLine();
            if (rec.equals("CHECK")){
                Random rand = new Random();
                int randomNum = rand.nextInt((100) + 1);
                if (randomNum <50){
                    outToClient.writeBytes("OK\n");
                } else {
                    System.out.println("UNlucky");
                    outToClient.writeBytes("NO\n");
                }

            } else {
                System.out.println("Uploading...");

                String parts[] = rec.split(" ", 2);

                parts = parts[1].split(", ");
                FileInputStream fis = new FileInputStream(parts[3]);
                byte[] buffer = new byte[Integer.parseInt(parts[2])];
                fis.read(buffer, 0, Integer.parseInt(parts[2]));

                for (byte b : buffer) {
                    outToClient.write(b);
                }
                outToClient.flush();
                System.out.println("Finished uploading file\n");
//                    outToClient.writeBytes("\0");
                outToClient.close();
                fis.close();


            }

        } catch (Exception ex){
            ex.printStackTrace();
        } finally {
        
        }
    }
}

