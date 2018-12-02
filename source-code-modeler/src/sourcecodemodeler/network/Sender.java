package sourcecodemodeler.network;

import sourcecodemodeler.Globals;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Sender {
    private final int PORT = Globals.PORT;
    private final String XML_DIR = Globals.PATH_TO_XML_FILES;
    private final String IP_ADDRESS = Globals.IP_ADDRESS[1];
    private Socket socket;

    //===== Constructor(s) =====//
    public Sender() {}

    //===== Getters & Setters =====//
    public Socket getSocket() {
        return socket;
    }
    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    //===== Methods =====//
    public void connect() {
        System.out.println("Creating/connecting sender socket...");
        try {
            socket = new Socket(IP_ADDRESS, PORT);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error when creating/connecting Sender class.");
        }
    }

    public void close() {
        System.out.println("Closing sender socket...");
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error when closing socket.");
        }
    }

    public void send() {
        System.out.println("Sender sending file(s)...");
        try {
            BufferedOutputStream bos = new BufferedOutputStream(socket.getOutputStream());
            System.out.print("Read:" + XML_DIR);
            bos.write(XML_DIR.getBytes());
            bos.flush();
            BufferedInputStream bis = new BufferedInputStream(socket.getInputStream());
            readZip(bis, XML_DIR);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error when sending file(s).");
        }

    }

    public static void readZip(InputStream socketIs, String outPutDirectory) throws IOException {
        ZipInputStream zips = new ZipInputStream(socketIs);
        ZipEntry zipEntry;

        while(null != (zipEntry = zips.getNextEntry())){
            String fileName = zipEntry.getName();
            File outFile = new File(outPutDirectory + "/" + fileName);
            System.out.println("----["+outFile.getName()+"], filesize["+zipEntry.getCompressedSize()+"]");

            if(zipEntry.isDirectory()){
                File zipEntryFolder = new File(zipEntry.getName());
                if(!zipEntryFolder.exists()){
                    outFile.mkdirs();
                }

                continue;
            }else{
                File parentFolder = outFile.getParentFile();
                if(!parentFolder.exists()){
                    parentFolder.mkdirs();
                }
            }

            System.out.println("ZipEntry: "+zipEntry.getCompressedSize());

            FileOutputStream fos = new FileOutputStream(outFile);
            int fileLength = (int)zipEntry.getSize();

            byte[] fileByte = new byte[fileLength];
            zips.read(fileByte);
            fos.write(fileByte);
            fos.close();
        }
    }

}
