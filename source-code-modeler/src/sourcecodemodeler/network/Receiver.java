package sourcecodemodeler.network;

import sourcecodemodeler.Globals;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Receiver {
    BufferedOutputStream bos;
    OutputStream output;
    DataOutputStream dos;
    int len;
    int smblen, filesize;
    InputStream in;
    boolean flag=true;
    DataInputStream clientData;
    BufferedInputStream clientBuff;
    Socket senderSocket = null;
    ServerSocket receiverSocket;
    PrintWriter out=null;
    private int fileSize;


    public Receiver() throws IOException {
    }

    public void startServer() throws IOException {
        senderSocket = receiverSocket.accept();
        fileSize=0;
        System.out.println("Listening on port " + Globals.PORT + "...");
        try {
            receiverSocket = new ServerSocket(Globals.PORT);
            while (true) {
                Socket socket = receiverSocket.accept();
                System.out.println("Got a connection!");
                try {
                    out = new PrintWriter(socket.getOutputStream(), true);
                    System.out.println("Hello from the server socket.");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void receiveFiles() throws IOException {
        try{
            while (true) {
                //while(true && flag==true){
                while (flag == true) {
                    //System.out.println("Got a connection!");

                    in = senderSocket.getInputStream(); //used
                    clientData = new DataInputStream(in); //use
                    clientBuff = new BufferedInputStream(in); //use

                    fileSize = clientData.read();

                    ArrayList<File> files = new ArrayList<File>(fileSize); //store list of filename from client directory
                    ArrayList<Integer> sizes = new ArrayList<Integer>(fileSize); //store file size from client
                    //Start to accept those filename from server
                    System.out.println("Receiving " + sizes.size() + " files");
                    for (int count = 0; count < fileSize; count++) {
                        File ff = new File(clientData.readUTF());
                        files.add(ff);
                    }

                    for (int count = 0; count < fileSize; count++) {

                        sizes.add(clientData.readInt());
                    }

                    for (int count = 0; count < fileSize; count++) {

                        if (fileSize - count == 1) {
                            flag = false;
                        }

                        len = sizes.get(count);

                        System.out.println("File Size =" + len);

                        output = new FileOutputStream(System.getProperty("user.dir") + "\\source-code-modeler\\resources\\converted_xml\\" + files.get(count));
                        dos = new DataOutputStream(output);
                        bos = new BufferedOutputStream(output);

                        byte[] buffer = new byte[1024];

                        bos.write(buffer, 0, buffer.length); //This line is important

                        while (len > 0 && (smblen = clientBuff.read(buffer)) > 0) {
                            dos.write(buffer, 0, smblen);
                            len = len - smblen;
                            dos.flush();
                        }
                        dos.close();  //It should close to avoid continue deploy by resource under view
                    }

                }

                if (flag == false) {
                    senderSocket = receiverSocket.accept();
                    flag = true;
                }
            }


        } //end of while(true)
        catch(IllegalArgumentException exc1){
            if (fileSize<=0)
                System.out.println("No files were sent");
            senderSocket.close();
        }
    }

    public void stopServer() {
        try {
            in.close();
            out.close();
            senderSocket.close();
            receiverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Stopped the server.");
        } catch (NullPointerException e){
            System.out.println("Stopped the server.");
        }
    }

    public static void main(String[] args) throws IOException,EOFException {
        // TODO Auto-generated method stub
        System.out.println("Starting...");
        System.out.println("Waiting for a connection.");


        //System.out.println(System.getProperty("user.dir")+ "\\resources"); DEBUG
    }

}