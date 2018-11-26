package sourcecodemodeler.controller;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketNode {
    private static final int PORT = 5991;
    private static final String[] IP_ADDRESSES = {
            "10.0.30.202",
            "10.132.178.107",
            "PC3",
            "PC4",
            "127.0.0.1"
    };
    private ServerSocket serverSocket;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    //===== Constructor(s) =====//
    public SocketNode() {}

    //===== Getters & Setters =====//
    public Socket getSocket() {
        return socket;
    }
    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    //===== Methods =====//
    //----- "Client" -----//
    public void startConnection(String ip, int port) {
        try {
            Socket socket = new Socket(InetAddress.getByName(ip), port);
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            //System.out.println(input.readLine());
            System.out.println("startConnection() done.");
        } catch (IOException e) {
            System.out.println("Problem with startConnection().");
            e.printStackTrace();
        }
    }

    public void stopConnection() {
        try {
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sendFiles(String folderPath, Socket socket) {
        File myFile = new File(folderPath);
        File[] Files = myFile.listFiles();

        try{
            //Path to the files the client is sending. Adds them in a Files array
            //output data stream on the socket
            OutputStream os = socket.getOutputStream();
            DataOutputStream dos = new DataOutputStream(os);
            //write the number of files as Integer on the data output stream
            if (Files.length <= 0) {
                socket.close();
            }
            dos.writeInt(Files.length);
            System.out.println("NUM OF FILES " + Files.length);
            //name of the files in utf8
            for (int count = 0; count < Files.length; count++) {
                dos.writeUTF(Files[count].getName());
            }
            //size of the files
            for (int count = 0; count < Files.length; count++) {
                int filesize = (int) Files[count].length();
                dos.writeInt(filesize);
            }
            //allocates an array of bytes with the size of each file and sends it via buffer
            for (int count = 0; count < Files.length; count++) {
                int filesize = (int) Files[count].length();
                byte[] buffer = new byte[filesize];

                //FileInputStream fis = new FileInputStream(myFile);
                FileInputStream fis = new FileInputStream(Files[count].toString());
                BufferedInputStream bis = new BufferedInputStream(fis);

                //Sending file name and file size to the server
                bis.read(buffer, 0, buffer.length); //This line is important

                dos.write(buffer, 0, buffer.length);
                dos.flush();
            }

        }
        catch (IOException exOb) {
            // exception handler for ExceptionType1
            if (Files.length<=0)
                System.out.println("The folder selected is empty");
            else System.out.println("An error has occured");
        }
    }

    //----- "Server" -----//
    public void startServer() {
        System.out.println("Listening on port " + PORT + "...");
        try {
            serverSocket = new ServerSocket(PORT);
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Got a connection!");
                try {
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    System.out.println("Hello from the server socket.");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopServer() {
        try {
            in.close();
            out.close();
            socket.close();
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //===== Main =====//
    public static void main(String[] args) {
        try {
            SocketNode socketNode = new SocketNode();
            socketNode.startConnection(IP_ADDRESSES[4], PORT);
            socketNode.setSocket(new Socket(IP_ADDRESSES[4], 5991));
            socketNode.sendFiles(System.getProperty("user.dir") + "\\source-code-modeler\\resources\\converted_xml\\", socketNode.getSocket());
            System.out.println("main() done.");
        } catch (IOException e) {
             e.printStackTrace();
        }
    }

}
