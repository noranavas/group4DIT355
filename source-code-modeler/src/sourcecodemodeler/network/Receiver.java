package sourcecodemodeler.network;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Receiver {
    private Socket senderSocket;
    private ServerSocket receiverSocket;
    private PrintWriter printWriter;
    private InputStream inputStream;

    //===== Constructor(s) =====//
    public Receiver() {}

    public void start(int port) throws IOException {
        System.out.println("Listening on port " + port + "...");
        senderSocket = receiverSocket.accept();
        try {
            receiverSocket = new ServerSocket(port);
            while (true) {
                System.out.println("Got a connection!");
                printWriter = new PrintWriter(receiverSocket.accept().getOutputStream(), true);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error when starting receiver(socket).");
        }
    }

    public void stop() {
        try {
            printWriter.close();
            inputStream.close();
            senderSocket.close();
            receiverSocket.close();
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
            System.out.println("Error when stopping receiver(socket).");
        }
    }

    public void receiveFiles() throws IOException {
        boolean flag = true;
        int fileSize = 0;
        BufferedOutputStream bos;
        OutputStream output;
        DataOutputStream dos;
        int len;
        int smblen;
        try {
            while (true) {
                //while(true && flag==true){
                while (flag == true) {
                    //System.out.println("Got a connection!");

                    inputStream = senderSocket.getInputStream();
                    DataInputStream dataInputStream = new DataInputStream(inputStream);

                    fileSize = dataInputStream.read();

                    // Store list of filename from sender directory.
                    ArrayList<File> files = new ArrayList<>(fileSize);

                    // Store file size from sender.
                    ArrayList<Integer> sizes = new ArrayList<>(fileSize);

                    // Start to accept those filename from receiver.
                    System.out.println("Receiving " + sizes.size() + " files...");
                    for (int count = 0; count < fileSize; count++) {
                        files.add(new File(dataInputStream.readUTF()));
                    }

                    for (int count = 0; count < fileSize; count++) {
                        sizes.add(dataInputStream.readInt());
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

                        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
                        while (len > 0 && (smblen = bufferedInputStream.read(buffer)) > 0) {
                            dos.write(buffer, 0, smblen);
                            len = len - smblen;
                            dos.flush();
                        }
                        dos.close();  //It should close to avoid continue deploy by resource under view
                    }

                }

                if (!flag) {
                    senderSocket = receiverSocket.accept();
                    flag = true;
                }
            }

        // End of while(true)
        } catch(IllegalArgumentException e){
            e.printStackTrace();
            if (fileSize <= 0) {
                System.out.println("No files were sent");
            }
            System.out.println("Error when receiving files.");
            senderSocket.close();
        }
    }

    public static void main(String[] args) {
        // TODO: Auto-generated method stub. ???
        System.out.println("Waiting for a connection...");
    }

}
