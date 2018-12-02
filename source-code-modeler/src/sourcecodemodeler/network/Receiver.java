package sourcecodemodeler.network;

import sourcecodemodeler.Globals;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Receiver extends Thread {
    private static final int MAX_READ_SIZE = 1024;
    private static final int PORT = Globals.PORT;
    private Socket socket;

    //===== Constructor(s) =====//
    public Receiver(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try {
            System.out.println("Connected");
            BufferedInputStream bis = new BufferedInputStream(socket.getInputStream());
            byte[] bytesToRead = new byte[MAX_READ_SIZE];
            String copyFolder = "";
            System.out.println("Reading");
            int readLength = 0;
            while (0 != (readLength = bis.read(bytesToRead))) {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bos.write(bytesToRead, 0, readLength);
                System.out.println("Reading::"+new String(bos.toByteArray()));
                copyFolder += new String(bos.toByteArray());
                if (readLength < MAX_READ_SIZE) {
                    break;
                }
            }

            File readFile = new File(copyFolder);
            if (readFile.exists()) {
                System.out.println("Reading Folder::" + copyFolder);
                ZipOutputStream zipOpStream = new ZipOutputStream(
                        socket.getOutputStream());
                sendFileOutput(zipOpStream, readFile);
                zipOpStream.flush();
                System.out.println("zipOpStream Flush");

            } else {
                System.out.println("Folder to read does not exist::["+readFile.getAbsolutePath()+"]");
            }
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Problem running Receiver.");
        }
    }

    public void sendFileOutput(ZipOutputStream zipOpStream, File outFile) throws Exception {
        String relativePath = outFile.getAbsoluteFile().getParentFile().getAbsolutePath();
        System.out.println("relativePath[" + relativePath + "]");
        outFile = outFile.getAbsoluteFile();
        if (outFile.isDirectory()) {
            sendFolder(zipOpStream, outFile, relativePath);
        } else {
            sendFolder(zipOpStream, outFile, relativePath);
        }
    }

    public void sendFolder(ZipOutputStream zipOpStream, File folder, String relativePath) throws Exception {
        File[] filesList = folder.listFiles();
        for (File file : filesList) {
            if (file.isDirectory()) {
                sendFolder(zipOpStream, file, relativePath);
            } else {
                sendFile(zipOpStream, file, relativePath);
            }
        }
    }

    public void sendFile(ZipOutputStream zipOpStream, File file, String relativePath) throws Exception {
        String absolutePath = file.getAbsolutePath();
        String zipEntryFileName = absolutePath;
        int index = absolutePath.indexOf(relativePath);
        if (absolutePath.startsWith(relativePath)) {
            zipEntryFileName = absolutePath.substring(relativePath.length());
            if (zipEntryFileName.startsWith(File.separator)) {
                zipEntryFileName = zipEntryFileName.substring(1);
            }
            System.out.println("zipEntryFileName:::"+relativePath.length()+"::"+zipEntryFileName);
        } else {
            throw new Exception("Invalid Absolute Path");
        }
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
        byte[] fileByte = new byte[MAX_READ_SIZE];
        int readBytes = 0;
        CRC32 crc = new CRC32();
        while (0 != (readBytes = bis.read(fileByte))) {
            if(-1 == readBytes){
                break;
            }
            //System.out.println("length::"+readBytes);
            crc.update(fileByte, 0, readBytes);
        }
        bis.close();
        ZipEntry zipEntry = new ZipEntry(zipEntryFileName);
        zipEntry.setMethod(ZipEntry.STORED);
        zipEntry.setCompressedSize(file.length());
        zipEntry.setSize(file.length());
        zipEntry.setCrc(crc.getValue());
        zipOpStream.putNextEntry(zipEntry);
        bis = new BufferedInputStream(new FileInputStream(file));
        //System.out.println("zipEntryFileName::"+zipEntryFileName);
        while (0 != (readBytes = bis.read(fileByte))) {
            if (-1 == readBytes) {
                break;
            }
            zipOpStream.write(fileByte, 0, readBytes);
        }
        bis.close();
    }

    //===== Main =====//
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            while (true) {
                new Receiver(serverSocket.accept()).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}