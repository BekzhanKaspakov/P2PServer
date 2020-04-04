package client;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.util.*;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import static client.ExampleGUI.getIPAddress;
import static java.lang.System.exit;
import static java.lang.System.out;

public class TestFrame extends JFrame {

    ExampleGUI ex;
    ServerSocket cServerSocket;
    Socket clientSocket;
    DataOutputStream outToServer;
    BufferedReader inFromServer;
    ArrayList<File> files;

    public TestFrame( ServerSocket cServerSocket) {
        super("UPLOAD YOUR FILES");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.cServerSocket = cServerSocket;
        this.ex = ex;
        files = new ArrayList<>();
        try {
            String send;
            this.cServerSocket = cServerSocket;
            this.clientSocket = new Socket("localhost", 8080);

            this.outToServer = new DataOutputStream(clientSocket.getOutputStream());
            this.inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            outToServer.writeBytes("HELLO\n");
            String rec;
            rec = inFromServer.readLine();
            System.out.println(rec + "\n");
            if (!rec.equals("HI"))
                exit(-1);

        } catch (Exception ex){
            ex.printStackTrace();
        }
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.add(Box.createVerticalGlue());

        final JLabel label = new JLabel("Chosen file");
        label.setAlignmentX(CENTER_ALIGNMENT);
        panel.add(label);

        panel.add(Box.createRigidArea(new Dimension(10, 10)));

        JButton button = new JButton("Choose files...");
        button.setAlignmentX(CENTER_ALIGNMENT);

        button.addActionListener(e -> {

            JFileChooser fileopen = new JFileChooser();
            fileopen.setMultiSelectionEnabled(true);
            int ret = fileopen.showDialog(null, "Open file");
            if (ret == JFileChooser.APPROVE_OPTION) {
                files = new ArrayList<>(Arrays.asList(fileopen.getSelectedFiles()));
                String send;
                for (File i : files) {
                    label.setText(i.getName());
                    try {
                        String parts[] = i.getName().split("\\.(?=[^\\.]+$)");
                        Date d = new Date(i.lastModified());
                        send = parts[0]+ ", " + parts[1]+ ", "+ i.getAbsolutePath() + ", "+ i.length()+ ", " +d+", " + getIPAddress()+", " + cServerSocket.getLocalPort();
                        System.out.println(send+"\n");
                        outToServer.writeBytes(send + '\n');
                    } catch (Exception ex){
                        ex.printStackTrace();
                    }
                }
                try {
                    send = "END";
                    System.out.println(send+"\n");
                    outToServer.writeBytes(send + '\n');
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
    
            }


            setVisible(false);
            ex = new ExampleGUI(cServerSocket, clientSocket, outToServer, inFromServer);
        });

        panel.add(button);
        panel.add(Box.createVerticalGlue());
        getContentPane().add(panel);

        setPreferredSize(new Dimension(260, 220));
        pack();
        setLocationRelativeTo(null);
        setVisible(true);


    }
}


