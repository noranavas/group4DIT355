package sourcecodemodeler.network;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Consumer;

public abstract class NetworkConnection {
    private ConnectionThread connectionThread = new ConnectionThread();
    private Consumer<Serializable> onReceiveCallback;

    public NetworkConnection(Consumer<Serializable> onReceiveCallback) {
        this.onReceiveCallback = onReceiveCallback;
        connectionThread.setDaemon(true); // Prevents blocking exiting from JBM. (?)
    }

    public NetworkConnection() {}

    public void startConnection() throws Exception {
        String s = isReceiver() ? "Receiver" : "Sender";
        System.out.println(s + " started.");
        try {
            connectionThread.start();
        } catch (IllegalThreadStateException e) {
            System.out.println("IllegalThreadStateException");
        }
    }

    public void send(Serializable data) throws Exception {
        System.out.println("Sending data...");
        connectionThread.out.writeObject(data);
    }

    public void closeConnection() throws Exception {
        String s = isReceiver() ? "Receiver" : "Sender";
        System.out.println(s + " closed.");
        try {
            connectionThread.socket.close();
        } catch (Exception e) {
            System.out.println("Exception when closing connection.");
        }
    }

    protected abstract boolean isReceiver();
    protected abstract String getIP();
    protected abstract int getPort();

    private class ConnectionThread extends Thread {
        private Socket socket;
        private ObjectOutputStream out;


        @Override
        public void run() {
            try (ServerSocket serverSocket = isReceiver() ? new ServerSocket(getPort()) : null;
                 Socket socket = isReceiver() ? serverSocket.accept() : new Socket(getIP(), getPort());
                 ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                 ObjectInputStream in = new ObjectInputStream(socket.getInputStream()))
            {
                this.socket = socket;
                this.out = out;
                socket.setTcpNoDelay(true); // Allows faster sending of messages.

                while (true) {
                    Serializable data = (Serializable) in.readObject();
                    onReceiveCallback.accept(data);
                    //if(socket.isConnected()) System.out.println("Socket connected");
                }
            } catch (Exception e) {
                //onReceiveCallback.accept("Connection closed.");
                System.out.println("no receiver found retrying in 10s");
                run();
            }
        }
    }

}
