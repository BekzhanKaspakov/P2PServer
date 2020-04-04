package client;

import server.WorkerRunnable;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import javax.swing.*;

import static java.lang.System.exit;
import static java.lang.System.out;
import static java.lang.System.setOut;

public class ExampleGUI extends JFrame implements ActionListener{
    private JButton search;  //Buttons
    private JButton dload;
    private JButton close;
    final JFileChooser fc;
    int returnVal;
    private Socket clientSocket;
    private DataOutputStream outToServer;
    private BufferedReader inFromServer;
    private ServerSocket cServerSocket;

    private JList jl;   // List that will show found files
    private JLabel label; //Label "File Name
    private JTextField tf,tf2; // Two textfields: one is for typing a file name, the other is just to show the selected file
    DefaultListModel listModel; // Used to select items in the list of found files


    public ExampleGUI(ServerSocket cServerSocket,
                      Socket clientSocket,
                      DataOutputStream outToServer,
                      BufferedReader inFromServer){
        super("Example GUI");
        setLayout(null);
        setSize(500,600);
        try {
            String send;
            this.cServerSocket = cServerSocket;
            this.clientSocket = clientSocket;

            this.outToServer = outToServer;
            this.inFromServer = inFromServer;

        } catch (Exception ex){
            ex.printStackTrace();
        }
        //Create a file chooser
        fc = new JFileChooser();

        //In response to a button click:
        //returnVal = fc.showOpenDialog(aComwponent);

        label=new JLabel("File name:");
        label.setBounds(50,50, 80,20);
        add(label);

        tf=new JTextField();
        tf.setBounds(130,50, 220,20);
        add(tf);

        search=new JButton("Search");
        search.setBounds(360,50,80,20);
        search.addActionListener(this);
        add(search);

        listModel = new DefaultListModel();
        jl=new JList(listModel);

        JScrollPane listScroller = new JScrollPane(jl);
        listScroller.setBounds(50, 80,300,300);

        add(listScroller);

        dload=new JButton("Download");
        dload.setBounds(200,400,130,20);
        dload.addActionListener(this);
        add(dload);

        tf2=new JTextField();
        tf2.setBounds(200,430,130,20);
        add(tf2);

        close=new JButton("Close");
        close.setBounds(360,470,80,20);
        close.addActionListener(this);
        add(close);

        setVisible(true);
    }
    public void actionPerformed(ActionEvent e)
    {
        String rec;
        try {

            if (e.getSource() == search) { //If search button is pressed show 25 randomly generated file info in text area
                listModel.clear();
                String fileName = tf.getText();
                //send = inFromUser.readLine();
                outToServer.writeBytes("SEARCH: " + fileName + '\n');
                rec = inFromServer.readLine();
                System.out.println("Received " +rec + "\n");
                String parts[];
                if (rec.equals("FOUND:")){
                    System.out.println("FOUND\n");
                    for (int i = 0;;i++) {
                        rec = inFromServer.readLine();
                        if (rec.equals("end")){
                            break;
                        } else{
                            parts = rec.split(", ");
                            int a = 0;
                            if (Integer.parseInt(parts[6])!=0) {
                                a = Integer.parseInt(parts[7]) *100/ Integer.parseInt(parts[6]) ;

                            }
                            listModel.insertElementAt(fileName+ ": "+parts[0]+ ", "+parts[1]+ ", "+parts[2]+ ", "+parts[3]+ ", "+parts[4]+", "+parts[5]+", "+ a+"%", i);
                        }
                    }
                } else if (rec.equals("NOT FOUND")){
                    listModel.insertElementAt("NOT FOUND", 0);
                }

            } else if (e.getSource() == dload) {   //If download button is pressed get the selected value from the list and show it in text field
                String asd = jl.getSelectedValue().toString();
                System.out.println(asd+"\n");
                String parts[] = asd.split(": ", 2);
                String filename = parts[0];
                parts = parts[1].split(", ");
                Socket connection1 = new Socket(parts[4], Integer.parseInt(parts[5]));

                outToServer.writeBytes("REQUEST: " + parts[4] + ", " + parts[5]+"\n");

                DataOutputStream toClient1 = new DataOutputStream(connection1.getOutputStream());
                toClient1.writeBytes("CHECK\n");
                BufferedReader in = new BufferedReader(new InputStreamReader(connection1.getInputStream()));
                String message = in.readLine();
                if (message.equals("NO")){
                    tf2.setText("Failed to download");

                } else{
                    connection1.close();
                    tf2.setText("Downloading...");
                    Socket connection = new Socket(parts[4], Integer.parseInt(parts[5]));

                    DataOutputStream toClient = new DataOutputStream(connection.getOutputStream());
                    toClient.writeBytes("DOWNLOAD: "+filename+", "+parts[0]+", "+parts[2] + ", "+parts[1] +"\n");

                    DataInputStream dis = new DataInputStream(connection.getInputStream());


                    int filesize = Integer.parseInt(parts[2]);
                    byte[] buffer = new byte[filesize];

                    for (int i = 0; i<filesize; i++) {
                        buffer[i] = (byte) dis.read();

                    }
                    FileOutputStream fos = new FileOutputStream(new File(filename+"."+parts[0]));
                    fos.write(buffer, 0, filesize);
                    outToServer.writeBytes("UPLOAD: " + parts[4] + ", " + parts[5]+"\n");
                    tf2.setText(jl.getSelectedValue().toString() + " downloaded");

                }



            } else if (e.getSource() == close) { //If close button is pressed exit
                String send = "LEAVE: " + cServerSocket.getInetAddress();
                System.out.println(send+"\n");;
                outToServer.writeBytes("LEAVE: " + getIPAddress() + "\n");
                exit(0);
            }
        } catch (Exception ex){
            ex.printStackTrace();
        }

    }
    public static String getIPAddress() {
        String ip = "";
        try {
            Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
            for (NetworkInterface netint : Collections.list(nets)){
                if (netint.isLoopback() || !netint.isUp()) {
                    continue;
                }
                Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
                for (InetAddress inetAddress : Collections.list(inetAddresses)) {
                    ip = inetAddress.getHostAddress();
                }
            }
        } catch (SocketException e) {
            System.out.println(e.getMessage());
        }
        return ip;
    }
    public static void main(String[] args) throws Exception{


        ServerSocket cServerSocket = new ServerSocket(0);
        System.out.println("listening on port: " + cServerSocket.getLocalPort() + " and " + getIPAddress());
        TestFrame ex = new TestFrame( cServerSocket);
        ex.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Close the window if x button is pressed

        while(true) {
            Socket clientSocket = null;
            try {
                clientSocket = cServerSocket.accept();
            } catch (IOException e) {
                System.out.println("Error accepting client.");
            }
            new Thread(new client.WorkerRunnable(clientSocket, cServerSocket)).start();
        }

    }
}