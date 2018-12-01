package sourcecodemodeler.controller;

import sourcecodemodeler.Globals;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class SendFileSocketClient {

    public static void main(String[] args) throws FileNotFoundException,
            IOException {

        String outDir = Globals.PATH_TO_XML_FILES;

        String readDir = "C:\\Users\\x220\\Desktop\\olio";
        System.out.print("Connectiong");
        Socket socket = new Socket("localhost", 50000);
        BufferedOutputStream bos = new BufferedOutputStream(socket.getOutputStream());
        System.out.print("Read::"+readDir);
        bos.write(readDir.getBytes());
        bos.flush();
        BufferedInputStream bis = new BufferedInputStream(socket.getInputStream());
        readZip(bis,outDir);
        //socket.close();

    }

    public static void readZip(InputStream socketIs,String outPutDirectory) throws IOException{
        ZipInputStream zips = new ZipInputStream(socketIs);
        ZipEntry zipEntry = null;


        while(null != (zipEntry = zips.getNextEntry())){
            String fileName = zipEntry.getName();
            File outFile = new File(outPutDirectory + "/" + fileName);
            System.out.println("----["+outFile.getName()+"], filesize["+zipEntry.getCompressedSize()+"]");


            if(zipEntry.isDirectory()){
                File zipEntryFolder = new File(zipEntry.getName());
                /*if(zipEntryFolder.exists() == false){
                    outFile.mkdirs();
                }*/

                continue;
            }else{
                File parentFolder = outFile.getParentFile();
                if(parentFolder.exists() == false){
                    parentFolder.mkdirs();
                }
            }

            System.out.println("ZipEntry::"+zipEntry.getCompressedSize());



            FileOutputStream fos = new FileOutputStream(outFile);
            int fileLength = (int)zipEntry.getSize();

            byte[] fileByte = new byte[fileLength];
            zips.read(fileByte);
            fos.write(fileByte);
            fos.close();
        }
    }

}
