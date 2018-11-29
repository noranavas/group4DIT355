package sourcecodemodeler.network;

import java.io.*;
import java.net.Socket;

public class Sender {
    private Socket senderSocket;

    // ===== Constructor(s) =====//
    public Sender() {}

    //===== Getters & Setters =====//
    public Socket getSocket(){
        return this.senderSocket;
    }

    //===== Methods =====//
    public void connect(String ip, int port){
        System.out.println("Creating a socket to " + ip + " , " + port);
        try {
            senderSocket = new Socket(ip , port);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error creating/connecting sender socket.");
        }
    }

    public void close() {
        try {
            senderSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error closing sender socket.");
        }
    }

    public void sendFiles(String folderPath) {
        File[] files = new File(folderPath).listFiles();
        try {
            DataOutputStream dos = new DataOutputStream(this.senderSocket.getOutputStream());

            // Write the number of files as Integer on the data output stream
            if (files.length <= 0) {
                senderSocket.close();
            }

            dos.writeInt(files.length);

            System.out.println("Files being sent: " + files.length);

            // Name of the files in utf8 and Size of the files
            for (int count = 0; count < files.length; count++) {
                dos.writeUTF(files[count].getName());
                dos.writeInt((int)files[count].length());
            }

            // Allocates an array of bytes with the size of each file and sends it via buffer
            for (int count = 0; count < files.length; count++) {
                byte[] buffer = new byte[(int) files[count].length()];

                FileInputStream fis = new FileInputStream(files[count].toString());
                BufferedInputStream bis = new BufferedInputStream(fis);

                // Sending file name and file size to the server
                bis.read(buffer, 0, buffer.length); // This line is important

                dos.write(buffer, 0, buffer.length);
                dos.flush();
            }

        }
        catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error sending files.");
        }
    }

}
