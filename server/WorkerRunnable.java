package server;



import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;


public class WorkerRunnable extends Thread {

    protected Socket clientSocket = null;
    private Hashtable<String, List<someFile>> SharedFiles;
    User user;
    DataOutputStream outToClient;
    BufferedReader inFromClient;
    Hashtable<String , Score> scores;

    boolean greet = false;


    public WorkerRunnable(Socket clientSocket, Hashtable<String, List<someFile>> SharedFiles,Hashtable<String, Score> scores) throws Exception {
        this.clientSocket = clientSocket;
        this.SharedFiles = SharedFiles;

        user = new User(clientSocket.getPort(),clientSocket.getInetAddress().getHostAddress());
        this.scores = scores;

        outToClient = new DataOutputStream(clientSocket.getOutputStream());
        inFromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        String clientSentence;
        clientSentence = inFromClient.readLine();
        if (clientSentence.equals("HELLO")) {
            outToClient.writeBytes("HI\n");
            System.out.println("Successful greet\n");
            greet = true;
        }
        else
        {
            outToClient.writeBytes("WTF\n");
        }
    }

    public void run() {

        try {
                        //clientOutputStreams.add(outToClient);
            while (true) {
                String clientSentence;

                if ((clientSentence = inFromClient.readLine()) == null) {
                    System.out.println("Client gone");
                    break;
                } else {
                    System.out.println("Check of the user with IP = " + user.getIP() + ", Port = " + user.getPort()+",  Here = " +user.getCheck());
                    if (user.getCheck())
                    {
                        String[] parts = clientSentence.split(" ", 2);
                        System.out.println("Received: " + clientSentence);

                        if (parts[0].equals("SEARCH:"))
                        {
                            System.out.println("SEARCHING\n");

                            if (SharedFiles.containsKey(parts[1]))
                            {
                                System.out.println("FOUND\n");
                                List<someFile> temp;
                                temp = SharedFiles.get(parts[1]);
                                outToClient.writeBytes("FOUND:\n" );
                                for (int i = 0; i < temp.size(); i++)
                                {
                                    if (temp.get(i)!=null){
                                        someFile t = temp.get(i);
                                        User k = new User(t.getPort(), t.getIP());
                                        k.setCheck(true);
                                        String mess = t.getAll() + ", "+ scores.get(t.getIP()).getNumRequests()+ ", "+ scores.get(t.getIP()).getNumUploads() + "\n";
                                        System.out.println(mess);
                                        outToClient.writeBytes( mess);
                                    }

                                }
                                outToClient.writeBytes("end\n");
                            }
                            else
                            {
                                outToClient.writeBytes("NOT FOUND\n");
                            }
                        }
                        else if (parts[0].equals("REQUEST:"))
                        {
                            parts = parts[1].split(", ");
                            User temp = new User(Integer.parseInt(parts[1]), parts[0]);
                            scores.get(parts[0]).RequestsInc();
                        }
                        else if (parts[0].equals("UPLOAD:")){
                            System.out.println(clientSentence);
                            System.out.println("UPLOAD received");
                            parts = parts[1].split(", ");
                            User temp = new User(Integer.parseInt(parts[1]), parts[0]);

                            scores.get(parts[0]).UploadsInc();
                        }
                        else if (parts[0].equals("LEAVE:")) {
                            
                            Set<String> keys = SharedFiles.keySet();
                            Iterator<String> keysIter = keys.iterator();
                            while(keysIter.hasNext()){
                                String key = keysIter.next();
                                System.out.println("Value of "+key+" is: "+SharedFiles.get(key));
                                Iterator<someFile> tempIterator = SharedFiles.get(key).iterator();
                                while (tempIterator.hasNext()) {
                                    someFile temp = tempIterator.next();
                                    if (temp.getIP().equals(parts[1])) {
                                        tempIterator.remove();
                                        if (!tempIterator.hasNext()) {
                                            keysIter.remove();
                                        }
                                    }
                                }
                            }

                        }
                    }
                    else
                    {

                        if (greet)
                        {
                            String[] parts;
                            do {
                                parts = clientSentence.split(", ");
                                System.out.println(clientSentence+"\n");

                                DateFormat format = new SimpleDateFormat("EEE MMM dd hh:mm:ss z yyyy", Locale.ENGLISH);
                                Date date = format.parse(parts[4]);
                                someFile file = new someFile(parts[1], parts[2], Integer.parseInt(parts[3]), date, parts[5], Integer.parseInt(parts[6]));
                                if (SharedFiles.contains(parts[0])) {
                                    if (!SharedFiles.get(parts[0]).contains(file))
                                        SharedFiles.get(parts[0]).add(file);
                                } else {
                                    ArrayList<someFile> temp = new ArrayList<>();
                                    temp.add(file);
                                    SharedFiles.put(parts[0], temp);
                                }
                            } while (!(clientSentence = inFromClient.readLine()).equals("END"));

                            boolean check = false;

                            user.setCheck(true);
                            user.setPort(Integer.parseInt(parts[6]));
                            user.setIP(parts[5]);

                            if (!scores.containsKey(parts[5])) {
                                scores.put(parts[5], new Score());
                            }
                            System.out.println(scores);

                        }


                    }
                }
            }
            clientSocket.close();
        } catch (Exception ex){
            ex.printStackTrace();
        }


    }

}