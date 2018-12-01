package sourcecodemodeler.network;

import javafx.concurrent.Task;

import java.io.*;
import java.net.Socket;

public class Sender {
    /*The code sends data in this order:
    1) Number of files being sent
    2) Name of the files being sent using UTF8 encoding
    3) Size of the files in bytes
    4) The files
    */
    private static final String[] IP_ADDRESSES = {
            "10.0.30.202",
            "10.132.178.107",
            "PC3",
            "PC4",
            "localhost"
    };
    Socket senderSocket =null;

    public Sender(){
    }

    public Socket getSocket(){
        return this.senderSocket;
    }
    public void Connect(String IP, int PORT){
        System.out.println("Creating a socket to " +IP + " , " + PORT);
        try {
            senderSocket = new Socket(IP,PORT);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error while creating the client socket");
        }
        //return senderSocket;
    }

    public void sendFiles(String folderPath) throws InterruptedException {
        Task sendTask= new Task(){
            @Override
            protected Object call() throws Exception{ //start the sender and send files

        File myFile = new File(folderPath);
        File[] Files = myFile.listFiles();

        try{
        //Path to the files the client is sending. Adds them in a Files array
        //output data stream on the socket
        OutputStream os = senderSocket.getOutputStream();
        DataOutputStream dos = new DataOutputStream(os);
        //write the number of files as Integer on the data output stream
        if (Files.length <= 0) {
            senderSocket.close();
        }
        dos.writeInt(Files.length);
        System.out.println("NUM OF FILES BEING SENT " + Files.length);
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

                return null;
            }
        };
        Thread sendThread= new Thread(sendTask);
        System.out.println("Thread ID: "+sendThread.getId());
        sendThread.start();
        sendThread.join();
        sendThread.interrupt();
    }

    public void closeSocket() {
        //Closing socket
        //dos.close();
        System.out.println("Closing the socket. Transfer done");
        //close the socket when done
        try {
            senderSocket.close();
        } catch (IOException exc2) {
            System.out.println("Error closing the socket");
        }
    }

   /* public void main(String[] args) {
        System.out.println("Connecting.........");
        Socket s = createSocket(IP_ADDRESSES[4], 5991);
        sendFiles(System.getProperty("user.dir") + "\\source-code-modeler\\resources\\converted_xml\\");
        closeSocket(s);
    }*/

}
