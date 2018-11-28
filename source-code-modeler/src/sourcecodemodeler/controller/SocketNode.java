package sourcecodemodeler.controller;

import java.io.*;
import java.net.*;

public class SocketNode {

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    //===== Constructor(s) =====//
    public SocketNode() {}

    //===== Getters & Setters =====//
    public Socket getSocket() {
        return clientSocket;
    }
    public void setSocket(Socket socket) {
        this.clientSocket = socket;
    }

    //===== Methods =====//
    //----- "Sender" -----//



    public void startConnection(Socket socket) {
        try {
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            //System.out.println(input.readLine());
            System.out.println("CONNECTED.");
        } catch (IOException e) {
            System.out.println("Problem with startConnection().");
            e.printStackTrace();
        }
    }

    public void stopConnection() {
        try {
            in.close();
            out.close();
            clientSocket.close();
        } catch (IOException e) {
            System.out.println("Stopped the client connection.");
            e.printStackTrace();
        } catch  (NullPointerException e){
            System.out.println("Stopped the client connection.");
            e.printStackTrace();
        }

    }
    //This method sends all the files contained in a folder through the client socket passed as parameter
    public static void sendFiles(String folderPath, Socket socket) {
        File myFile = new File(folderPath);
        File[] Files = myFile.listFiles();

        try{
            System.out.println("Starting to send files. output folder: " + folderPath);
            //Path to the files the client is sending. Adds them in a Files array
            //output data stream on the socket
            OutputStream os = socket.getOutputStream();
            DataOutputStream dos = new DataOutputStream(os);
            System.out.println("line 75 reached");
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
            System.out.println("Files sent");

        }
        catch (IOException exOb) {
            // exception handler for ExceptionType1
            if (Files.length<=0)
                System.out.println("The folder selected is empty");
            else System.out.println("An error has occured");
        }
    }

    //----- "Receiver" -----//
    public void startServer() {
        System.out.println("Listening on port " + PublicData.PORT+ "...");
        try {
            serverSocket = new ServerSocket(PublicData.PORT);
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
            clientSocket.close();
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Stopped the server.");
        } catch (NullPointerException e){
            System.out.println("Stopped the server.");
        }
    }

    //===== Main =====//
   /* public static void main(String[] args) {
        try {
            //create the socket
            SocketNode socketNode = new SocketNode();
            //server and THEN client
            socketNode.setSocket(new Socket(IP_ADDRESSES[4], 5991));
            socketNode.startConnection(IP_ADDRESSES[4], PORT);

            socketNode.sendFiles(System.getProperty("user.dir") + "\\source-code-modeler\\resources\\converted_xml\\", socketNode.getSocket());
            System.out.println("main() done.");

            //stop client and THEN server
            socketNode.stopConnection();
            socketNode.stopServer();
        } catch (IOException e) {
             e.printStackTrace();
        }
    }*/

}
