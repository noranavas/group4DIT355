package sourcecodemodeler.controller;

import java.io.*;
import java.net.Socket;

public class Client {
    /*The code sends data in this order:
    1) Number of files being sent
    2) Name of the files being sent using UTF8 encoding
    3) Size of the files in bytes
    4) The files
    */


    public Socket createSocket(String IP, int PORT){
        Socket clientSocket= null;
        try {
            clientSocket = new Socket(IP,PORT);
        } catch (IOException e) {
            System.out.println("Error while creating the client socket");
        }
        return clientSocket;
    }

    public void sendFiles(String folderPath, Socket socket) {
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
        public void closeSocket(Socket socket) {
            //Closing socket
            //dos.close();
            System.out.println("Closing the socket. Transfer done");
            //close the socket when done
            try {
                socket.close();
            } catch (IOException exc2) {
                System.out.println("Error closing the socket");
            }
        }

    public static void main(String[] args) {
        System.out.println("Connecting.........");
        }

    }
