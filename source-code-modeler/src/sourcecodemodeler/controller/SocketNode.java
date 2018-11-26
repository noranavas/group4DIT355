package sourcecodemodeler.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class SocketNode {
    private static final int PORT = 5991;
    private static final String ip = "d";
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    //===== Constructor(s) =====//
    public SocketNode() {}

    public SocketNode(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public void startConnection(String ip, int port) {
        try {
            Socket socket = new Socket(InetAddress.getByName(ip), PORT);
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            System.out.println(input.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopConnection() {
        try {
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String sendMessage(String msg) {
        out.println(msg);
        String resp = "";
        try {
            resp = in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resp;
    }

    //===== Main =====//
    public static void main(String[] args) {
        SocketNode sn = new SocketNode();
        sn.startConnection(IP_ADDRESS, PORT);
    }


}
