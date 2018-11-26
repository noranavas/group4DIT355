package sourcecodemodeler.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {

    public static void main(String[] args) throws IOException,EOFException {
        // TODO Auto-generated method stub
        System.out.println("Starting...");
        System.out.println("Waiting for a connection.");

        BufferedOutputStream bos;
        OutputStream output;
        DataOutputStream dos;
        int len;
        int smblen;
        InputStream in;
        boolean flag=true;
        DataInputStream clientData;
        BufferedInputStream clientBuff;
        //System.out.println(System.getProperty("user.dir")+ "\\resources"); DEBUG
        ServerSocket serverSocket = new ServerSocket(5991);
        Socket clientSocket = null;
        clientSocket = serverSocket.accept();
        int fileSize=0;

        try{
            while (true) {
                //while(true && flag==true){
                while (flag == true) {
                    System.out.println("Got a connection!");

                    in = clientSocket.getInputStream(); //used
                    clientData = new DataInputStream(in); //use
                    clientBuff = new BufferedInputStream(in); //use

                    fileSize = clientData.read();

                    ArrayList<File> files = new ArrayList<File>(fileSize); //store list of filename from client directory
                    ArrayList<Integer> sizes = new ArrayList<Integer>(fileSize); //store file size from client
                    //Start to accept those filename from server
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
                    clientSocket = serverSocket.accept();
                    flag = true;
                }
            }


        } //end of while(true)
        catch(IllegalArgumentException exc1){
            if (fileSize<=0)
                System.out.println("No files were sent");
            clientSocket.close();
        }
    }

}